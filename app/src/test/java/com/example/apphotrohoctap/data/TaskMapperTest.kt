package com.example.apphotrohoctap.data

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class TaskMapperTest {

    // Helper: Tạo thời điểm giả định làm mốc (now) -> 12:00 trưa
    private fun getMockNow(): Long {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.OCTOBER, 20, 12, 0, 0)
        return cal.timeInMillis
    }

    // Helper: Tạo TaskWithSubtasks nhanh chóng
    private fun createTestTask(
        dueAtMillis: Long,
        isCompleted: Boolean = false,
        priority: String = "Cao",
        category: String = "deadline",
        subtasks: List<SubtaskEntity> = emptyList()
    ): TaskWithSubtasks {
        return TaskWithSubtasks(
            task = TaskEntity(
                id = 1L,
                subject = "Toán",
                title = "Làm bài tập",
                dueAtMillis = dueAtMillis,
                category = category,
                priority = priority,
                place = "",
                notes = "",
                isCompleted = isCompleted
            ),
            subtasks = subtasks
        )
    }

    @Test
    fun taskDueIn10Hours_isGroupedAsHomNay_andMarkedAsGap() {
        // Arrange
        val now = getMockNow()
        val in10Hours = now + (10 * 60 * 60 * 1000L)
        val task = createTestTask(dueAtMillis = in10Hours)

        // Act
        val uiModel = task.toUiModel(now)

        // Assert
        assertEquals("Hôm nay", uiModel.group)
        assertEquals("Gấp • 10 giờ nữa", uiModel.urgency)
    }

    @Test
    fun taskDueIn30Hours_isGroupedAsNgayMai() {
        // Arrange
        val now = getMockNow()
        val in30Hours = now + (30 * 60 * 60 * 1000L)
        val task = createTestTask(dueAtMillis = in30Hours)

        // Act
        val uiModel = task.toUiModel(now)

        // Assert
        assertEquals("Ngày mai", uiModel.group)
        assertEquals("Sắp đến", uiModel.urgency)
    }

    @Test
    fun taskOverdue_isMarkedAsQuaHan() {
        // Arrange
        val now = getMockNow()
        val past2Hours = now - (2 * 60 * 60 * 1000L)
        val task = createTestTask(dueAtMillis = past2Hours)

        // Act
        val uiModel = task.toUiModel(now)

        // Assert
        assertEquals("Hôm nay", uiModel.group) // Diff < 24h vẫn xếp Hôm nay
        assertEquals("Quá hạn", uiModel.urgency)
    }

    @Test
    fun taskCompleted_hasNoUrgency_andMappedCorrectly() {
        // Arrange
        val now = getMockNow()
        val task = createTestTask(dueAtMillis = now + 10000L, isCompleted = true)

        // Act
        val uiModel = task.toUiModel(now)

        // Assert
        assertEquals(true, uiModel.completed)
        assertNull("Task đã xong không hiển thị mức độ khẩn", uiModel.urgency)
    }

    @Test
    fun taskWithSubtasks_mapsSubtasksCorrectly() {
        // Arrange
        val now = getMockNow()
        val subtasks = listOf(
            SubtaskEntity(1, 1, "Câu 1", true),
            SubtaskEntity(2, 1, "Câu 2", false)
        )
        val task = createTestTask(dueAtMillis = now + 10000L, subtasks = subtasks)

        // Act
        val uiModel = task.toUiModel(now)

        // Assert
        assertEquals(2, uiModel.subtasks.size)
        assertEquals(true, uiModel.subtasks[0].isCompleted)
        assertEquals(false, uiModel.subtasks[1].isCompleted)
    }
}
