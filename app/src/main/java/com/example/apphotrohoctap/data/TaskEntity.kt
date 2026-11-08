package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String = "",
    val title: String = "",
    val dueAtMillis: Long = 0L,
    val category: String = "deadline", // "deadline", "assignment", "class"
    val priority: String = "Trung bình", // "Cao", "Trung bình", "Thấp"
    val place: String? = null,
    val notes: String? = null,
    val isCompleted: Boolean = false,
    val syncId: String = UUID.randomUUID().toString(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
