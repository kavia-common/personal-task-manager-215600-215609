package org.example.app.data

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LocalTaskStore(
    private val context: Context,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    private val prefs by lazy { context.getSharedPreferences("tasks_store", Context.MODE_PRIVATE) }
    private val key = "tasks_json"

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        _tasks.value = load()
    }

    private fun load(): List<Task> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return runCatching { decode(json) }.getOrElse { emptyList() }
    }

    private fun persist(tasks: List<Task>) {
        prefs.edit().putString(key, encode(tasks)).apply()
    }

    private fun encode(tasks: List<Task>): String {
        val arr = JSONArray()
        tasks.forEach { t ->
            val o = JSONObject()
            o.put("id", t.id)
            o.put("title", t.title)
            o.put("description", t.description)
            o.put("completed", t.completed)
            o.put("createdAt", t.createdAt)
            o.put("updatedAt", t.updatedAt)
            arr.put(o)
        }
        return arr.toString()
    }

    private fun decode(json: String): List<Task> {
        val arr = JSONArray(json)
        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    Task(
                        id = o.getLong("id"),
                        title = o.getString("title"),
                        description = o.optString("description", ""),
                        completed = o.optBoolean("completed", false),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
        }
    }

    suspend fun add(title: String, description: String): Long = withContext(io) {
        val now = System.currentTimeMillis()
        val current = _tasks.value
        val newId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
        val updated: List<Task> = listOf(
            Task(id = newId, title = title, description = description, completed = false, createdAt = now, updatedAt = now)
        ) + current
        persist(updated)
        _tasks.value = updated
        newId
    }

    suspend fun update(id: Long, title: String, description: String, completed: Boolean) = withContext(io) {
        val now = System.currentTimeMillis()
        val updated: List<Task> = _tasks.value
            .map { t ->
                if (t.id == id) t.copy(title = title, description = description, completed = completed, updatedAt = now) else t
            }
            .sortedWith(compareBy<Task> { it.completed }.thenByDescending { it.updatedAt })
        persist(updated)
        _tasks.value = updated
    }

    suspend fun delete(id: Long) = withContext(io) {
        val updated: List<Task> = _tasks.value.filterNot { it.id == id }
        persist(updated)
        _tasks.value = updated
    }

    suspend fun setCompleted(id: Long, completed: Boolean) = withContext(io) {
        val now = System.currentTimeMillis()
        val updated: List<Task> = _tasks.value
            .map { t ->
                if (t.id == id) t.copy(completed = completed, updatedAt = now) else t
            }
            .sortedWith(compareBy<Task> { it.completed }.thenByDescending { it.updatedAt })
        persist(updated)
        _tasks.value = updated
    }
}
