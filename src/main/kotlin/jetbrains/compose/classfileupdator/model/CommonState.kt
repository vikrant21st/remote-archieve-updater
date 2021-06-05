package jetbrains.compose.classfileupdator.model

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList

class FilesList private constructor(
    private val files: SnapshotStateList<AnyFile>,
    private val filesCount: MutableState<Int>
) {
    fun filesCount() = filesCount.value
    fun files() = files.toList()

    fun set(list: List<AnyFile>) {
        files.clear()
        files.addAll(list)
        synchronizeCount()
    }

    fun add(file: AnyFile) {
        files.add(file)
        synchronizeCount()
    }

    fun addAll(files: List<AnyFile>) {
        this.files.addAll(files)
        synchronizeCount()
    }

    fun remove(file: AnyFile) {
        files.remove(file)
        synchronizeCount()
    }

    fun clear() {
        files.clear()
        synchronizeCount()
    }

    private fun synchronizeCount() {
        filesCount.value = files.count()
    }

    internal companion object {
        @Composable
        fun createList() = FilesList(
            files = remember { mutableStateListOf() },
            filesCount = remember { mutableStateOf(0) }
        )
    }
}

class CommonState private constructor(
    val configuration: AppConfiguration,
    val outputWindowState: OutputWindowState,
    val all: FilesList,
    val selected: FilesList,
) {

    fun selectFile(file: AnyFile) {
        if (selected.files().contains(file)) {
            return
        }
        selected.add(file)

        if (file is ClassFile)
            selected.addAll(
                all.files().filter {
                    it is ClassFile && it.className.startsWith(file.className + "$")
                }
            )
    }

    companion object {
        private var commonAppState: CommonState? = null

        @Composable
        fun getAppState(): CommonState {
            if (commonAppState == null) {
                commonAppState = CommonState(
                    AppConfiguration.configuration(),
                    outputWindowState = OutputWindowState.getState(),
                    all = FilesList.createList(),
                    selected = FilesList.createList(),
                )
            }
            return commonAppState!!
        }
    }
}
