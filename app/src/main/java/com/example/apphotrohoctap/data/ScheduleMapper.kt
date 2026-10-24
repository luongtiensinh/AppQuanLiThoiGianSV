package com.example.apphotrohoctap.data

import androidx.compose.ui.graphics.Color
import com.example.apphotrohoctap.ui.schedule.StudyClass
import java.util.Calendar

internal fun ScheduleEntity.toUiModel(now: Long): StudyClass {
    val startTimes = listOf("06:45", "07:30", "08:25", "09:20", "10:05", "10:50", "12:30", "13:15", "14:10", "15:05", "15:50", "16:35", "18:00")
    val endTimes = listOf("07:30", "08:15", "09:10", "10:05", "10:50", "11:35", "13:15", "14:00", "14:55", "15:50", "16:35", "17:20", "20:15")

    val sIdx = (startPeriod - 1).coerceIn(0, 12)
    val eIdx = (endPeriod - 1).coerceIn(0, 12)
    val timeString = "${startTimes[sIdx]} - ${endTimes[eIdx]}"

    val session = when {
        startPeriod <= 6 -> "Ca sáng"
        startPeriod <= 12 -> "Ca chiều"
        else -> "Ca tối"
    }

    val leadingColor = when (colorKey) {
        "0" -> Color(0xFF2563EB)
        "1" -> Color(0xFFE11D48)
        "2" -> Color(0xFF16A34A)
        "3" -> Color(0xFFD97706)
        "4" -> Color(0xFF5B5FEF)
        "5" -> Color(0xFF0D9488)
        else -> Color(0xFF5B5FEF)
    }

    val cal = Calendar.getInstance().apply { timeInMillis = now }
    // Calendar.MONDAY is 2 -> convert to 1 (T2) - 7 (CN)
    val currentDay = if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
    val currentHour = cal.get(Calendar.HOUR_OF_DAY)
    val currentMin = cal.get(Calendar.MINUTE)
    val currentTotalMin = currentHour * 60 + currentMin

    val startTotalMin = startTimes[sIdx].split(":").let { it[0].toInt() * 60 + it[1].toInt() }
    val endTotalMin = endTimes[eIdx].split(":").let { it[0].toInt() * 60 + it[1].toInt() }

    var status = "Sắp tới"
    var statusColor = Color(0xFFE8E6FC)
    var statusTextColor = Color(0xFF464555)

    if (dayOfWeek == currentDay) {
        if (currentTotalMin in startTotalMin..endTotalMin) {
            status = "Đang diễn ra"
            statusColor = Color(0xFFE1E0FF)
            statusTextColor = Color(0xFF2B2BC5)
        } else if (currentTotalMin < startTotalMin) {
            status = "Hôm nay"
        } else {
            status = "Đã kết thúc"
        }
    }

    return StudyClass(
        id = this.id,
        time = timeString,
        session = session,
        status = status,
        title = subject,
        place = room,
        teacher = lecturer,
        leadingColor = leadingColor,
        statusColor = statusColor,
        statusTextColor = statusTextColor
    )
}
