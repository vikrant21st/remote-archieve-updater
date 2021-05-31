package sshcommands.api

import java.time.Duration

val Int.seconds: Duration
    get() = Duration.ofSeconds(this.toLong())

class SshConfig(
    val host: String,
    val username: String,
    val password: String,
    val port: Int
)

abstract class SshCommandsApi {
    abstract fun runCommands(command: String, sshConfig: SshConfig): List<String>
}
