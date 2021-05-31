package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import jetbrains.compose.classfileupdator.model.CommonState.Companion.getAppState

@Composable
fun MainWindow() {
    val appState = getAppState()

    Column(Modifier.background(Color.DarkGray)) {
        FileScanner(appState)
        HorizontalLine()

        Row(Modifier.fillMaxWidth().fillMaxHeight().padding(8.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().fillMaxHeight().padding(8.dp)) {
                val boxWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() / 2 }
                val boxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

                Column(Modifier.align(Alignment.CenterStart).size(boxWidth, boxHeight)) {
                    ClassFileFinder(
                        allFiles = { appState.all.files() },
                        selectedFiles = { appState.selected.files() },
                        actionToSelectFile = appState::selectFile
                    )
                }

                Column(Modifier.align(Alignment.CenterEnd).size(boxWidth, boxHeight)) {
                    SelectedFiles(appState)
                }
            }
        }
    }

}

@Composable
private fun ColumnScope.Banner() {
    Row(Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally)) {
        Text(
            text = "Jar file updater",
            color = Color.LightGray,
            fontSize = 2.em,
            fontWeight = FontWeight.ExtraBold
        )
    }
}