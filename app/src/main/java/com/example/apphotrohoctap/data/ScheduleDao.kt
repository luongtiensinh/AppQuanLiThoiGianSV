package com.example.apphotrohoctap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE isDeleted = 0 ORDER BY startPeriod ASC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE isDeleted = 0 AND subject COLLATE NOCASE = :subject ORDER BY startPeriod ASC")
    fun getSchedulesBySubject(subject: String): Flow<List<ScheduleEntity>>

    @Query("SELECT DISTINCT subject FROM schedules WHERE isDeleted = 0 ORDER BY subject ASC")
    fun getDistinctSubjectNames(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Query("UPDATE schedules SET isDeleted = 1, updatedAt = :timestamp WHERE id = :scheduleId")
    suspend fun softDeleteSchedule(scheduleId: Long, timestamp: Long)

    @Query("SELECT * FROM schedules WHERE updatedAt > :lastSyncTime")
    suspend fun getModifiedSchedules(lastSyncTime: Long): List<ScheduleEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleMany(schedules: List<ScheduleEntity>)
}
