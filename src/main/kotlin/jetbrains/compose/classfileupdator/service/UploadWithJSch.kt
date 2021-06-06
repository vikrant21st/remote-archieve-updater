package jetbrains.compose.classfileupdator.service

import jschutils.Monitor
import jschutils.withSFTPChannel
import jschutils.withSshSession
import sshcommands.api.SshConfig
import java.io.File

fun uploadJSch(file: File, sshConfig: SshConfig): Monitor =
    sshConfig.withSshSession {
        withSFTPChannel {
            val monitor = Monitor(file.name)
            file.inputStream().use { inputFile ->
                put(inputFile, file.name, monitor)
            }
            if (!monitor.completed)
                throw Exception("Upload failed: ${monitor.filename}")
            monitor
        }
    }
