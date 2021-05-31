package jetbrains.compose.classfileupdator.view

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import jetbrains.compose.classfileupdator.model.ClassFile

@Composable
fun ClassName(classFile: ClassFile) {
    Text(
            text = classFile.decoratedClassName.first,
            style = MaterialTheme.typography.body1
    )
    Text(
            text = classFile.decoratedClassName.second,
            style = MaterialTheme.typography.body2.copy(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Thin),
    )
}
