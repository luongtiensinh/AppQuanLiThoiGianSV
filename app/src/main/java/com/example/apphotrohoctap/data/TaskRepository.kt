package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasks(): Flow<List<TaskWithSubtasks>> = taskDao.getAllTasks()
    fun getTasksBySubject(subject: String): Flow<List<TaskWithSubtasks>> = taskDao.getTasksBySubject(subject)
    suspend fun getTaskById(id: Long): TaskWithSubtasks? = taskDao.getTaskById(id)
    
    suspend fun addTaskWithSubtasks(task: TaskEntity, subtasks: List<String>) {
        val taskId = taskDao.insertTask(task.copy(updatedAt = System.currentTimeMillis()))
        if (subtasks.isNotEmpty()) {
            val subtaskEntities = subtasks.map { 
                SubtaskEntity(taskId = taskId, title = it, isCompleted = false, updatedAt = System.currentTimeMillis()) 
            }
            taskDao.insertSubtasks(subtaskEntities)
        }
    }
    
    suspend fun addSubtask(taskId: Long, title: String) {
        taskDao.insertSubtask(SubtaskEntity(taskId = taskId, title = title, isCompleted = false, updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    suspend fun updateSubtask(subtask: SubtaskEntity) = taskDao.updateSubtask(subtask.copy(updatedAt = System.currentTimeMillis()))
    suspend fun deleteTask(taskId: Long) = taskDao.softDeleteTask(taskId, System.currentTimeMillis())
    suspend fun deleteSubtask(subtaskId: Long) = taskDao.softDeleteSubtask(subtaskId, System.currentTimeMillis())
    suspend fun deleteSubtasksByTaskId(taskId: Long) = taskDao.softDeleteSubtasksByTaskId(taskId, System.currentTimeMillis())
}
