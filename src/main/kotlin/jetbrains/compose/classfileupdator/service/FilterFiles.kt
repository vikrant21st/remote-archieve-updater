package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.AnyFile
import jetbrains.compose.classfileupdator.model.ClassFile
import jetbrains.compose.classfileupdator.model.anyFile
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

fun filterFiles(
    allFiles: () -> List<AnyFile>,
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
        }.getOrNull()

        val isKeywordCamelCase = keyword.isCamelCased()

        allFiles().filter {
            with(it) {
                notAnInnerClass &&
                        (keywordMatched(keyword) ||
                                hasAnArbitraryMatch(regex, isKeywordCamelCase))
            }
        }
    }

private fun AnyFile.keywordMatched(keyword: String) =
    fullFilePathInArchive.contains(keyword, ignoreCase = true)

private fun AnyFile.hasAnArbitraryMatch(
    regex: Regex?,
    keywordIsCamelCase: Boolean
) =
    keywordIsCamelCase && (regex?.matches(fileName()) ?: false)

internal fun getAllFilesIn(baseDirectory: String): List<AnyFile> {
    val fileList = mutableListOf<AnyFile>()

    val basDirName =
        if (baseDirectory.endsWith('\\')) baseDirectory
        else baseDirectory + '\\'

    Files.walkFileTree(
        Path.of(basDirName),
        object : SimpleFileVisitor<Path>() {
            override fun visitFile(
                file: Path?,
                attrs: BasicFileAttributes?
            ): FileVisitResult {
                if (file != null) {
                    val filePath = file.toAbsolutePath().toString()
                    fileList.add(anyFile(filePath, basDirName))
                }
                return FileVisitResult.CONTINUE
            }
        })
    return fileList.sortedBy { it.fullFilePathInArchive }
}

internal fun String.isCamelCased() =
    when {
        firstOrNull()?.isUpperCase() == true -> drop(1).indexOfFirst { it.isLowerCase() } != -1
        firstOrNull()?.isLowerCase() == true -> drop(1).indexOfFirst { it.isUpperCase() } != -1
        else -> false
    }

internal val AnyFile.notAnInnerClass
    get() = this !is ClassFile || !this.className.contains('$')

