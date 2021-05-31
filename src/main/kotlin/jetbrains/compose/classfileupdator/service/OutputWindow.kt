package jetbrains.compose.classfileupdator.service

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import jetbrains.compose.classfileupdator.model.LogMessage
import java.io.File
import java.time.Instant
import kotlin.reflect.KClass

fun filterLogs(logs: List<LogMessage>, kClasses: List<KClass<out LogMessage>>) =
    if (kClasses.isEmpty()) logs
    else logs.filter { kClasses.contains(it::class) }

fun saveLogsFile(logs: List<LogMessage>) =
    try {
        val dir = File("logs")
        if (!dir.exists())
            dir.mkdir()

        val logFileName = dir.name + File.separatorChar +
                "upload_log_${Instant.now()}.log".replace(":", "")
        val file = File(logFileName)
        file.createNewFile()
        file.writer().use { writer ->
            writer.write(logs.joinToString(separator = "\n") { it.text })
            writer.flush()
        }
    } catch (exception: Exception) {
        println(exception)
        exception.printStackTrace()
    }

fun logToAnnotatedString(message: LogMessage): List<AnnotatedString> =
    message.text.split('\n')
        .map { messageLine ->
            buildAnnotatedString {
                withStyle(message.textStyle) {
                    append(messageLine)
                }
            }
        }