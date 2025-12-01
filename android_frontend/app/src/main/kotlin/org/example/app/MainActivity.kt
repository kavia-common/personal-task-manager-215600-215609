package org.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import org.example.app.data.Task
import org.example.app.data.TaskRepository
import org.example.app.ui.TaskViewModel
import org.example.app.ui.screens.TaskEditorSheet
import org.example.app.ui.screens.TaskListScreen
import org.example.app.ui.theme.OceanProfessionalTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        val repo = TaskRepository.create(this)
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OceanProfessionalTheme {
                ToDoApp(viewModel)
            }
        }
    }
}

@Composable
private fun ToDoApp(vm: TaskViewModel) {
    // Collect flows explicitly
    val tasks by vm.tasks.collectAsState()
    val editorState by vm.editorState.collectAsState()

    // Use rememberSaveable to avoid inline remember issues and survive config changes
    var editorVisible by rememberSaveable { mutableStateOf(false) }
    var isEditing by rememberSaveable { mutableStateOf(false) }

    // Explicit lambdas to avoid invokeDynamic indirection
    val onToggle: (Task) -> Unit = { task ->
        vm.toggleCompleted(task.id, !task.completed)
    }
    val onEdit: (Task) -> Unit = { task ->
        isEditing = true
        vm.startEdit(task)
        editorVisible = true
    }
    val onDelete: (Task) -> Unit = { task ->
        vm.delete(task.id)
    }
    val onAdd: () -> Unit = {
        isEditing = false
        vm.startCreate()
        editorVisible = true
    }

    TaskListScreen(
        tasks = tasks,
        onToggle = onToggle,
        onEdit = onEdit,
        onDelete = onDelete,
        onAdd = onAdd
    )

    TaskEditorSheet(
        visible = editorVisible,
        isEditing = isEditing,
        title = editorState.title,
        description = editorState.description,
        completed = editorState.completed,
        onTitleChange = { vm.updateTitle(it) },
        onDescriptionChange = { vm.updateDescription(it) },
        onCompletedChange = { vm.updateCompleted(it) },
        onDismiss = { editorVisible = false },
        onSave = { vm.save() }
    )
}
