package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String = "",
    val courseCode: String = "",
    val dayOfWeek: Int = 1, // 1 (Thứ 2) - 7 (Chủ Nhật)
    val startPeriod: Int = 1,
    val endPeriod: Int = 1,
    val room: String = "",
    val lecturer: String = "",
    val repeatWeekly: Boolean = true,
    val startDateMillis: Long = 0L,
    val endDateMillis: Long = 0L,
    val colorKey: String = "0", // Mã màu (0-5)
    val syncId: String = UUID.randomUUID().toString(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
