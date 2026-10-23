package com.example.apphotrohoctap.data

import androidx.compose.ui.graphics.Color
import com.example.apphotrohoctap.ui.tasks.Subtask
import com.example.apphotrohoctap.ui.tasks.TaskItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun TaskWithSubtasks.toUiModel(now: Long): TaskItem {
    // 1. Phân loại Group: Hôm nay, Ngày mai, Tuần này dựa trên thời gian
    val diffMillis = task.dueAtMillis - now
    val diffHours = diffMillis / (1000 * 60 * 60)

    val group = when {
        diffHours < 24 -> "Hôm nay"
        diffHours in 24..48 -> "Ngày mai"
        else -> "Tuần này"
    }

    // 2. Suy luận Urgency (độ khẩn cấp)
    val urgency = if (!task.isCompleted && task.priority == "Cao") {
        if (diffHours in 0..12) "Gấp • ${diffHours} giờ nữa"
        else if (diffHours < 0) "Quá hạn"
        else "Sắp đến"
    } else null

    // 3. Quy định Accent Color (Màu thẻ)
    val accent = when (task.category) {
        "deadline" -> Color(0xFFEF4444) // Đỏ
        "class" -> Color(0xFF38BDF8)    // Xanh dương
        else -> Color(0xFFF59E0B)       // Vàng cam (assignment)
    }

    // 4. Format thời gian hiển thị (Due String)
    val formatter = SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault())
    val dueStr = formatter.format(Date(task.dueAtMillis))

    val uiSubtasks = subtasks.map { Subtask(it.subtaskId, it.title, it.isCompleted) }

    return TaskItem(
        id = task.id.toString(),
        group = group,
        subject = task.subject,
        title = task.title,
        due = dueStr,
        category = task.category,
        accent = accent,
        urgency = urgency,
        place = task.place,
        completed = task.isCompleted,
        notes = task.notes ?: "",
        subtasks = uiSubtasks
    )
}
