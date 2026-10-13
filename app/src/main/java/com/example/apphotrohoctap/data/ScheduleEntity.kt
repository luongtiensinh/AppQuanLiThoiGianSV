package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val courseCode: String,
    val dayOfWeek: Int, // 1 (Thứ 2) - 7 (Chủ Nhật)
    val startPeriod: Int,
    val endPeriod: Int,
    val room: String,
    val lecturer: String,
    val repeatWeekly: Boolean,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val colorKey: String // Mã màu (0-5)
)
