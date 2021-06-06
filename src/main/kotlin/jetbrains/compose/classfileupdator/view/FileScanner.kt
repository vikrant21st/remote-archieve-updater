package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import jetbrains.compose.classfileupdator.model.AppConfiguration
import jetbrains.compose.classfileupdator.model.CommonState
import jetbrains.compose.classfileupdator.service.getAllFilesIn
import java.io.File

@Composable
fun ColumnScope.FileScanner(appState: CommonState) {
    val modifier = Modifier.fillMaxWidth(0.9f).padding(10.dp)
        .align(Alignment.CenterHorizontally)

    Row(modifier) { ServerAppDirectory(appState.configuration) }
    Row(modifier) { AppNameUserPasswordAndPort(appState.configuration) }
    Row(modifier) { LocalBaseDirectory(appState) }
}

@Composable
private fun RowScope.LocalBaseDirectory(appState: CommonState) {
    TextField(
        value = appState.configuration.localDirectory.value,
        label = {
            Text(
                "Local directory, aligned with archive's folder-structure (" +
                        "For ex. proj/target/classes/** folder is aligned with packages in it's Jar)"
            )
        },
        modifier = Modifier.alignBy(LastBaseline).weight(1.0f),
        onValueChange = { appState.configuration.localDirectory.value = it },
    )

    VerticalSpace()

    VerticalSpace()

    Button(
        onClick = {
            appState.selected.clear()
            val baseDir = appState.configuration.localDirectory.value.text
            appState.all.set(
                getAllFilesIn(baseDir)
            )
        },
        modifier = Modifier.alignByBaseline(),
        enabled = File(appState.configuration.localDirectory.value.text).isDirectory
    ) {
        Text("Load files")
    }

    VerticalSpace(20.dp)

    Text(
        text = "${appState.all.filesCount()} files found",
        style = TextStyle(
            color = Color.Gray,
            fontSize = 0.7.em,
            fontStyle = FontStyle.Italic
        ),
        modifier = Modifier.alignByBaseline().width(80.dp),
    )
}

@Composable
private fun RowScope.AppNameUserPasswordAndPort(configuration: AppConfiguration) {
    TextField(
        value = configuration.serverArchiveName.value,
        label = { Text("Archive file") },
        modifier = Modifier.alignBy(LastBaseline).weight(0.4f),
        onValueChange = { configuration.serverArchiveName.value = it },
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
        value = configuration.serverArchiveDirectory.value,
        label = { Text("Archive directory (on server )") },
        modifier = Modifier.alignBy(LastBaseline).weight(1f),
        onValueChange = { configuration.serverArchiveDirectory.value = it },
    )
}
