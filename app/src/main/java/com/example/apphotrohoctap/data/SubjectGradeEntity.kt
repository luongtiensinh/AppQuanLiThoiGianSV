package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "subject_grades",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["semesterId"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE // Xóa học kỳ -> xóa luôn các môn trong học kỳ đó
        )
    ],
    indices = [Index(value = ["semesterId"])]
)
data class SubjectGradeEntity(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Long = 0,
    val semesterId: Long, // ID của học kỳ chứa môn học này
    val subjectName: String,
    val credits: Int,
    val targetGrade: String, // Mục tiêu điểm chữ, ví dụ: "A", "B+"
    val syncId: String = UUID.randomUUID().toString(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
