package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.AppConfig
import jetbrains.compose.classfileupdator.model.ClassFile
import jetbrains.compose.classfileupdator.model.DebugMessage
import jetbrains.compose.classfileupdator.model.LogMessage
import jschutils.Monitor
import jschutils.withSFTPChannel
import jschutils.withSshSession
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking
import java.io.File

fun uploadJSch(
    files: List<ClassFile>,
    configuration: AppConfig,
    outputChannel: SendChannel<LogMessage>
): List<Monitor> {
    return configuration.getSshConfig()
        .withSshSession {
            withSFTPChannel {
                files.map { classFile ->
                    val monitor = Monitor(classFile.className)
                    val localFile = File(classFile.path)
                    val remoteFilePath =
                        configuration.realWorkDirectory + classFile.getFilePathInJar

                    put(localFile.inputStream(), remoteFilePath, monitor)
                    if (!monitor.completed)
                        throw Exception("Upload failed: ${monitor.filename}")

                    outputChannel.sendBlocking(
                        DebugMessage("Uploaded ${classFile.className} in ${monitor.timeTaken} millis")
                    )
                    monitor
                }
            }
        }
}
