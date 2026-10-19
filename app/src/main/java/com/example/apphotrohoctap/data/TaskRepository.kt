package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasks(): Flow<List<TaskWithSubtasks>> = taskDao.getAllTasks()
    fun getTasksBySubject(subject: String): Flow<List<TaskWithSubtasks>> = taskDao.getTasksBySubject(subject)
    suspend fun getTaskById(id: Long): TaskWithSubtasks? = taskDao.getTaskById(id)
    
    suspend fun addTaskWithSubtasks(task: TaskEntity, subtasks: List<String>) {
        val taskId = taskDao.insertTask(task)
        if (subtasks.isNotEmpty()) {
            val subtaskEntities = subtasks.map { SubtaskEntity(taskId = taskId, title = it, isCompleted = false) }
            taskDao.insertSubtasks(subtaskEntities)
        }
    }
    
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun updateSubtask(subtask: SubtaskEntity) = taskDao.updateSubtask(subtask)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun deleteSubtasksByTaskId(taskId: Long) = taskDao.deleteSubtasksByTaskId(taskId)
}
