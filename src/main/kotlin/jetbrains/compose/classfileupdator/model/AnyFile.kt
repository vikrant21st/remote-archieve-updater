package jetbrains.compose.classfileupdator.model

sealed class AnyFile(val path: String, baseDirectory: String) {
    val fullFilePathInArchive by lazy {
        path.removePrefix(baseDirectory).replace('\\', '/')
    }

    fun fileName() = fullFilePathInArchive.drop(fullFilePathInArchive.lastIndexOf('/') + 1)

    fun fileLocationInArchive() = fullFilePathInArchive.take(fullFilePathInArchive.lastIndexOf('/'))

    fun fileToLocation() = fileName() to fileLocationInArchive()

    override fun toString() = path
}

class NonClassFile(path: String, baseDirectory: String) : AnyFile(path, baseDirectory)

class ClassFile internal constructor(path: String, baseDirectory: String) : AnyFile(path, baseDirectory) {
    val className = fullFilePathInArchive.removeSuffix(".class").replace('/', '.')

    val decoratedClassName by lazy {
        className.lastIndexOf('.').let { indexOfDot ->
            className.drop(indexOfDot + 1) to className.take(indexOfDot)
        }
    }
}

fun anyFile(path: String, baseDirectory: String) =
    if (path.endsWith(".class")) ClassFile(path, baseDirectory)
    else NonClassFile(path, baseDirectory)
