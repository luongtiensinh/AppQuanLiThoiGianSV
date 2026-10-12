package com.example.apphotrohoctap.ui.schedule

import androidx.compose.ui.graphics.Color

internal data class StudyClass(
    val id: Long,
    val time: String,
    val session: String,
    val status: String,
    val title: String,
    val place: String,
    val teacher: String,
    val leadingColor: Color,
    val statusColor: Color,
    val statusTextColor: Color
)
