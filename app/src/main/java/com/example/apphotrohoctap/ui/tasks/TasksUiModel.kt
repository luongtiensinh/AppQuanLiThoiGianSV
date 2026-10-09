package com.example.apphotrohoctap.ui.tasks

import androidx.compose.ui.graphics.Color

internal val TasksBackground = Color(0xFFFCF8FF)
internal val TasksPale = Color(0xFFF5F2FF)
internal val TasksChip = Color(0xFFEFECFF)
internal val TasksBrand = Color(0xFF4143D5)
internal val TasksBright = Color(0xFF5B5FEF)
internal val TasksInk = Color(0xFF1A1A2A)
internal val TasksSecondary = Color(0xFF464555)
internal val TasksMuted = Color(0xFF767586)
internal val TasksDivider = Color(0xFFE3E0F7)
internal val TasksRed = Color(0xFFBA1A1A)
internal val TasksRedPale = Color(0xFFFFDAD6)

internal data class TaskFilter(val label: String, val kind: String)

internal data class TaskItem(
    val id: String,
    val group: String,
    val subject: String,
    val title: String,
    val due: String,
    val category: String,
    val accent: Color,
    val urgency: String? = null,
    val place: String? = null,
    val completed: Boolean = false
)

internal val taskFilters = listOf(
    TaskFilter("Tất cả", "all"),
    TaskFilter("Lịch học", "class"),
    TaskFilter("Deadline", "deadline"),
    TaskFilter("Bài tập", "assignment"),
    TaskFilter("Đã xong", "done")
)
