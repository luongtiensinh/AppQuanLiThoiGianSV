package com.example.apphotrohoctap.ui.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Gom toàn bộ logic xử lý thời gian học thuật (ngày bắt đầu học kỳ, tuần học,...)
 * về một nơi duy nhất (Single Source of Truth) nhằm:
 * 1. Đảm bảo tính nhất quán giữa các màn hình (Home, Schedule, Settings).
 * 2. Dễ dàng bảo trì, cập nhật khi chuyển sang học kỳ mới mà không cần sửa code ở hàng loạt nơi.
 * 3. Dễ dàng viết Unit Test độc lập với UI.
 */
object AcademicCalendar {

    // Ngày bắt đầu học kỳ: 31/08/2026 (Thứ Hai)
    private val SEMESTER_START_CAL = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2026)
        set(Calendar.MONTH, Calendar.AUGUST)
        set(Calendar.DAY_OF_MONTH, 31)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val SEMESTER_START: Long = SEMESTER_START_CAL.timeInMillis

    /**
     * Tính tuần học hiện tại dựa trên mốc thời gian [now].
     */
    fun weekOfSemester(now: Long): Int {
        if (now < SEMESTER_START) return -1
        
        val diffMillis = now - SEMESTER_START
        val days = diffMillis / (1000 * 60 * 60 * 24)
        return (days / 7).toInt() + 1
    }

    /**
     * Lấy nhãn học kỳ (ví dụ: Học kỳ 1 (2026 - 2027))
     */
    fun semesterLabel(now: Long): String {
        return "Học kỳ 1 (2026 - 2027)"
    }
    
    /**
     * Lấy nhãn học kỳ ngắn (ví dụ: HK I (2026 - 2027))
     */
    fun semesterLabelShort(now: Long): String {
        return "HK I (2026 - 2027)"
    }

    /**
     * Định dạng ngày "Hôm nay" theo tiếng Việt (VD: Thứ Tư, 22 Tháng 10)
     */
    fun formatTodayVi(now: Long): String {
        val formatter = SimpleDateFormat("EEEE, dd 'Tháng' MM", Locale.forLanguageTag("vi-VN"))
        return formatter.format(now)
    }

    /**
     * Lấy nhãn Tháng Năm (VD: Tháng 10, 2026)
     */
    fun monthYearLabel(now: Long): String {
        val formatter = SimpleDateFormat("'Tháng' M, yyyy", Locale.forLanguageTag("vi-VN"))
        return formatter.format(now)
    }
}
