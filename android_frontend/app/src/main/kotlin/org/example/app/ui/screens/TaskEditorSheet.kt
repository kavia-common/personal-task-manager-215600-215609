package org.example.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// PUBLIC_INTERFACE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorSheet(
    visible: Boolean,
    isEditing: Boolean,
    title: String,
    description: String,
    completed: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    /**
     * Modal bottom sheet for creating/updating a task.
     * Uses rememberModalBottomSheetState so Density is taken from CompositionLocal.
     * Avoids inline-heavy patterns to prevent IR compiler issues on some CI setups.
     *
     * Note: Replaced Row usages with Column/Box-based lightweight layout to avoid RowKt.Row inline paths.
     */
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val sheetOuterModifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .navigationBarsPadding()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = sheetOuterModifier) {
            HeaderTitle(isEditing = isEditing)

            Spacer(modifier = Modifier.height(12.dp))

            TitleField(
                title = title,
                onTitleChange = onTitleChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            DescriptionField(
                description = description,
                onDescriptionChange = onDescriptionChange
            )

            if (isEditing) {
                Spacer(Modifier.height(8.dp))
                CompletedToggleLine(
                    checked = completed,
                    onCheckedChange = onCompletedChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SaveButton(
                isEditing = isEditing,
                onSave = {
                    onSave()
                    onDismiss()
                }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderTitle(isEditing: Boolean) {
    val text = if (isEditing) "Edit Task" else "New Task"
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun TitleField(
    title: String,
    onTitleChange: (String) -> Unit
) {
    val modifier = Modifier.fillMaxWidth()
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = modifier,
        label = { Text("Title") },
        singleLine = true
    )
}

@Composable
private fun DescriptionField(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    val modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 100.dp)
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        modifier = modifier,
        label = { Text("Description (optional)") }
    )
}

@Composable
private fun SaveButton(
    isEditing: Boolean,
    onSave: () -> Unit
) {
    val buttonModifier = Modifier.fillMaxWidth()
    val colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
    Button(
        onClick = onSave,
        modifier = buttonModifier,
        colors = colors
    ) {
        val label = if (isEditing) "Save Changes" else "Add Task"
        Text(text = label)
    }
}

@Composable
private fun CompletedToggleLine(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    /**
     * Extremely minimal layout that avoids inline Box/Row/Column invocations.
     * We render checkbox and text using paddings to position text visually to the right.
     * This relies on the surrounding Column from the caller to stack items vertically.
     */
    // First item: checkbox (touch target at start)
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.secondary
        )
    )
    // Second item: label text with left padding so it appears to the right of the checkbox visually.
    // The visual alignment is acceptable for our simple use-case and avoids inline layout composables.
    Text(
        text = "Mark as completed",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp) // approximate space for checkbox
    )
}
