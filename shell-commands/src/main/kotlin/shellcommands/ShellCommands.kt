package shellcommands

internal val String.doubleQuoted get() = "\"$this\""

sealed class ShellCommand(workingDir: String?) {
    protected val workingDir = workingDir?.doubleQuoted

    val `cd workingDir && `
        get() = cdToWorkingDir()?.let { "$it && " } ?: ""

    private fun cdToWorkingDir() = when (workingDir) {
        null, ".".doubleQuoted -> null
        else -> "cd $workingDir"
    }

    operator fun plus(other: ShellCommand): ShellCommand =
        this `&&` other

    infix fun `&&`(other: ShellCommand): ShellCommand =
        CombinedCommands(this, other)

    fun `and cd back`() =
        CustomCommand("current_dir=\$(pwd)") `&&`
                this `&&` CustomCommand("cd \"\$current_dir\"")

    abstract override fun toString(): String
}

private class CustomCommand(private val cmd: String) : ShellCommand(null) {
    override fun toString() = cmd
}

private object NoCommand : ShellCommand(null) {
    override fun toString() = ""
}

val NO_COMMAND = NoCommand as ShellCommand

private class CombinedCommands(
    private val first: ShellCommand,
    private val second: ShellCommand
) : ShellCommand(null) {
    override fun toString() =
        when {
            first is NoCommand -> second.toString()
            second is NoCommand -> first.toString()
            else -> "$first && $second"
        }
}

class MakeDir(
    directory: String,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val directory = directory.doubleQuoted
    override fun toString() = "$`cd workingDir && `mkdir -p $directory"
}

class MakeDirs(
    directories: List<String>,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val mkDirectories =
        directories.distinct().map { MakeDir(it) }.toSingleCommand()

    override fun toString() = "$`cd workingDir && `$mkDirectories"
}

class DeleteDir(
    directory: String,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val directory = directory.doubleQuoted
    override fun toString() = "$`cd workingDir && `rm -rf $directory"
}

class DeleteFile(
    path: String,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val path = path.doubleQuoted
    override fun toString() = "$`cd workingDir && `rm $path"
}

class CopyFile(
    sourceFile: String,
    targetDir: String = ".",
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val sourceFile = sourceFile.doubleQuoted
    private val targetDir = targetDir.doubleQuoted
    override fun toString() = "$`cd workingDir && `cp $sourceFile $targetDir"
}

class Unzip(
    archivePath: String,
    fileToBeExtracted: List<String>? = null,
    workingDir: String? = null,
) : ShellCommand(workingDir) {

    private val archivePath = archivePath.doubleQuoted
    private val fileToBeExtracted =
        if (fileToBeExtracted == null || fileToBeExtracted.isEmpty())
            ""
        else
            " " + fileToBeExtracted.joinToString(" ") { it.doubleQuoted }

    override fun toString() = `cd workingDir && ` +
            "unzip $archivePath" + fileToBeExtracted
}

class ChMod(private val privileges: String, path: String) : ShellCommand(null) {
    private val path = path.doubleQuoted
    override fun toString() = "chmod $privileges $path"
}

class RunScript(
    private val script: String,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    override fun toString() = "$`cd workingDir && `./$script"
}

class ZipFiles(
    filePaths: List<String>,
    archivePath: String,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    constructor(filePath: String, archivePath: String, workingDir: String = ".")
            : this(filePaths = listOf(filePath), archivePath, workingDir)

    private val archivePath = archivePath.doubleQuoted
    private val files = filePaths.joinToString(separator = " ") {
        it.doubleQuoted
    }

    override fun toString() = "$`cd workingDir && `zip -o $archivePath $files"
}

class ZipDirectory(
    directory: String,
    archivePath: String,
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val directory = directory.doubleQuoted
    private val archivePath = archivePath.doubleQuoted

    override fun toString() =
        "$`cd workingDir && `zip -r $archivePath $directory"
}

class MoveWithBackup(
    fromTo: Pair<String, String>,
    suffix: String = "-bk",
    workingDir: String? = null,
) : ShellCommand(workingDir) {
    private val newFile: String = fromTo.first.doubleQuoted
    private val target: String = fromTo.second.doubleQuoted
    private val backupFile: String = (fromTo.second + suffix).doubleQuoted

    override fun toString(): String {
        return "$`cd workingDir && `mv $target $backupFile && mv $newFile $target"
    }
}

class ChangeDir(directory: String) : ShellCommand(null) {
    private val directory = directory.doubleQuoted
    override fun toString() = "cd $directory"
}

object PrintWorkingDirectory : ShellCommand(null) {
    override fun toString() = "pwd"
}

fun Iterable<ShellCommand>.toSingleCommand() = joinShellCommands(this)

fun joinShellCommands(commands: Iterable<ShellCommand>): ShellCommand =
    commands.fold(NO_COMMAND) { allCommands, nextCmd -> allCommands `&&` nextCmd }

fun joinShellCommands(vararg commands: ShellCommand): ShellCommand =
    commands.toList().toSingleCommand()