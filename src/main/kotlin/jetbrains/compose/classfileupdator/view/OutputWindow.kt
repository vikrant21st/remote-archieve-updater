package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jetbrains.compose.classfileupdator.model.*
import kotlin.reflect.KClass

@Composable
fun OutputWindow(outputWindowState: OutputWindowState) {
    val verboseLog = remember { mutableStateOf(false) }

    val logTypes =
        if (verboseLog.value)
            listOf()
        else
            listOf(
                HeaderMessage::class,
                InfoMessage::class,
                ErrorMessage::class,
                TimeLogMessage::class
            )

    Column(
        modifier = Modifier.background(Color.DarkGray).fillMaxWidth().fillMaxHeight().padding(8.dp)
    ) {
        Row(Modifier.align(Alignment.CenterHorizontally)) {
            LogModeToggle(verboseLog)
            SaveLogButton(outputWindowState.doneWithTheJob, outputWindowState::saveLogsFile)
        }

        LogsView(outputWindowState::getLogs, logTypes)

        BackButton(outputWindowState)
    }
}

@Composable
private fun ColumnScope.BackButton(outputWindowState: OutputWindowState) {
    Button(
        onClick = {
            outputWindowState.clearOutput()
            outputWindowState.showOutputWindow.value = false
        },
        enabled = outputWindowState.doneWithTheJob.value,
        modifier = Modifier.fillMaxWidth(0.7f).padding(8.dp).align(Alignment.CenterHorizontally)
    ) {
        Text(if (outputWindowState.doneWithTheJob.value) "Done" else "Wait...")
    }
}

@Composable
private fun ColumnScope.LogsView(
    logs: (List<KClass<out LogMessage>>) -> List<AnnotatedString>,
    logTypes: List<KClass<out LogMessage>>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(0.7f).fillMaxWidth(0.9f)
            .border(3.dp, Color.Blue).background(Color.Black)
            .align(Alignment.CenterHorizontally)
    ) {
        items(logs(logTypes)) {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 10.dp, end = 15.dp)
            )
        }
    }
}

@Composable
private fun RowScope.LogModeToggle(verboseLog: MutableState<Boolean>) {
    TextButton(
        onClick = { verboseLog.value = !verboseLog.value },
        modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically)
    ) {
        Text(
            text = if (verboseLog.value) "Summary only" else "Show complete log",
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RowScope.SaveLogButton(enabled: State<Boolean>, saveAction: () -> Unit) {
    Text(
        text = " | ",
        modifier = Modifier.align(Alignment.CenterVertically),
        textAlign = TextAlign.Center,
    )

    TextButton(
        onClick = saveAction,
        enabled = enabled.value,
        modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically)
    ) {
        Text(
            text = "Save log to file",
            modifier = Modifier.align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
        )
    }
}