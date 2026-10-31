package com.example.apphotrohoctap.ui.analytics

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progressText: String
)

data class DailyFocusData(
    val dayLabel: String,
    val focusMinutes: Int
)

data class WeeklyReportData(
    val totalFocusMinutes: Int,
    val completedTasksCount: Int,
    val totalTasksCount: Int,
    val dailyFocusList: List<DailyFocusData>
)
