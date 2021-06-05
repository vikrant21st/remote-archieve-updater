package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jetbrains.compose.classfileupdator.model.AnyFile
import jetbrains.compose.classfileupdator.service.filterFiles

@Composable
fun FileFinder(
    allFiles: () -> List<AnyFile>,
    selectedFiles: () -> List<AnyFile>,
    actionToSelectFile: (AnyFile) -> Unit
) {
    val filteredClasses = remember { mutableStateListOf<AnyFile>() }

    FileSearchTextBox(
        allFiles,
        saveFilteredClassesAction = { newFilteredClasses ->
            filteredClasses.clear()
            filteredClasses.addAll(newFilteredClasses.filter { !selectedFiles().contains(it) })
        }
    )

    FilteredFilesList(
        filteredClasses,
        saveFileAction = actionToSelectFile
    )
}

@Composable
private fun FilteredFilesList(
    filteredFiles: List<AnyFile>,
    saveFileAction: (AnyFile) -> Unit
) {
    // sort by file name
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(8.dp)) {
        LazyColumn {
            items(filteredFiles.sortedBy { it.fileToLocation().first }) { classFile ->
                FileListItem(classFile, saveAction = saveFileAction)
            }
        }
    }
}

@Composable
private fun FileListItem(anyFile: AnyFile, saveAction: (AnyFile) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            FileName(anyFile)
        }

        Column(Modifier.weight(0.1f)) {}

        Column(Modifier.weight(0.1f)) {
            Button(
                onClick = { saveAction(anyFile) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green, contentColor = Color.White),
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun FileSearchTextBox(allFiles: () -> List<AnyFile>, saveFilteredClassesAction: (List<AnyFile>) -> Unit) {
    val keyword = remember { mutableStateOf(TextFieldValue("")) }

    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        TextField(
            value = keyword.value,
            placeholder = {
                Text("Search files")
            },
            modifier = Modifier.fillMaxWidth().weight(1.0f),
            onValueChange = {
                keyword.value = it
                saveFilteredClassesAction(filterFiles(allFiles, keyword.value.text))
            },
        )
    }
}

