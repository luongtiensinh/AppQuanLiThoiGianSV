package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semesters")
data class SemesterEntity(
    @PrimaryKey(autoGenerate = true)
    val semesterId: Long = 0,
    val semesterName: String, // Ví dụ: "Học kỳ 1, 2023-2024"
    val isCurrent: Boolean = false // Đánh dấu học kỳ hiện tại
)
