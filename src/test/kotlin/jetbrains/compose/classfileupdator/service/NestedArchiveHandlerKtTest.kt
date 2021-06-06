package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.AppConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File

internal class NestedArchiveHandlerKtTest {
    private val selectedFiles = listOf(
        "com/example/App.class",
        "com/example/util/Util$1.class",
    )

    @Test
    fun commands_to_execute_script() {
        val actual = commandsToExecuteScript().toString()
        val expected = readCommandFromResource("commands_to_execute_script.sh")
        assertEquals(
            expected.replace(" && ", "\n"),
            actual.replace(" && ", "\n")
        )
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @CsvSource(
        "MyRemoteApp.jar//nested/folder/Inner.zip,  script_to_parcel.sh",
        "MyRemoteApp.jar//nested/folder/Inner.zip//more/nested/folder/InnerMost.zip,    script_to_parcel2.sh",
        "MyRemoteApp.jar,   script_to_parcel3.sh",
        "MyRemoteApp.jar//nested/folder/Inner.zip//InnerMost.zip,    script_to_parcel4.sh",
    )
    fun script_to_parcel(serverAppName: String, scriptFile: String) {
        val appConfig = AppConfig.defaultConfig().copy(
            serverArchiveName = serverAppName
        ).pathsAdjusted()
        val actual = scriptToParcel(appConfig, selectedFiles).toString()
        val expected = readCommandFromResource(scriptFile)
        assertEquals(
            expected.replace(" && ", "\n"),
            actual.replace(" && ", "\n")
        )
    }

    private fun readCommandFromResource(scriptFile: String): String =
        File(ClassLoader.getSystemResource(scriptFile).file)
            .useLines { lines ->
                lines.joinToString("") { it.trim() }
                    .replace(" \\", " ")
                    .replace("\n", "")
            }
}
