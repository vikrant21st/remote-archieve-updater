package jetbrains.compose.classfileupdator.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpace(width: Dp = 8.dp) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun HorizontalLine(thickness: Dp = 4.dp, padding: Dp = 8.dp) {
    Row(Modifier.fillMaxWidth().background(Color.Magenta).height(thickness).padding(padding)) { }
}