package com.example.apphotrohoctap.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.TaskEntity
import com.example.apphotrohoctap.data.TaskRepository
import com.example.apphotrohoctap.data.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.apphotrohoctap.ui.common.DateTimeUtils

data class AddTaskFormState(
    val title: String = "",
    val selectedCourse: String = "",
    val selectedCategory: String = "Deadline",
    val selectedDateUtcMillis: Long = DateTimeUtils.getLocalDayUtcMillis(0),
    val selectedHour: Int = 23,
    val selectedMinute: Int = 59,
    val reminderEnabled: Boolean = true,
    val selectedPriority: String = "Cao",
    val notes: String = "",
    val subtaskText: String = "",
    val subtasks: List<String> = emptyList()
)

internal class TasksViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _addTaskFormState = MutableStateFlow(AddTaskFormState())
    val addTaskFormState: StateFlow<AddTaskFormState> = _addTaskFormState.asStateFlow()

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

    fun updateTitle(title: String) { _addTaskFormState.update { it.copy(title = title) } }
    fun updateCourse(course: String) { _addTaskFormState.update { it.copy(selectedCourse = course) } }
    fun updateCategory(category: String) { _addTaskFormState.update { it.copy(selectedCategory = category) } }
    fun updateDate(millis: Long) { _addTaskFormState.update { it.copy(selectedDateUtcMillis = millis) } }
    fun updateTime(hour: Int, minute: Int) { _addTaskFormState.update { it.copy(selectedHour = hour, selectedMinute = minute) } }
    fun updateReminder(enabled: Boolean) { _addTaskFormState.update { it.copy(reminderEnabled = enabled) } }
    fun updatePriority(priority: String) { _addTaskFormState.update { it.copy(selectedPriority = priority) } }
    fun updateNotes(notes: String) { _addTaskFormState.update { it.copy(notes = notes) } }
    
    fun updateSubtaskText(text: String) { _addTaskFormState.update { it.copy(subtaskText = text) } }
    fun addSubtask() {
        _addTaskFormState.update { state ->
            val text = state.subtaskText.trim()
            if (text.isNotBlank()) {
                state.copy(subtasks = state.subtasks + text, subtaskText = "")
            } else state
        }
    }
    fun clearAddTaskForm() {
        _addTaskFormState.value = AddTaskFormState()
    }

    /**
     * @return true nếu hạn chót hợp lệ và đã được lưu thành công.
     *         false nếu hạn chót nằm trong quá khứ (cần xác nhận).
     */
    fun saveTask(forceSave: Boolean = false): Boolean {
        val state = _addTaskFormState.value
        val dueAtMillis = DateTimeUtils.toDueAtMillis(state.selectedDateUtcMillis, state.selectedHour, state.selectedMinute)
        
        if (!forceSave && dueAtMillis < System.currentTimeMillis()) {
            return false
        }
        
        val categoryKey = when (state.selectedCategory) {
            "Deadline" -> "deadline"
            "Lịch học" -> "class"
            else -> "assignment"
        }
        
        val newEntity = TaskEntity(
            subject = state.selectedCourse,
            title = state.title,
            dueAtMillis = dueAtMillis,
            category = categoryKey,
            priority = state.selectedPriority,
            place = null,
            notes = state.notes,
            isCompleted = false
        )
        
        addTaskWithSubtasks(newEntity, state.subtasks)
        clearAddTaskForm()
        return true
    }

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
