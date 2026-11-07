package com.example.apphotrohoctap.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val courseName: String,
    val colorKey: String, // Chuỗi lưu màu nền hiển thị
    @ColumnInfo(name = "updatedAtMillis")
    val updatedAt: Long,
    val syncId: String = UUID.randomUUID().toString(),
    val isDeleted: Boolean = false
)
