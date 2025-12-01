package org.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
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
    val tasks by vm.tasks.collectAsState()
    var editorVisible by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    TaskListScreen(
        tasks = tasks,
        onToggle = { task: Task ->
            vm.toggleCompleted(task.id, !task.completed)
        },
        onEdit = { task ->
            isEditing = true
            vm.startEdit(task)
            editorVisible = true
        },
        onDelete = { task ->
            vm.delete(task.id)
        },
        onAdd = {
            isEditing = false
            vm.startCreate()
            editorVisible = true
        }
    )

    val editorState by vm.editorState.collectAsState()
    TaskEditorSheet(
        visible = editorVisible,
        isEditing = isEditing,
        title = editorState.title,
        description = editorState.description,
        completed = editorState.completed,
        onTitleChange = vm::updateTitle,
        onDescriptionChange = vm::updateDescription,
        onCompletedChange = vm::updateCompleted,
        onDismiss = { editorVisible = false },
        onSave = { vm.save { /* no-op */ } }
    )
}
