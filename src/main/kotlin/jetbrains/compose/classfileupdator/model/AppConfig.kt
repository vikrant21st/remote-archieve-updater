package jetbrains.compose.classfileupdator.model

import jetbrains.compose.classfileupdator.service.getFromFile
import jetbrains.compose.classfileupdator.service.saveToFile
import jetbrains.compose.classfileupdator.utils.addFileSeparatorSuffix
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.min

private const val maxThreads = 8
private const val defaultThreads = 4

@Serializable
data class AppConfig(
    val localClassesDirectory: String,
    val serverAppDirectory: String,
    val serverAppName: String,
    val serverTempDirectory: String,
    val hostName: String,
    val sshPort: Int,
    val username: String,
    val password: String,
    val threads: Int,
) {
    @Transient
    var realWorkDirectory = serverTempDirectory

    fun pathsAdjusted() = copy(
        localClassesDirectory = addFileSeparatorSuffix(localClassesDirectory),
        serverAppDirectory = addFileSeparatorSuffix(serverAppDirectory, separator = '/'),
        serverTempDirectory = addFileSeparatorSuffix(serverTempDirectory, separator = '/'),
        threads = min(threads, maxThreads)
    )

    companion object {

        private fun defaultConfig() = AppConfig(
            localClassesDirectory = "C:\\application\\target\\classes",
            serverAppDirectory = "/users/username/applications/jars",
            serverAppName = "MyRemoteApp.jar",
            username = "username",
            password = "secretpass",
            hostName = "",
            sshPort = 22,
            serverTempDirectory = "\$HOME/tmpwrk",
            threads = defaultThreads
        )

        fun copyFrom(appConfiguration: AppConfiguration) = with(appConfiguration) {
            AppConfig(
                localClassesDirectory = baseDirectory.value.text,
                serverAppDirectory = serverAppDirectory.value.text,
                serverAppName = serverAppName.value.text,
                username = username.value.text,
                password = password.value.text,
                sshPort = sshPort.value.text.toInt(),
                hostName = hostName.value.text,
                serverTempDirectory = workDirectory,
                threads = threads
            )
        }

        fun readFromFile() = getFromFile() ?: defaultConfig()

        fun saveToFile(appConfiguration: AppConfiguration) = saveToFile(copyFrom(appConfiguration))
    }
}