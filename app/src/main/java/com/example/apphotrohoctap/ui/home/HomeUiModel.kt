package com.example.apphotrohoctap.ui.home

import androidx.compose.ui.graphics.Color
import com.example.apphotrohoctap.ui.schedule.StudyClass

internal data class DeadlineTask(
    val id: String,
    val subject: String,
    val urgency: String,
    val title: String,
    val dueDate: String,
    val progress: Float,
    val progressLabel: String,
    val accent: Color,
    val urgencyBackground: Color,
    val urgencyColor: Color,
    val timeColor: Color,
    val urgencyIcon: String? = null,
    val dateIcon: String = "home_date.svg"
)

internal data class HomeUiState(
    val tasksToday: Int = 0,
    val tasksUrgent: Int = 0,
    val deadlinesThisWeek: Int = 0,
    val tasksDone: Int = 0,
    val tasksTotalThisWeek: Int = 0, // NEW
    val todaySchedules: List<StudyClass> = emptyList(),
    val upcomingDeadlines: List<DeadlineTask> = emptyList()
)
