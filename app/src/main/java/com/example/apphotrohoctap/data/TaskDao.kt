package com.example.apphotrohoctap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY dueAtMillis ASC")
    fun getAllTasks(): Flow<List<TaskWithSubtasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND subject COLLATE NOCASE = :subject ORDER BY dueAtMillis ASC")
    fun getTasksBySubject(subject: String): Flow<List<TaskWithSubtasks>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND id = :id")
    suspend fun getTaskById(id: Long): TaskWithSubtasks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<SubtaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Update
    suspend fun updateSubtask(subtask: SubtaskEntity)

    @Query("UPDATE tasks SET isDeleted = 1, updatedAt = :timestamp WHERE id = :taskId")
    suspend fun softDeleteTask(taskId: Long, timestamp: Long)

    @Query("UPDATE subtasks SET isDeleted = 1, updatedAt = :timestamp WHERE subtaskId = :subtaskId")
    suspend fun softDeleteSubtask(subtaskId: Long, timestamp: Long)

    @Query("UPDATE subtasks SET isDeleted = 1, updatedAt = :timestamp WHERE taskId = :taskId")
    suspend fun softDeleteSubtasksByTaskId(taskId: Long, timestamp: Long)

    @Transaction
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND isCompleted = 0 AND dueAtMillis > :now AND dueAtMillis <= :next24Hours ORDER BY dueAtMillis ASC")
    suspend fun getUpcomingDeadlines(now: Long, next24Hours: Long): List<TaskWithSubtasks>
}
