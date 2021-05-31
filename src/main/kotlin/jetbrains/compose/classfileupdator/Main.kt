package jetbrains.compose.classfileupdator

import androidx.compose.desktop.AppManager
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.Window
import androidx.compose.material.darkColors
import androidx.compose.ui.unit.IntSize
import jetbrains.compose.classfileupdator.model.CommonState
import jetbrains.compose.classfileupdator.view.MainWindow
import jetbrains.compose.classfileupdator.view.OutputWindow
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.time.Duration

const val DEFAULT_WIDTH = 1200
const val DEFAULT_HEIGHT = 800

fun main() = Window(
        title = "Class Files Update",
        size = IntSize(DEFAULT_WIDTH, DEFAULT_HEIGHT),
) {
    val appState = CommonState.getAppState()

    DesktopMaterialTheme(colors = darkColors()) {
        when {
            appState.outputWindowState.showOutputWindow.value -> OutputWindow(appState.outputWindowState)
            else -> MainWindow()
        }
    }

    AppManager.windows.first().window.contentPane.dropTarget =
        object : DropTarget() {
            @Synchronized
            override fun drop(evt: DropTargetDropEvent) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_REFERENCE)
                    val droppedFiles =
                                evt.transferable.getTransferData(
                                        DataFlavor.javaFileListFlavor
                                ) as List<*>
                    droppedFiles.firstOrNull { (it as File).isDirectory }
                        ?.let {
                            appState.configuration.setBaseDirectory((it as File).absolutePath)
                        }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
}

val Int.seconds: Duration
    get() = Duration.ofSeconds(this.toLong())
