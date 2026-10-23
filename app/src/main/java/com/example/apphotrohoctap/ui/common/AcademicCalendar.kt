package com.example.apphotrohoctap.ui.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Cấu hình cho một học kỳ học thuật.
 */
data class AcademicSemesterConfig(
    val startMillis: Long,
    val endMillis: Long,
    val semesterName: String, // Ví dụ: "Học kỳ 1"
    val schoolYearStart: Int, // Ví dụ: 2026
    val schoolYearEnd: Int    // Ví dụ: 2027
)

/**
 * Đại diện cho một ngày trên giao diện (UI) Carousel.
 */
data class WeekDayUi(
    val weekdayLabel: String,
    val dayOfMonth: String
)

/**
 * Nguồn dữ liệu thời gian học thuật trung tâm (Single Source of Truth),
 * sử dụng cấu hình động thay vì chuỗi tĩnh cứng.
 */
object AcademicCalendar {

    // Helper tạo timestamp an toàn
    private fun createDate(year: Int, month: Int, day: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Cấu hình linh hoạt các học kỳ
    private val semesters = listOf(
        AcademicSemesterConfig(
            startMillis = createDate(2026, Calendar.AUGUST, 31),
            endMillis = createDate(2027, Calendar.JANUARY, 10),
            semesterName = "Học kỳ 1",
            schoolYearStart = 2026,
            schoolYearEnd = 2027
        ),
        AcademicSemesterConfig(
            startMillis = createDate(2027, Calendar.FEBRUARY, 1),
            endMillis = createDate(2027, Calendar.JUNE, 15),
            semesterName = "Học kỳ 2",
            schoolYearStart = 2026,
            schoolYearEnd = 2027
        )
    )

    /**
     * Xác định học kỳ hiện tại dựa vào thời điểm [now].
     */
    fun currentSemester(now: Long): AcademicSemesterConfig {
        // Lấy học kỳ đầu tiên có thời gian kết thúc ở tương lai
        // Nếu đã qua tất cả các học kỳ cấu hình, lấy học kỳ cuối cùng
        return semesters.firstOrNull { now <= it.endMillis } ?: semesters.last()
    }

    fun weekOfSemester(now: Long): Int {
        val current = currentSemester(now)
        if (now < current.startMillis) return -1
        
        val diffMillis = now - current.startMillis
        val days = diffMillis / (1000L * 60 * 60 * 24)
        return (days / 7).toInt() + 1
    }

    fun semesterLabel(now: Long): String {
        val current = currentSemester(now)
        return "${current.semesterName} (${current.schoolYearStart} - ${current.schoolYearEnd})"
    }

    fun semesterLabelShort(now: Long): String {
        val current = currentSemester(now)
        val romanNum = when (current.semesterName) {
            "Học kỳ 1" -> "I"
            "Học kỳ 2" -> "II"
            "Học kỳ 3" -> "III"
            else -> current.semesterName.replace("Học kỳ ", "")
        }
        return "HK $romanNum (${current.schoolYearStart} - ${current.schoolYearEnd})"
    }

    fun semesterHeaderLabel(now: Long): String {
        val current = currentSemester(now)
        return "${current.semesterName} • Năm học ${current.schoolYearStart}-${current.schoolYearEnd}"
    }

    fun formatTodayVi(now: Long): String {
        val formatter = SimpleDateFormat("EEEE, dd 'Tháng' MM", Locale.forLanguageTag("vi-VN"))
        return formatter.format(now)
    }

    fun monthYearLabel(now: Long): String {
        val formatter = SimpleDateFormat("'Tháng' M, yyyy", Locale.forLanguageTag("vi-VN"))
        return formatter.format(now)
    }

    fun currentDayString(now: Long): String {
        val formatter = SimpleDateFormat("dd", Locale.forLanguageTag("vi-VN"))
        return formatter.format(now)
    }

    fun generateWeekDays(now: Long): List<WeekDayUi> {
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        // Lùi về Thứ 2
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        val weekDays = mutableListOf<WeekDayUi>()
        val dayFormat = SimpleDateFormat("dd", Locale.forLanguageTag("vi-VN"))
        val weekdays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

        for (i in 0..6) {
            weekDays.add(WeekDayUi(weekdays[i], dayFormat.format(calendar.timeInMillis)))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return weekDays
    }
}
