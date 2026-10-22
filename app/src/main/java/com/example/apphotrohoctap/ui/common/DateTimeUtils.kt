package com.example.apphotrohoctap.ui.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {
    /**
     * Chuyển đổi ngày (được chọn từ Material 3 DatePicker dưới dạng millis theo múi giờ UTC)
     * và giờ/phút thành millis cục bộ hợp lệ cho hạn chót (dueAtMillis).
     *
     * Giải thích: DatePicker của Material 3 trả về timestamp tính bằng millis tính từ Epoch
     * tại nửa đêm (00:00) theo giờ UTC (Coordinated Universal Time).
     * Tuy nhiên, hệ thống Android (và Calendar.getInstance()) mặc định chạy theo múi giờ
     * cục bộ của thiết bị. Ví dụ ở Việt Nam (UTC+7), 00:00 UTC tương đương với 07:00 sáng
     * theo giờ địa phương. Nếu ta cộng thêm giờ/phút hoặc chuyển thẳng millis này cho
     * Calendar cục bộ để hiển thị, nó sẽ hiểu là 07:00 sáng của ngày đó, và nếu cộng thêm
     * thời gian thì có thể bị lệch sang ngày khác hoặc sai giờ.
     * Hàm này trích xuất tường minh [năm, tháng, ngày] từ millis UTC, sau đó dùng các
     * giá trị đó để thiết lập một Calendar mới theo múi giờ hiện tại, đảm bảo ngày không
     * bị dịch chuyển.
     */
    fun toDueAtMillis(
        dateUtcMillis: Long,
        hour: Int,
        minute: Int,
        zone: TimeZone = TimeZone.getDefault()
    ): Long {
        // Đọc năm, tháng, ngày từ UTC millis
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = dateUtcMillis
        }
        val year = utcCalendar.get(Calendar.YEAR)
        val month = utcCalendar.get(Calendar.MONTH)
        val day = utcCalendar.get(Calendar.DAY_OF_MONTH)

        // Dựng lại thời gian theo múi giờ cục bộ
        val localCalendar = Calendar.getInstance(zone).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return localCalendar.timeInMillis
    }

    /**
     * Định dạng millis thành chuỗi ngày hiển thị (VD: Thứ Bảy, 27/04/2024).
     */
    fun formatToDateString(millis: Long): String {
        val formatter = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.forLanguageTag("vi-VN"))
        return formatter.format(millis)
    }

    /**
     * Lấy millis của nửa đêm (00:00 UTC) cho một ngày tương đối so với hôm nay
     * (VD: offsetDays = 0 là hôm nay, 1 là ngày mai).
     */
    fun getLocalDayUtcMillis(offsetDays: Int = 0): Long {
        val localNow = Calendar.getInstance()
        localNow.add(Calendar.DAY_OF_YEAR, offsetDays)
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, localNow.get(Calendar.YEAR))
            set(Calendar.MONTH, localNow.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, localNow.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return utcCal.timeInMillis
    }
}
