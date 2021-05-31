package jschutils

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import sshcommands.api.SshConfig
import java.io.File

inline fun <T> SshConfig.withSshSession(action: Session.() -> T) =
    JSch().let { jSch ->
        jSch.setKnownHosts(System.getProperty("USERPROFILE") + "\\.ssh\\known_hosts")
        jSch.getSession(username, host, port)
            .run {
                setPassword(password)
                setConfig("StrictHostKeyChecking", "no")
                connect()
                try {
                    action()
                } finally {
                    disconnect()
                }
            }
    }

fun Session.exec(command: String): List<String> =
    (openChannel("exec") as ChannelExec).run {
        setCommand(command)
        inputStream = null
        setErrStream(System.err)
        val ipStream = inputStream
        connect()
        var output = ""
        val tmp = ByteArray(1024)
        while (true) {
            while (ipStream.available() > 0) {
                val i = ipStream.read(tmp, 0, 1024)
                if (i < 0)
                    break
                output += String(tmp, 0, i)
            }
            if (isClosed) {
                if (ipStream.available() > 0)
                    continue

                println("Exit-status: $exitStatus")
                break
            }
            runCatching { Thread.sleep(1000) }
        }
        output.split('\n').filter { it.isNotBlank() }
    }

inline fun <T> Session.withSFTPChannel(action: ChannelSftp.() -> T) =
    (openChannel("sftp") as ChannelSftp)
        .let { channelSftp ->
            channelSftp.connect()
            try {
                channelSftp.action()
            } finally {
                channelSftp.disconnect()
                channelSftp.exit()
            }
        }
