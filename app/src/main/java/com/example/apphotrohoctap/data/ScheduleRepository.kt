package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    fun getAllSchedules(): Flow<List<ScheduleEntity>> = scheduleDao.getAllSchedules()
    fun getSchedulesBySubject(subject: String): Flow<List<ScheduleEntity>> = scheduleDao.getSchedulesBySubject(subject)
    suspend fun addSchedule(schedule: ScheduleEntity) = scheduleDao.insertSchedule(schedule)
    suspend fun updateSchedule(schedule: ScheduleEntity) = scheduleDao.updateSchedule(schedule)
    suspend fun deleteSchedule(schedule: ScheduleEntity) = scheduleDao.deleteSchedule(schedule)
}
