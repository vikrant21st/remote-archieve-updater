package jetbrains.compose.classfileupdator.model

import jetbrains.compose.classfileupdator.service.getFromFile
import jetbrains.compose.classfileupdator.service.saveToFile
import jetbrains.compose.classfileupdator.utils.addFileSeparatorSuffix
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val localDirectory: String,
    val serverArchiveDirectory: String,
    val serverArchiveName: String,
    val hostName: String,
    val sshPort: Int,
    val username: String,
    val password: String,
) {
    fun pathsAdjusted() = copy(
        localDirectory = addFileSeparatorSuffix(localDirectory).trim(),
        serverArchiveDirectory = addFileSeparatorSuffix(
            serverArchiveDirectory,
            separator = '/'
        ).trim(),
        serverArchiveName = serverArchiveName.trim(),
        hostName = hostName.trim(),
    )

    companion object {

        fun defaultConfig() = AppConfig(
            localDirectory = "C:\\application\\target\\classes",
            serverArchiveDirectory = "/users/username/applications/jars",
            serverArchiveName = "MyRemoteApp.jar",
            username = "username",
            password = "secretpass",
            hostName = "",
            sshPort = 22,
        )

        fun copyFrom(appConfiguration: AppConfiguration) =
            with(appConfiguration) {
                AppConfig(
                    localDirectory = localDirectory.value.text,
                    serverArchiveDirectory = serverArchiveDirectory.value.text,
                    serverArchiveName = serverArchiveName.value.text,
                    username = username.value.text,
                    password = password.value.text,
                    sshPort = sshPort.value.text.toInt(),
                    hostName = hostName.value.text,
                )
            }

        fun readFromFile() = getFromFile() ?: defaultConfig()

        fun saveToFile(appConfiguration: AppConfiguration) =
            saveToFile(copyFrom(appConfiguration))
    }
}