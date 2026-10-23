package com.example.apphotrohoctap.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class AcademicCalendarTest {

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

    @Test
    fun currentSemester_beforeSem1_returnsSem1() {
        val now = createDate(2026, Calendar.AUGUST, 15) // Before Aug 31
        val sem = AcademicCalendar.currentSemester(now)
        assertEquals("Học kỳ 1", sem.semesterName)
    }

    @Test
    fun weekOfSemester_beforeSem1_returnsMinusOne() {
        val now = createDate(2026, Calendar.AUGUST, 15) // Before Aug 31
        val week = AcademicCalendar.weekOfSemester(now)
        assertEquals(-1, week)
    }

    @Test
    fun weekOfSemester_inSem1_week1() {
        val now = createDate(2026, Calendar.AUGUST, 31) // First day
        val week = AcademicCalendar.weekOfSemester(now)
        assertEquals(1, week)
    }

    @Test
    fun weekOfSemester_inSem1_week2() {
        val now = createDate(2026, Calendar.SEPTEMBER, 8) // 8 days after
        val week = AcademicCalendar.weekOfSemester(now)
        assertEquals(2, week)
    }

    @Test
    fun currentSemester_inSem2_returnsSem2() {
        val now = createDate(2027, Calendar.MARCH, 15)
        val sem = AcademicCalendar.currentSemester(now)
        assertEquals("Học kỳ 2", sem.semesterName)
        
        val week = AcademicCalendar.weekOfSemester(now)
        // Feb 1 to Mar 15 is approx 42 days => Week 7
        assertEquals(7, week)
    }

    @Test
    fun labels_areFormattedCorrectly() {
        val nowSem1 = createDate(2026, Calendar.NOVEMBER, 1)
        assertEquals("Học kỳ 1 (2026 - 2027)", AcademicCalendar.semesterLabel(nowSem1))
        assertEquals("HK I (2026 - 2027)", AcademicCalendar.semesterLabelShort(nowSem1))
        assertEquals("Học kỳ 1 • Năm học 2026-2027", AcademicCalendar.semesterHeaderLabel(nowSem1))

        val nowSem2 = createDate(2027, Calendar.APRIL, 1)
        assertEquals("Học kỳ 2 (2026 - 2027)", AcademicCalendar.semesterLabel(nowSem2))
        assertEquals("HK II (2026 - 2027)", AcademicCalendar.semesterLabelShort(nowSem2))
        assertEquals("Học kỳ 2 • Năm học 2026-2027", AcademicCalendar.semesterHeaderLabel(nowSem2))
    }

    @Test
    fun generateWeekDays_startsOnMonday() {
        // Nov 1, 2026 is Sunday
        val now = createDate(2026, Calendar.NOVEMBER, 1)
        val weekDays = AcademicCalendar.generateWeekDays(now)
        
        assertEquals(7, weekDays.size)
        assertEquals("T2", weekDays[0].weekdayLabel)
        assertEquals("26", weekDays[0].dayOfMonth) // Monday of that week was Oct 26
        assertEquals("CN", weekDays[6].weekdayLabel)
        assertEquals("01", weekDays[6].dayOfMonth)
    }
}
