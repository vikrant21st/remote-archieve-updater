package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.*
import jetbrains.compose.classfileupdator.utils.divideIn
import jschutils.SshCommandsJsch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import puttyutils.SshCommandsPutty
import sshcommands.api.SshConfig

fun upload(
    classFiles: List<ClassFile>,
    outputChannel: SendChannel<LogMessage>,
    configuration: AppConfig
) =
    runBlocking(Dispatchers.IO) {
        classFiles.divideIn(configuration.threads)
            .forEach { list ->
                launch {
                    uploadJSch(list, configuration, outputChannel)
                }
            }
    }

fun AppConfig.getSshConfig() = SshConfig(hostName, username, password, sshPort)

suspend fun updateJar(
    selectedClassFiles: List<ClassFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>,
    useJsch: Boolean = true,
) {
    try {
        val config = configuration.getSshConfig()
        updateJarGeneric(
            selectedClassFiles, configuration, outputChannel
        ) { command: String ->
            outputChannel.sendBlocking(
                DebugMessage(
                    """Executing Command:
                        |$command
                        |
                        |Output:""".trimMargin()
                )
            )
            if (useJsch)
                SshCommandsJsch.runCommands(command, config)
            else
                SshCommandsPutty.runCommands(command, config)
        }
    } catch (exception: Exception) {
        outputChannel.sendBlocking(
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
    selectedClassFiles: List<ClassFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>,
    runCommandImpl: (String) -> List<String>
) {
    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Creating temp directory"))
        createAndCleanTempDirectory(
            configuration,
            selectedClassFiles,
            runCommandImpl
        ).forEach {
            outputChannel.send(DebugMessage(it))
        }
    }

    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Uploading files"))
        upload(selectedClassFiles, outputChannel, configuration)
    }

    withTimeLogging(outputChannel) {
        outputChannel.send(HeaderMessage("Adding Files To Jar"))
        addFilesToJar(configuration, selectedClassFiles.map { it.getFilePathInJar }, runCommandImpl)
            .forEach {
                outputChannel.send(InfoMessage(it))
            }
    }
}

private fun createAndCleanTempDirectory(
    configuration: AppConfig,
    selectedClassFiles: List<ClassFile>,
    runCommandImpl: (String) -> List<String>
): List<String> =
    with(configuration) {
        val rmIfExists = "rm -rf $serverTempDirectory"
        val createTempDir = "mkdir -p $serverTempDirectory"
        val createPackageFolders = selectedClassFiles
            .map { it.getFolderPathInJar() }
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
    outputChannel.sendBlocking(TimeLogMessage("Time Taken: $time seconds"))
    return result
}
