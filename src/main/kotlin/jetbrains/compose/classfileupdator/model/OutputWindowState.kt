package jetbrains.compose.classfileupdator.model

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import jetbrains.compose.classfileupdator.service.filterLogs
import jetbrains.compose.classfileupdator.service.logToAnnotatedString
import jetbrains.compose.classfileupdator.service.saveLogsFile
import kotlin.reflect.KClass

class OutputWindowState private constructor(
    val showOutputWindow: MutableState<Boolean>,
    private val logs: SnapshotStateList<LogMessage>,
    val doneWithTheJob: MutableState<Boolean>,
) {
    fun getLogs(kClasses: List<KClass<out LogMessage>> = emptyList()) =
        filterLogs(logs, kClasses).flatMap(::logToAnnotatedString)

    fun log(msg: LogMessage) {
        println(msg)
        logs.add(msg)
    }

    fun saveLogsFile() = saveLogsFile(logs)

    fun clearOutput() = logs.clear()

    companion object {
        private var state: OutputWindowState? = null

        @Composable
        fun getState(): OutputWindowState {
            if (state == null) {
                state = OutputWindowState(
                    showOutputWindow = remember { mutableStateOf(false) },
                    logs = remember { mutableStateListOf() },
                    doneWithTheJob = remember { mutableStateOf(false) },
                )
            }
            return state!!
        }
    }
}