package org.example.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.app.data.Task
import org.example.app.data.TaskRepository

data class TaskEditorState(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val completed: Boolean = false
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = repository.observeTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
    val tasks: StateFlow<List<Task>> = _tasks

    private val _editorState = MutableStateFlow(TaskEditorState())
    val editorState: StateFlow<TaskEditorState> = _editorState.asStateFlow()

    // PUBLIC_INTERFACE
    fun startCreate() {
        /** Reset editor for a new task. */
        _editorState.value = TaskEditorState()
    }

    // PUBLIC_INTERFACE
    fun startEdit(task: Task) {
        /** Populate editor with an existing task to edit. */
        _editorState.value = TaskEditorState(
            id = task.id,
            title = task.title,
            description = task.description,
            completed = task.completed
        )
    }

    // PUBLIC_INTERFACE
    fun updateTitle(newTitle: String) {
        /** Update the editor's title. */
        _editorState.update { it.copy(title = newTitle) }
    }

    // PUBLIC_INTERFACE
    fun updateDescription(newDescription: String) {
        /** Update the editor's description. */
        _editorState.update { it.copy(description = newDescription) }
    }

    // PUBLIC_INTERFACE
    fun updateCompleted(completed: Boolean) {
        /** Update the editor's completed flag. */
        _editorState.update { it.copy(completed = completed) }
    }

    // PUBLIC_INTERFACE
    fun save(onSaved: (Long) -> Unit = {}) {
        /** Save the editor state as create or update. */
        val state = _editorState.value
        viewModelScope.launch {
            if (state.id == null) {
                if (state.title.isNotBlank()) {
                    val id = repository.addTask(state.title, state.description)
                    onSaved(id)
                }
            } else {
                repository.updateTask(state.id, state.title, state.description, state.completed)
                onSaved(state.id)
            }
        }
    }

    // PUBLIC_INTERFACE
    fun delete(id: Long) {
        /** Delete a task by id. */
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    // PUBLIC_INTERFACE
    fun toggleCompleted(id: Long, completed: Boolean) {
        /** Toggle task completion from the list. */
        viewModelScope.launch {
            repository.toggleCompleted(id, completed)
        }
    }
}
