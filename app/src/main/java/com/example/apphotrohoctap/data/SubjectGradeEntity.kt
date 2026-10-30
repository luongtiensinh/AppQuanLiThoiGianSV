package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject_grades")
data class SubjectGradeEntity(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Long = 0,
    val subjectName: String,
    val credits: Int,
    val targetGrade: String // Mục tiêu điểm chữ, ví dụ: "A", "B+"
)
