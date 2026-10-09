package com.example.apphotrohoctap.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.TaskEntity
import com.example.apphotrohoctap.data.TaskRepository
import com.example.apphotrohoctap.data.toUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: StateFlow<List<TaskItem>> = repository.getTasks()
        .map { entities ->
            val now = System.currentTimeMillis()
            entities.map { it.toUiModel(now) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun toggleCompleted(id: String) {
        viewModelScope.launch {
            val taskId = id.toLongOrNull() ?: return@launch
            val task = repository.getTaskById(taskId)
            if (task != null) {
                repository.updateTask(task.copy(isCompleted = !task.isCompleted))
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            val taskId = id.toLongOrNull() ?: return@launch
            val task = repository.getTaskById(taskId)
            if (task != null) {
                repository.deleteTask(task)
            }
        }
    }
}
