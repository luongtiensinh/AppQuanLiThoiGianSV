package com.example.apphotrohoctap.ui.analytics

import android.content.Context
import java.util.Calendar

object PomodoroStatsManager {
    private const val PREFS_NAME = "studyflow_pomo_stats"

    fun recordFocusMinutes(context: Context, minutes: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cal = Calendar.getInstance()
        val dayKey = getDayKey(cal.get(Calendar.DAY_OF_WEEK))
        val currentMins = prefs.getInt(dayKey, 0)
        prefs.edit().putInt(dayKey, currentMins + minutes).apply()
    }

    fun getWeeklyFocusList(context: Context): List<DailyFocusData> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        return days.map { day ->
            DailyFocusData(dayLabel = day, focusMinutes = prefs.getInt(day, 0))
        }
    }

    private fun getDayKey(calendarDayOfWeek: Int): String {
        return when (calendarDayOfWeek) {
            Calendar.MONDAY -> "T2"
            Calendar.TUESDAY -> "T3"
            Calendar.WEDNESDAY -> "T4"
            Calendar.THURSDAY -> "T5"
            Calendar.FRIDAY -> "T6"
            Calendar.SATURDAY -> "T7"
            Calendar.SUNDAY -> "CN"
            else -> "T2"
        }
    }
}
