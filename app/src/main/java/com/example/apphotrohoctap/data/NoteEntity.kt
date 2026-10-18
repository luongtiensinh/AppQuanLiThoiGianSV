package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val courseName: String,
    val colorKey: String, // Chuỗi lưu màu nền hiển thị
    val updatedAtMillis: Long
)
