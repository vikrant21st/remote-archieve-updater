package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import sshcommands.api.SshConfig

fun AppConfig.getSshConfig() = SshConfig(hostName, username, password, sshPort)

suspend fun updateJar(
    selectedFiles: List<AnyFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>,
) {
    try {
        uploadForNestedArchives(selectedFiles, configuration, outputChannel)
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

internal inline fun <T> withTimeLogging(
    outputChannel: SendChannel<LogMessage>,
    block: () -> T
): T {
    var time = System.currentTimeMillis()
    val result = block()
    time = (System.currentTimeMillis() - time) / 1000
    outputChannel.trySendBlocking(TimeLogMessage("Time Taken: $time seconds"))
    return result
}
