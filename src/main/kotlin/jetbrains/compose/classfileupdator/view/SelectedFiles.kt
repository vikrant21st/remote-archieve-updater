package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import jetbrains.compose.classfileupdator.model.*
import jetbrains.compose.classfileupdator.service.updateJar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@Composable
fun SelectedFiles(appState: CommonState) {
    val hostName = appState.configuration.hostName

    Row(Modifier.fillMaxWidth().padding(8.dp)) {
        Column {
            Row(Modifier.fillMaxWidth()) {
                TextField(
                    value = hostName.value,
                    placeholder = {
                        Text("Host")
                    },
                    modifier = Modifier.alignBy(LastBaseline).weight(1.0f),
                    onValueChange = { hostName.value = it }
                )

                VerticalSpace()

                Button(
                    onClick = { startUploadJob(appState) },
                    modifier = Modifier.alignByBaseline(),
                    enabled = appState.selected.filesCount() > 0 && hostName.value.text.isNotBlank()
                ) {
                    Text("Upload?")
                }

                VerticalSpace()

                Text(
                    text = "${appState.selected.filesCount()} files selected",
                    modifier = Modifier.alignBy(LastBaseline).weight(1.0f),
                    style = TextStyle(color = Color.Gray, fontSize = 0.7.em, fontStyle = FontStyle.Italic),
                    textAlign = TextAlign.End,
                )
            }

            LazyColumn(Modifier.fillMaxHeight().padding(8.dp)) {
                items(
                    // sort by class name
                    appState.selected.files().sortedBy { it.fileName() }
                ) { anyFile ->
                    SelectedFile(anyFile, removeAction = appState.selected::remove)
                }
            }
        }
    }
}

private fun startUploadJob(appState: CommonState) {
    AppConfiguration.saveToFile(appState.configuration)
    appState.outputWindowState.showOutputWindow.value = true
    appState.outputWindowState.doneWithTheJob.value = false

    val channel = Channel<LogMessage>(capacity = 15)

    GlobalScope.launch {
        updateJar(
            appState.selected.files(),
            AppConfig.copyFrom(appState.configuration).pathsAdjusted(),
            outputChannel = channel
        )
    }
    GlobalScope.launch {
        channel.consumeEach(appState.outputWindowState::log).let {
            appState.outputWindowState.doneWithTheJob.value = true
        }

    }
}

@Composable
fun SelectedFile(classFile: AnyFile, removeAction: (AnyFile) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            FileName(classFile)
        }

        Column(Modifier.weight(0.1f)) {}

        Column(Modifier.weight(0.1f)) {
            Button(
                onClick = { removeAction(classFile) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                ),
            ) {
                Text("-")
            }
        }
    }
}
