package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.ClassFile
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

fun filterClasses(
    allFiles: () -> List<ClassFile>,
    keyword: String
) =
    if (keyword.contains(Regex("\\s+")) || keyword.length < 2) {
        emptyList()
    } else {
        val regex = runCatching {
            // in case of regex related symbols in `keyword`, regex creation fails and UI goes in
            // unresponsive mode
            val keywordExpanded = keyword.toCharArray().joinToString(".*")
            Regex(pattern = ".*$keywordExpanded.*")
        }
        if (regex.isFailure)
            emptyList()
        else
            allFiles().filter { classFile ->
                !classFile.className.contains('$') &&
                        (classFile.className.contains(keyword, ignoreCase = true) ||
                                classNameArbitraryMatch(regex.getOrNull()!!, classFile.className))
            }
    }

private fun classNameArbitraryMatch(regex: Regex, classname: String) =
    regex.matches(classname.drop(classname.lastIndexOf('.') + 1))

fun getClassFilesIn(baseDirectory: String): List<ClassFile> {
    val fileList = mutableListOf<ClassFile>()

    val basDirName =
        if (baseDirectory.endsWith('\\')) baseDirectory
        else baseDirectory + '\\'

    Files.walkFileTree(
        Path.of(basDirName),
        object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                val filePath = file?.toAbsolutePath()?.toString() ?: ""
                if (filePath.endsWith(".class")) {
                    fileList.add(ClassFile(filePath, basDirName))
                }
                return FileVisitResult.CONTINUE
            }
        })
    return fileList.sortedBy { it.className }
}