package jetbrains.compose.classfileupdator.view

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import jetbrains.compose.classfileupdator.model.AnyFile
import jetbrains.compose.classfileupdator.model.ClassFile

@Composable
fun FileName(anyFile: AnyFile) {
    val (filename, location) =
        when (anyFile) {
            is ClassFile -> anyFile.decoratedClassName
            else -> anyFile.fileToLocation()
        }

    Text(
        text = filename,
        style = MaterialTheme.typography.body1,
    )

    Text(
        text = location,
        style = MaterialTheme.typography.body2.copy(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Thin),
    )
}
