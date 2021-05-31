package jetbrains.compose.classfileupdator.model

class ClassFile(val path: String, baseDirectory: String) {
    val className = path.removePrefix(baseDirectory).removeSuffix(".class").replace("\\", ".")

    val getFilePathInJar by lazy {
        path.removePrefix(baseDirectory).replace("\\", "/")
    }

    val decoratedClassName by lazy {
        className.lastIndexOf('.').let { dot ->
            className.drop(dot + 1) to className.take(dot)
        }
    }

    fun getFolderPathInJar() = className.take(className.lastIndexOf('.')).replace('.', '/')

    override fun toString() = path
}