package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val title: String,
    val dueAtMillis: Long,
    val category: String, // "deadline", "assignment", "class"
    val priority: String, // "Cao", "Trung bình", "Thấp"
    val place: String?,
    val notes: String?,
    val isCompleted: Boolean
)
