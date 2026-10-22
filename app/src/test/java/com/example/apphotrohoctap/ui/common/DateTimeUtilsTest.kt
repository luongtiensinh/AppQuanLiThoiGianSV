package com.example.apphotrohoctap.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DateTimeUtilsTest {

    @Test
    fun toDueAtMillis_correctlyConstructsLocalTime() {
        // Arrange
        // (a) 15/11/2026
        // UTC midnight for Nov 15, 2026
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.NOVEMBER) // 10
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dateUtcMillis = utcCalendar.timeInMillis
        val zoneDaNang = TimeZone.getTimeZone("Asia/Da_Nang") // UTC+7

        // Act (a) 08:30
        val resultMillis = DateTimeUtils.toDueAtMillis(
            dateUtcMillis = dateUtcMillis,
            hour = 8,
            minute = 30,
            zone = zoneDaNang
        )

        // Assert
        val resultCalendar = Calendar.getInstance(zoneDaNang).apply {
            timeInMillis = resultMillis
        }
        
        // (b) ngày không bị lệch
        assertEquals("Year should be 2026", 2026, resultCalendar.get(Calendar.YEAR))
        assertEquals("Month should be November (10)", Calendar.NOVEMBER, resultCalendar.get(Calendar.MONTH))
        assertEquals("Day should be 15", 15, resultCalendar.get(Calendar.DAY_OF_MONTH))
        assertEquals("Hour should be 8", 8, resultCalendar.get(Calendar.HOUR_OF_DAY))
        assertEquals("Minute should be 30", 30, resultCalendar.get(Calendar.MINUTE))
        assertEquals("Second should be 0", 0, resultCalendar.get(Calendar.SECOND))
        assertEquals("Millisecond should be 0", 0, resultCalendar.get(Calendar.MILLISECOND))
    }

    @Test
    fun toDueAtMillis_endOfDay_isCorrect() {
        // Arrange
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.DECEMBER) // 11
            set(Calendar.DAY_OF_MONTH, 31)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dateUtcMillis = utcCalendar.timeInMillis
        val zoneDaNang = TimeZone.getTimeZone("Asia/Da_Nang") // UTC+7

        // Act (c) trường hợp 23:59
        val resultMillis = DateTimeUtils.toDueAtMillis(
            dateUtcMillis = dateUtcMillis,
            hour = 23,
            minute = 59,
            zone = zoneDaNang
        )

        // Assert
        val resultCalendar = Calendar.getInstance(zoneDaNang).apply {
            timeInMillis = resultMillis
        }
        
        assertEquals(2026, resultCalendar.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, resultCalendar.get(Calendar.MONTH))
        assertEquals(31, resultCalendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, resultCalendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, resultCalendar.get(Calendar.MINUTE))
        assertEquals(0, resultCalendar.get(Calendar.SECOND))
        assertEquals(0, resultCalendar.get(Calendar.MILLISECOND))
    }
}
