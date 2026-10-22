package com.example.apphotrohoctap.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class AcademicCalendarTest {

    @Test
    fun weekOfSemester_returnsCorrectWeek() {
        // Arrange
        // SEMESTER_START is August 31, 2026.
        // Let's create a date 7 days after SEMESTER_START (which means it's Week 2)
        val now = AcademicCalendar.SEMESTER_START + 7L * 24 * 60 * 60 * 1000

        // Act
        val week = AcademicCalendar.weekOfSemester(now)

        // Assert
        assertEquals(2, week)
    }

    @Test
    fun weekOfSemester_beforeSemester_returnsMinusOne() {
        // Arrange
        // Let's create a date 1 day before SEMESTER_START
        val now = AcademicCalendar.SEMESTER_START - 1L * 24 * 60 * 60 * 1000

        // Act
        val week = AcademicCalendar.weekOfSemester(now)

        // Assert
        assertEquals(-1, week)
    }

    @Test
    fun semesterLabel_returnsCorrectString() {
        // Act
        val label = AcademicCalendar.semesterLabel(System.currentTimeMillis())

        // Assert
        assertEquals("Học kỳ 1 (2026 - 2027)", label)
    }
}
