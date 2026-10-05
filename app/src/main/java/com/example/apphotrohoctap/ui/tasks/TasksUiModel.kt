package com.example.apphotrohoctap.ui.tasks

import androidx.compose.ui.graphics.Color

internal val TasksBackground = Color(0xFFFCF8FF)
internal val TasksPale = Color(0xFFF5F2FF)
internal val TasksChip = Color(0xFFEFECFF)
internal val TasksBrand = Color(0xFF4143D5)
internal val TasksBright = Color(0xFF5B5FEF)
internal val TasksInk = Color(0xFF1A1A2A)
internal val TasksSecondary = Color(0xFF464555)
internal val TasksMuted = Color(0xFF767586)
internal val TasksDivider = Color(0xFFE3E0F7)
internal val TasksRed = Color(0xFFBA1A1A)
internal val TasksRedPale = Color(0xFFFFDAD6)

internal data class TaskFilter(val label: String, val kind: String)

internal data class TaskItem(
    val id: String,
    val group: String,
    val subject: String,
    val title: String,
    val due: String,
    val category: String,
    val accent: Color,
    val urgency: String? = null,
    val place: String? = null,
    val completed: Boolean = false
)

internal val taskFilters = listOf(
    TaskFilter("Tất cả", "all"),
    TaskFilter("Lịch học", "class"),
    TaskFilter("Deadline", "deadline"),
    TaskFilter("Bài tập", "assignment"),
    TaskFilter("Đã xong", "done")
)

internal val sampleTasks = listOf(
    TaskItem("report", "Hôm nay", "CSDL Nâng cao (IT3020)", "Báo cáo Đồ án Bán kỳ: Thiết kế schema & Indexing", "23:59 hôm nay", "deadline", Color(0xFFEF4444), "Gấp • 6 giờ nữa", "Figma Project"),
    TaskItem("sprint", "Hôm nay", "Lập trình Ứng dụng Di động", "Nộp bài tập Sprint 2 - Android Architecture...", "15:00 hôm nay", "assignment", Color(0xFFF59E0B), "Sắp đến • 15:00", "Phòng Lab 405"),
    TaskItem("chapter", "Hôm nay", "CSDL Nâng cao", "Đọc trước Chapter 4: Distributed Database...", "Đã nộp lúc 09:30", "done", Color(0xFF34C38F), completed = true),
    TaskItem("slides", "Ngày mai", "Kỹ năng thuyết trình (IT4210)", "Chuẩn bị bài thuyết trình Slide Presentation: Cloud...", "Ngày mai, 17:00", "assignment", Color(0xFFF59E0B), "Điện toán đám mây (IT4210)", "Nhóm 4 thành viên"),
    TaskItem("quiz", "Ngày mai", "Tiếng Anh CNTT", "Làm bài tập trắc nghiệm Vocabulary Unit 5 & 6", "Ngày mai, 21:00", "assignment", Color(0xFF38BDF8), "Tiếng Anh CNTT", "20 câu hỏi LMS"),
    TaskItem("prototype", "Tuần này", "Thiết kế UI/UX", "Hoàn thiện Prototype Figma User Flow màn hình thanh...", "Thứ Bảy, 27/04 - 12:00", "assignment", Color(0xFFF59E0B), "Thiết kế UI/UX", "Figma Project")
)
