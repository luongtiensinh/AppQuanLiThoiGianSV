package com.example.apphotrohoctap.data

import androidx.compose.ui.graphics.Color
import com.example.apphotrohoctap.ui.schedule.StudyClass
import java.util.Calendar

internal fun ScheduleEntity.toUiModel(now: Long): StudyClass {
    val startTimes = listOf("06:45", "07:30", "08:25", "09:20", "10:05", "10:50", "12:30", "13:15", "14:10", "15:05", "15:50", "16:35", "18:00")
    val endTimes = listOf("07:30", "08:15", "09:10", "10:05", "10:50", "11:35", "13:15", "14:00", "14:55", "15:50", "16:35", "17:20", "20:15")

    val isPackedStart = startPeriod >= 10000
    val isPackedEnd = endPeriod >= 10000

    val sPeriod = if (isPackedStart) startPeriod / 10000 else startPeriod
    val ePeriod = if (isPackedEnd) endPeriod / 10000 else endPeriod

    val sIdx = (sPeriod - 1).coerceIn(0, 12)
    val eIdx = (ePeriod - 1).coerceIn(0, 12)

    val sHour = if (isPackedStart) (startPeriod % 10000) / 100 else startTimes[sIdx].split(":")[0].toInt()
    val sMin = if (isPackedStart) startPeriod % 100 else startTimes[sIdx].split(":")[1].toInt()

    val eHour = if (isPackedEnd) (endPeriod % 10000) / 100 else endTimes[eIdx].split(":")[0].toInt()
    val eMin = if (isPackedEnd) endPeriod % 100 else endTimes[eIdx].split(":")[1].toInt()

    val timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d - %02d:%02d", sHour, sMin, eHour, eMin)

    val session = when {
        sHour < 12 -> "Ca sáng"
        sHour < 18 -> "Ca chiều"
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

    val startTotalMin = sHour * 60 + sMin
    val endTotalMin = eHour * 60 + eMin

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
