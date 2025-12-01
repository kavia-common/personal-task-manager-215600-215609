package org.example.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for tasks backed by LocalTaskStore (SharedPreferences JSON).
 * Note: No Room/SQL usage; all persistence is local JSON for simplicity.
 */
class TaskRepository private constructor(private val store: LocalTaskStore) {

    companion object {
        // PUBLIC_INTERFACE
        fun create(context: Context): TaskRepository {
            /** Factory to create repository backed by LocalTaskStore (SharedPreferences). */
            return TaskRepository(LocalTaskStore(context))
        }
    }

    // PUBLIC_INTERFACE
    fun observeTasks(): Flow<List<Task>> {
        /** Observe all tasks as a Flow sorted by completion then recency. */
        return store.tasks
    }

    // PUBLIC_INTERFACE
    fun observeTask(id: Long): Flow<Task?> {
        /** Observe a single task by id. */
        return store.tasks.map { list -> list.find { it.id == id } }
    }

    // PUBLIC_INTERFACE
    suspend fun addTask(title: String, description: String): Long {
        /** Add a new task with title and description. Returns new id. */
        return store.add(title.trim(), description.trim())
    }

    // PUBLIC_INTERFACE
    suspend fun updateTask(id: Long, title: String, description: String, completed: Boolean) {
        /** Update an existing task identified by id. */
        store.update(id, title.trim(), description.trim(), completed)
    }

    // PUBLIC_INTERFACE
    suspend fun deleteTask(id: Long) {
        /** Delete a task by id. */
        store.delete(id)
    }

    // PUBLIC_INTERFACE
    suspend fun toggleCompleted(id: Long, completed: Boolean) {
        /** Toggle completion state for a task. */
        store.setCompleted(id, completed)
    }
}
