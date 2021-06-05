package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.*
import jschutils.Monitor
import jschutils.withSFTPChannel
import jschutils.withSshSession
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import java.io.File

fun uploadJSch(
    files: List<AnyFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>
): List<Monitor> {
    return configuration.getSshConfig()
        .withSshSession {
            withSFTPChannel {
                files.map { file ->
                    val monitor = Monitor(file.fileName())
                    val localFile = File(file.path)
                    val remoteFilePath =
                        configuration.realWorkDirectory + file.fullFilePathInArchive

                    put(localFile.inputStream(), remoteFilePath, monitor)
                    if (!monitor.completed)
                        throw Exception("Upload failed: ${monitor.filename}")

                    outputChannel.trySendBlocking(
                        DebugMessage("Uploaded ${file.fileName()} in ${monitor.timeTaken} millis")
                    )
                    monitor
                }
            }
        }
}
