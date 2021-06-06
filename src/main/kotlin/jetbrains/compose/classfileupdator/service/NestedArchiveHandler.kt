package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.*
import jetbrains.compose.classfileupdator.utils.countSubstring
import jetbrains.compose.classfileupdator.utils.fileAtPath
import jschutils.SshCommandsJsch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.zeroturnaround.zip.ZipUtil
import shellcommands.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString

internal const val parcel_name = "rau_parcel_with_all_data.zip"
internal const val rau_script = "rau.sh"
internal const val rau_work_directory = "rau_temp_work"
internal const val rau_files_upload_dir = "files"
internal const val archive_ = "archive_"

internal suspend fun uploadForNestedArchives(
    selectedFiles: List<AnyFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>,
) {
    withTimeLogging(outputChannel) {
        val archiveDepth = countSubstring(configuration.serverArchiveName, "//")
        outputChannel.send(HeaderMessage("Uploading files"))
        shipParcel(
            Parcel(
                scriptToParcel(
                    configuration,
                    selectedFiles.map { it.fullFilePathInArchive }),
                selectedFiles,
                archiveDepth
            )
        ) {
            uploadJSch(it, configuration.getSshConfig())
        }
        outputChannel.send(InfoMessage("Files uploaded"))
    }

    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Updating archive"))
        val op = SshCommandsJsch.runCommands(
            commandsToExecuteScript().toString(),
            configuration.getSshConfig(),
        )
        op.forEach {
            outputChannel.send(DebugMessage(it))
        }
        outputChannel.send(InfoMessage("Archive updated"))
    }
}

internal fun commandsToExecuteScript() =
    joinShellCommands(
        DeleteDir(rau_work_directory),
        MakeDir(rau_work_directory),
        Unzip("../$parcel_name", workingDir = rau_work_directory),
        DeleteFile("../$parcel_name"),
        ChMod("0775", rau_script),
        RunScript(rau_script),
        DeleteDir(rau_work_directory, workingDir = "..")
    )

internal fun scriptToParcel(appConfig: AppConfig, selectedFiles: List<String>) =
    with(appConfig) {
        val archives = serverArchiveName.split("//")
        val archiveFoldersStack = Stack<String>()
        val archiveNamesStack = Stack<String>()
        val mainArchivePath =
            serverArchiveDirectory + archiveNamesStack.push(archives[0])

        joinShellCommands(
            CopyFile(
                sourceFile = mainArchivePath,
                targetDir = archiveFoldersStack.push(archive_ + 0)
            ),

            archives.drop(1).mapIndexed { index, innerPath ->
                Unzip(
                    archivePath = "../${archiveFoldersStack.peek()}/${archiveNamesStack.peek()}".replaceFirst(
                        "//",
                        "/"
                    ),
                    fileToBeExtracted = listOf(archiveNamesStack.push(innerPath)),
                    workingDir = archiveFoldersStack.push(archive_ + (index + 1)),
                ).`and cd back`()
            }.toSingleCommand(),

            ZipFiles(
                filePaths = selectedFiles.map { it.replace("$", "\\$") },
                archivePath = "../${archiveFoldersStack.peek()}/${archiveNamesStack.peek()}",
                workingDir = rau_files_upload_dir,
            ).`and cd back`(),

            (1 until archiveFoldersStack.size).map {
                val workingDir = archiveFoldersStack.pop()
                val filePath = archiveNamesStack.pop()
                val archivePath = ("../${archiveFoldersStack.peek()}" +
                        "/${archiveNamesStack.peek()}").replaceFirst("//", "/")

                ZipFiles(
                    workingDir = workingDir,
                    archivePath = archivePath,
                    filePath = filePath
                ).`and cd back`()
            }.toSingleCommand(),

            MoveWithBackup("${archive_ + 0}/${archives[0]}" to mainArchivePath),
        )
    }

internal inline fun withinTempDirectory(action: Path.() -> Unit): Path {
    val tempParcelDirectory = Files.createTempDirectory("rau_parcel")
    try {
        action(tempParcelDirectory.absolute())
    } finally {
        if (tempParcelDirectory?.toFile()?.deleteRecursively() == false) {
            throw Exception("Failed to delete temp folder $tempParcelDirectory")
        }
    }
    return tempParcelDirectory
}

internal class Parcel(
    private val script: ShellCommand,
    private val selectedFiles: List<AnyFile>,
    private val archiveDepth: Int,
) {
    fun packTheParcel(tempDir: Path, parcelFile: File) =
        createFilesAndFolders(targetDir = tempDir)
            .also { packZip(tempDir = tempDir, parcelFile) }

    private fun createFilesAndFolders(targetDir: Path) {
        val targetDirPath = targetDir.absolutePathString()
        val scriptFile = fileAtPath(targetDirPath, rau_script)
        scriptFile.writeAndFlush(script.toString())
        scriptFile.setExecutable(true)

        runBlocking {
            createDumpDirectories(targetDirPath)
            copySelectedFiles(targetDirPath)
        }
    }

    private fun File.writeAndFlush(text: String) =
        writer().use {
            it.write(text)
            it.flush()
        }

    private fun packZip(tempDir: Path, parcelFile: File) {
        val sourceRootDir =
            fileAtPath(tempDir.absolutePathString())

        ZipUtil.pack(sourceRootDir, parcelFile, 6)
    }

    private fun CoroutineScope.createDumpDirectories(targetDir: String) =
        launch {
            (0..archiveDepth).forEach {
                fileAtPath(targetDir, "$archive_$it").mkdir()
            }
        }

    private fun CoroutineScope.copySelectedFiles(targetDir: String) =
        selectedFiles.forEach {
            launch {
                val target = fileAtPath(
                    targetDir,
                    rau_files_upload_dir,
                    it.fullFilePathInArchive
                )
                fileAtPath(it.path).copyTo(target, overwrite = true)
            }
        }
}

internal inline fun shipParcel(
    parcel: Parcel,
    crossinline uploadFunction: (File) -> Unit
) =
    withinTempDirectory {
        val rauWorkDir =
            fileAtPath(this.absolutePathString(), rau_work_directory)
        val parcelFile = fileAtPath(this.absolutePathString(), parcel_name)
        if (rauWorkDir.mkdir()) {
            parcel.packTheParcel(rauWorkDir.toPath(), parcelFile)
            uploadFunction(parcelFile)
        } else {
            throw Exception("Error creating directory $rau_work_directory")
        }
    }
