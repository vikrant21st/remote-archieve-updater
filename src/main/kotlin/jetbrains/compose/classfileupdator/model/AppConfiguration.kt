package jetbrains.compose.classfileupdator.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue

class AppConfiguration private constructor(
    val baseDirectory: MutableState<TextFieldValue>,
    val serverAppDirectory: MutableState<TextFieldValue>,
    val serverAppName: MutableState<TextFieldValue>,
    val username: MutableState<TextFieldValue>,
    val password: MutableState<TextFieldValue>,
    val sshPort: MutableState<TextFieldValue>,
    var hostName: MutableState<TextFieldValue>,
    val workDirectory: String,
    val threads: Int,
) {

    fun setBaseDirectory(baseDir: String) {
        baseDirectory.value = TextFieldValue(baseDir)
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
                            baseDirectory = remember { mutableStateOf(TextFieldValue(localClassesDirectory)) },
                            serverAppDirectory = remember {
                                mutableStateOf(TextFieldValue(serverAppDirectory))
                            },
                            serverAppName = remember { mutableStateOf(TextFieldValue(serverAppName)) },
                            username = remember { mutableStateOf(TextFieldValue(username)) },
                            password = remember { mutableStateOf(TextFieldValue(password)) },
                            hostName = remember { mutableStateOf(TextFieldValue(hostName)) },
                            sshPort = remember { mutableStateOf(TextFieldValue(sshPort.toString())) },
                            workDirectory = serverTempDirectory,
                            threads = threads,
                        )
                    }
            }
            return configuration!!
        }

        fun saveToFile(configuration: AppConfiguration) = AppConfig.saveToFile(configuration)
    }
}
