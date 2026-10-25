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

    @Test
    fun sanitizeTitle_trimsAndHandlesNullOrBlank() {
        assertNull(com.example.apphotrohoctap.ui.tasks.sanitizeTitle(null))
        assertNull(com.example.apphotrohoctap.ui.tasks.sanitizeTitle("   "))
        assertEquals("Học bài", com.example.apphotrohoctap.ui.tasks.sanitizeTitle("  Học bài  "))
    }

    @Test
    fun taskMapper_mapsPriorityAndDueAtMillisCorrectly() {
        val now = getMockNow()
        val dueAt = now + 50000L
        val task = createTestTask(dueAtMillis = dueAt, priority = "Cao")

        val uiModel = task.toUiModel(now)

        assertEquals("Cao", uiModel.priority)
        assertEquals(dueAt, uiModel.dueAtMillis)
    }

    @Test
    fun taskWithSubtasks_sortsSubtasksBySubtaskIdAscending() {
        val now = getMockNow()
        val subtasks = listOf(
            SubtaskEntity(subtaskId = 10, taskId = 1, title = "Câu 3", isCompleted = false),
            SubtaskEntity(subtaskId = 2, taskId = 1, title = "Câu 1", isCompleted = true),
            SubtaskEntity(subtaskId = 5, taskId = 1, title = "Câu 2", isCompleted = false)
        )
        val task = createTestTask(dueAtMillis = now + 10000L, subtasks = subtasks)

        val uiModel = task.toUiModel(now)

        assertEquals(3, uiModel.subtasks.size)
        assertEquals(2L, uiModel.subtasks[0].id)
        assertEquals(5L, uiModel.subtasks[1].id)
        assertEquals(10L, uiModel.subtasks[2].id)
    }
}
