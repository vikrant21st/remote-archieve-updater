package jetbrains.compose.classfileupdator.service

import jetbrains.compose.classfileupdator.model.AppConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

private var lastSavedConfig: AppConfig? = null
private const val fileName = "configuration.json"


fun getFromFile(): AppConfig? {
    val file = File(fileName)
    val result = runCatching {
        Json.decodeFromString<AppConfig>(file.readText())
    }
    if (result.isFailure) {
        println("Error reading configuration.json: ${result.exceptionOrNull()}")
    }
    lastSavedConfig = result.getOrNull()
    return lastSavedConfig
}

fun saveToFile(newConfig: AppConfig) {
    val file = File(fileName)
    if (lastSavedConfig != null && lastSavedConfig == newConfig)
        return

    try {
        if (file.exists()) file.truncateFile()
        else file.createNewFile()

        lastSavedConfig = null
        val text = Json { prettyPrint = true }.encodeToString(newConfig)
        file.writer().use { writer ->
            writer.write(text)
            writer.flush()
        }
        lastSavedConfig = newConfig
    } catch (exception: Exception) {
        println(exception)
        exception.printStackTrace()
    }
}

private fun File.truncateFile() {
    FileOutputStream(name, true).use { fileOutputStream ->
        fileOutputStream.channel.use { channel ->
            channel.truncate(0)
        }
    }
}