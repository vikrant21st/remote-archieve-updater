package jetbrains.compose.classfileupdator.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue

class AppConfiguration private constructor(
    val localDirectory: MutableState<TextFieldValue>,
    val serverArchiveDirectory: MutableState<TextFieldValue>,
    val serverArchiveName: MutableState<TextFieldValue>,
    val username: MutableState<TextFieldValue>,
    val password: MutableState<TextFieldValue>,
    val sshPort: MutableState<TextFieldValue>,
    var hostName: MutableState<TextFieldValue>,
) {

    fun setBaseDirectory(baseDir: String) {
        localDirectory.value = TextFieldValue(baseDir)
    }

    companion object {
        private var configuration: AppConfiguration? = null

        @Composable
        internal fun configuration(): AppConfiguration {
            if (configuration == null) {
                val appConfig = AppConfig.readFromFile()

                configuration =
                    with(appConfig) {
                        AppConfiguration(
                            localDirectory = remember { mutableStateOf(TextFieldValue(localDirectory)) },
                            serverArchiveDirectory = remember {
                                mutableStateOf(TextFieldValue(serverArchiveDirectory))
                            },
                            serverArchiveName = remember { mutableStateOf(TextFieldValue(serverArchiveName)) },
                            username = remember { mutableStateOf(TextFieldValue(username)) },
                            password = remember { mutableStateOf(TextFieldValue(password)) },
                            hostName = remember { mutableStateOf(TextFieldValue(hostName)) },
                            sshPort = remember { mutableStateOf(TextFieldValue(sshPort.toString())) },
                        )
                    }
            }
            return configuration!!
        }

        fun saveToFile(configuration: AppConfiguration) = AppConfig.saveToFile(configuration)
    }
}
