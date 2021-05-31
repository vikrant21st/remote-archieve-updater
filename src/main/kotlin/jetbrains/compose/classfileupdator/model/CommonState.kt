package jetbrains.compose.classfileupdator.model

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList

class ClassFilesList private constructor(
    private val files: SnapshotStateList<ClassFile>,
    private val filesCount: MutableState<Int>
) {
    fun filesCount() = filesCount.value
    fun files() = files.toList()

    fun set(list: List<ClassFile>) {
        files.clear()
        files.addAll(list)
        synchronizeCount()
    }

    fun add(classFile: ClassFile) {
        files.add(classFile)
        synchronizeCount()
    }

    fun addAll(classFile: List<ClassFile>) {
        files.addAll(classFile)
        synchronizeCount()
    }

    fun remove(classFile: ClassFile) {
        files.remove(classFile)
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
        fun createList() = ClassFilesList(
            files = remember { mutableStateListOf() },
            filesCount = remember { mutableStateOf(0) }
        )
    }
}

class CommonState private constructor(
    val configuration: AppConfiguration,
    val outputWindowState: OutputWindowState,
    val all: ClassFilesList,
    val selected: ClassFilesList,
) {

    fun selectFile(classFile: ClassFile) {
        if (selected.files().contains(classFile)) {
            return
        }
        selected.add(classFile)
        selected.addAll(
            all.files().filter { it.className.startsWith(classFile.className + "$") })
    }

    companion object {
        private var commonAppState: CommonState? = null

        @Composable
        fun getAppState(): CommonState {
            if (commonAppState == null) {
                commonAppState = CommonState(
                    AppConfiguration.configuration(),
                    outputWindowState = OutputWindowState.getState(),
                    all = ClassFilesList.createList(),
                    selected = ClassFilesList.createList(),
                )
            }
            return commonAppState!!
        }
    }
}
