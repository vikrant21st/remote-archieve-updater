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
import jetbrains.compose.classfileupdator.model.ClassFile
import jetbrains.compose.classfileupdator.service.filterClasses

@Composable
fun ClassFileFinder(
    allFiles: () -> List<ClassFile>,
    selectedFiles: () -> List<ClassFile>,
    actionToSelectFile: (ClassFile) -> Unit
) {
    val filteredClasses = remember { mutableStateListOf<ClassFile>() }

    ClassSearchTextBox(
            allFiles,
            saveFilteredClassesAction = { newFilteredClasses ->
                filteredClasses.clear()
                filteredClasses.addAll(newFilteredClasses.filter { !selectedFiles().contains(it) })
            }
    )

    FilteredClassFilesList(
            filteredClasses,
            saveClassFileAction = actionToSelectFile
    )
}

@Composable
fun FilteredClassFilesList(
    filteredClasses: List<ClassFile>,
    saveClassFileAction: (ClassFile) -> Unit
) {
    // sort by class name
    val filteredClassesSorted = filteredClasses.sortedBy { it.decoratedClassName.first }
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(8.dp)) {
        LazyColumn {
            items(filteredClassesSorted) { classFile ->
                FileListItem(classFile, saveAction = saveClassFileAction)
            }
        }
    }
}

@Composable
fun FileListItem(classFile: ClassFile, saveAction: (ClassFile) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            ClassName(classFile)
        }

        Column(Modifier.weight(0.1f)) {}

        Column(Modifier.weight(0.1f)) {
            Button(
                    onClick = { saveAction(classFile) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green, contentColor = Color.White),
            ) {
                Text("+")
            }
        }
    }
}

@Composable
fun ClassSearchTextBox(allFiles: () -> List<ClassFile>, saveFilteredClassesAction: (List<ClassFile>) -> Unit) {
    val keyword = remember { mutableStateOf(TextFieldValue("")) }

    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        TextField(
                value = keyword.value,
                placeholder = {
                    Text("Search classes")
                },
                modifier = Modifier.fillMaxWidth().weight(1.0f),
                onValueChange = {
                    keyword.value = it
                    saveFilteredClassesAction(filterClasses(allFiles, keyword.value.text))
                },
        )
    }
}

