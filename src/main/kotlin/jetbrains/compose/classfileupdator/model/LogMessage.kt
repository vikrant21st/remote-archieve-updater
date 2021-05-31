package jetbrains.compose.classfileupdator.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em

sealed class LogMessage(val text: String, val textStyle: SpanStyle) {
    override fun toString(): String = text
}

class InfoMessage(message: String) : LogMessage(message, infoMessageStyle)
class DebugMessage(message: String) : LogMessage(message, infoMessageStyle)

class ErrorMessage(message: String) : LogMessage(message, errorMessageStyle)

class HeaderMessage(message: String) : LogMessage("\n$message", headerMessageStyle)

class TimeLogMessage(message: String) : LogMessage(message, timeLogMessageStyle)

val errorMessageStyle = SpanStyle(
    Color.Red, fontSize = 1.em, FontWeight.Bold, FontStyle.Normal
)
val timeLogMessageStyle = SpanStyle(
    Color.Gray, fontSize = (0.8).em, FontWeight.Normal, FontStyle.Italic
)
val headerMessageStyle = SpanStyle(
    Color.White, fontSize = (1.15).em, FontWeight.Bold, FontStyle.Normal
)
val infoMessageStyle = SpanStyle(
    Color.White, fontSize = 1.em, FontWeight.Normal, FontStyle.Normal
)