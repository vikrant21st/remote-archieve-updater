package puttyutils

import com.lordcodes.turtle.shellRun
import sshcommands.api.SshCommandsApi
import sshcommands.api.SshConfig

internal const val plink = "plink.exe"

object SshCommandsPutty : SshCommandsApi() {
    override fun runCommands(command: String, sshConfig: SshConfig): List<String> {
        return runWithPlink(sshConfig, command).toList()
    }

    private fun runWithPlink(configuration: SshConfig, command: String): Pair<String, String> {
        val arguments = configuration.run {
            listOf(
                "-ssh", "-C", "-T", "-pw", password, "$username@$host", command
            )
        }
        val output = "$plink ${arguments.joinToString(" ")}"
        return output to shellRun(plink, arguments)
    }
}
