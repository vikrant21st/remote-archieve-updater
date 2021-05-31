package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.unit.dp
import jetbrains.compose.classfileupdator.model.AppConfiguration
import jetbrains.compose.classfileupdator.model.CommonState
import jetbrains.compose.classfileupdator.service.getClassFilesIn
import java.io.File

@Composable
fun ColumnScope.FileScanner(appState: CommonState) {
    val modifier = Modifier.fillMaxWidth(0.9f).padding(10.dp).align(Alignment.CenterHorizontally)

    Row(modifier) { ServerAppDirectory(appState.configuration) }
    Row(modifier) { AppNameUserPasswordAndPort(appState.configuration) }
    Row(modifier) { BaseDirectory(appState) }
}

@Composable
private fun RowScope.BaseDirectory(appState: CommonState) {
    TextField(
        value = appState.configuration.baseDirectory.value,
        label = { Text("Local compiled classes directory") },
        modifier = Modifier.alignBy(LastBaseline).weight(1.0f),
        onValueChange = { appState.configuration.baseDirectory.value = it },
    )

    VerticalSpace()

    Button(
        onClick = {
            appState.selected.clear()
            val baseDir = appState.configuration.baseDirectory.value.text
            appState.all.set(getClassFilesIn(baseDir))
        },
        modifier = Modifier.alignByBaseline(),
        enabled = File(appState.configuration.baseDirectory.value.text).isDirectory
    ) {
        Text("Load Classes")
    }

    VerticalSpace()

    Text(
        text = "${appState.all.filesCount()} files found",
        modifier = Modifier.alignByBaseline(),
    )
}

@Composable
private fun RowScope.AppNameUserPasswordAndPort(configuration: AppConfiguration) {
    TextField(
        value = configuration.serverAppName.value,
        label = { Text("Jar filename") },
        modifier = Modifier.alignBy(LastBaseline).weight(0.4f),
        onValueChange = { configuration.serverAppName.value = it },
    )

    VerticalSpace()
    TextField(
        value = configuration.username.value,
        label = { Text("Username") },
        modifier = Modifier.alignBy(LastBaseline).weight(0.25f),
        onValueChange = { configuration.username.value = it },
    )

    VerticalSpace()
    TextField(
        value = configuration.password.value,
        label = { Text("Password") },
        modifier = Modifier.alignBy(LastBaseline).weight(0.25f),
        onValueChange = { configuration.password.value = it },
    )

    VerticalSpace()
    TextField(
        value = configuration.sshPort.value,
        label = { Text("Port") },
        modifier = Modifier.alignBy(LastBaseline).weight(0.1f),
        onValueChange = { configuration.sshPort.value = it },
    )
}

@Composable
private fun RowScope.ServerAppDirectory(configuration: AppConfiguration) {
    TextField(
        value = configuration.serverAppDirectory.value,
        label = { Text("Server Jar directory") },
        modifier = Modifier.alignBy(LastBaseline).weight(1f),
        onValueChange = { configuration.serverAppDirectory.value = it },
    )
}
