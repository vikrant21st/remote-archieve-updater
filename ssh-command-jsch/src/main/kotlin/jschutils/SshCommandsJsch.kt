package jschutils

import sshcommands.api.SshCommandsApi
import sshcommands.api.SshConfig

object SshCommandsJsch : SshCommandsApi() {
    override fun runCommands(command: String, sshConfig: SshConfig): List<String> {
        return sshConfig.withSshSession { exec(command) }
    }
}