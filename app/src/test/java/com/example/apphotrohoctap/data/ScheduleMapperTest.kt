package com.example.apphotrohoctap.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class ScheduleMapperTest {

    // Helper: Tạo thời điểm giả định -> Thứ 3, 10:00 sáng
    private fun getMockNowTuesday10AM(): Long {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.OCTOBER, 20, 10, 0, 0) // Ngày 20/10/2026 là Thứ 3
        return cal.timeInMillis
    }

    private fun createSchedule(dayOfWeek: Int, startPeriod: Int, endPeriod: Int): ScheduleEntity {
        return ScheduleEntity(
            id = 1L,
            subject = "Toán",
            courseCode = "MI1111",
            dayOfWeek = dayOfWeek,
            startPeriod = startPeriod,
            endPeriod = endPeriod,
            room = "D3-505",
            lecturer = "Thầy Hùng",
            repeatWeekly = true,
            startDateMillis = 0L,
            endDateMillis = 0L,
            colorKey = "1"
        )
    }

    @Test
    fun schedulePeriod1To3_mapsToCorrectTimeAndMorningSession() {
        // Arrange
        val now = getMockNowTuesday10AM()
        val schedule = createSchedule(dayOfWeek = 2, startPeriod = 1, endPeriod = 3)

        // Act
        val uiModel = schedule.toUiModel(now)

        // Assert
        assertEquals("06:45 - 09:10", uiModel.time)
        assertEquals("Ca sáng", uiModel.session)
    }

    @Test
    fun scheduleOnSameDay_inProgress_isMarkedDangDienRa() {
        // Arrange
        val now = getMockNowTuesday10AM() // Thứ 3, 10:00
        val schedule = createSchedule(
            dayOfWeek = 2, // Thứ 3
            startPeriod = 4, // 09:20
            endPeriod = 6  // 11:35
        )

        // Act
        val uiModel = schedule.toUiModel(now)

        // Assert
        assertEquals("Đang diễn ra", uiModel.status)
    }

    @Test
    fun scheduleOnSameDay_passed_isMarkedDaKetThuc() {
        // Arrange
        val now = getMockNowTuesday10AM() // Thứ 3, 10:00
        val schedule = createSchedule(
            dayOfWeek = 2, // Thứ 3
            startPeriod = 1, // 06:45
            endPeriod = 3  // 09:10
        )

        // Act
        val uiModel = schedule.toUiModel(now)

        // Assert
        assertEquals("Đã kết thúc", uiModel.status)
    }

    @Test
    fun scheduleOnDifferentDay_isMarkedSapToi() {
        // Arrange
        val now = getMockNowTuesday10AM() // Thứ 3
        val schedule = createSchedule(
            dayOfWeek = 4, // Thứ 5
            startPeriod = 1,
            endPeriod = 3
        )

        // Act
        val uiModel = schedule.toUiModel(now)

        // Assert
        assertEquals("Sắp tới", uiModel.status)
    }
}
