package org.example.app.data

/**
 * Plain data model representing a Task.
 * Persistence is handled by LocalTaskStore (SharedPreferences JSON), not Room.
 */
data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
