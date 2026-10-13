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

    fun addTaskWithSubtasks(task: TaskEntity, subtasks: List<String>) {
        viewModelScope.launch {
            repository.addTaskWithSubtasks(task, subtasks)
        }
    }

    fun toggleCompleted(id: String) {
        viewModelScope.launch {
            val taskId = id.toLongOrNull() ?: return@launch
            val taskWithSubtasks = repository.getTaskById(taskId)
            if (taskWithSubtasks != null) {
                repository.updateTask(taskWithSubtasks.task.copy(isCompleted = !taskWithSubtasks.task.isCompleted))
            }
        }
    }

    fun toggleSubtask(taskId: Long, subtaskId: Long) {
        viewModelScope.launch {
            val taskWithSubtasks = repository.getTaskById(taskId)
            val subtask = taskWithSubtasks?.subtasks?.find { it.subtaskId == subtaskId }
            if (subtask != null) {
                repository.updateSubtask(subtask.copy(isCompleted = !subtask.isCompleted))
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            val taskId = id.toLongOrNull() ?: return@launch
            val taskWithSubtasks = repository.getTaskById(taskId)
            if (taskWithSubtasks != null) {
                repository.deleteSubtasksByTaskId(taskId)
                repository.deleteTask(taskWithSubtasks.task)
            }
        }
    }
}
