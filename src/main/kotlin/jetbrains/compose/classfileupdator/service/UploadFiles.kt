package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.*
import jetbrains.compose.classfileupdator.utils.divideIn
import jschutils.SshCommandsJsch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import puttyutils.SshCommandsPutty
import sshcommands.api.SshConfig

fun upload(
    files: List<AnyFile>,
    outputChannel: SendChannel<LogMessage>,
    configuration: AppConfig
) =
    runBlocking(Dispatchers.IO) {
        files.divideIn(configuration.threads)
            .forEach { list ->
                launch {
                    uploadJSch(list, configuration, outputChannel)
                }
            }
    }

fun AppConfig.getSshConfig() = SshConfig(hostName, username, password, sshPort)

suspend fun updateJar(
    selectedFiles: List<AnyFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>,
    useJsch: Boolean = true,
) {
    try {
        val config = configuration.getSshConfig()
        updateJarGeneric(
            selectedFiles, configuration, outputChannel
        ) { command: String ->
            runBlocking {
                outputChannel.send(
                    DebugMessage(
                        """Executing Command:
                        |$command
                        |
                        |Output:""".trimMargin()
                    )
                )
            }
            if (useJsch)
                SshCommandsJsch.runCommands(command, config)
            else
                SshCommandsPutty.runCommands(command, config)
        }
    } catch (exception: Exception) {
        outputChannel.send(
            ErrorMessage(
                """????? error in uploading files ??????
                |$exception
                |${exception.stackTrace.joinToString("\n")}
            """.trimMargin()
            )
        )
    } finally {
        runCatching { outputChannel.close() }
    }
}

private suspend fun updateJarGeneric(
    selectedFiles: List<AnyFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>,
    runCommandImpl: (String) -> List<String>
) {
    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Creating temp directory"))
        createAndCleanTempDirectory(
            configuration,
            selectedFiles,
            runCommandImpl
        ).forEach {
            outputChannel.send(DebugMessage(it))
        }
    }

    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Uploading files"))
        upload(selectedFiles, outputChannel, configuration)
    }

    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Adding Files To Jar"))
        addFilesToJar(configuration, selectedFiles.map { it.fullFilePathInArchive }, runCommandImpl)
            .forEach {
                outputChannel.send(InfoMessage(it))
            }
    }
}

private fun createAndCleanTempDirectory(
    configuration: AppConfig,
    selectedFiles: List<AnyFile>,
    runCommandImpl: (String) -> List<String>
): List<String> =
    with(configuration) {
        val rmIfExists = "rm -rf $serverTempDirectory"
        val createTempDir = "mkdir -p $serverTempDirectory"
        val createPackageFolders = selectedFiles
            .map { it.fileLocationInArchive() }
            .distinct()
            .map { "mkdir -p $it" }
        val commands = listOf(
            rmIfExists,
            createTempDir,
            "cd $serverTempDirectory"
        ) + createPackageFolders + "pwd"
        val output = runCommandImpl(joinCommands(commands))
        realWorkDirectory = output.last().trim() + '/'
        return output
    }

private fun joinCommands(commands: List<String>) = commands.joinToString(" && ")

private fun addFilesToJar(
    configuration: AppConfig,
    files: List<String>,
    runCommandImpl: (String) -> List<String>
): List<String> =
    runCommandImpl(commandsToAddFilesIntoJar(files, configuration))

@Suppress("LocalVariableName")
private fun commandsToAddFilesIntoJar(files: List<String>, configuration: AppConfig) =
    with(configuration) {
        val filesList = files.joinToString(" ").replace("$", "\\$")

        val cdToWorkDir = "cd $serverTempDirectory"
        val copyJar = "cp $serverAppDirectory$serverAppName ."
        val zipFilesIntoJar = "zip -o $serverAppName $filesList"
        val backupOriginalJar =
            "mv $serverAppDirectory$serverAppName $serverAppDirectory$serverAppName-bk"
        val installNewJar = "mv $serverAppName $serverAppDirectory$serverAppName"
        val cleanUp = "rm -rf $serverTempDirectory"

        joinCommands(
            listOf(
                cdToWorkDir,
                copyJar, zipFilesIntoJar,
                backupOriginalJar, installNewJar,
                cleanUp
            )
        )
    }

private inline fun <T> withTimeLogging(outputChannel: SendChannel<LogMessage>, block: () -> T): T {
    var time = System.currentTimeMillis()
    val result = block()
    time = (System.currentTimeMillis() - time) / 1000
    runBlocking { outputChannel.send(TimeLogMessage("Time Taken: $time seconds")) }
    return result
}
