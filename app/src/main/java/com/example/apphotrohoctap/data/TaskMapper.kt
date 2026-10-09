package com.example.apphotrohoctap.data

import androidx.compose.ui.graphics.Color
import com.example.apphotrohoctap.ui.tasks.TaskItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun TaskEntity.toUiModel(now: Long): TaskItem {
    // 1. Phân loại Group: Hôm nay, Ngày mai, Tuần này dựa trên thời gian
    val diffMillis = dueAtMillis - now
    val diffHours = diffMillis / (1000 * 60 * 60)

    val group = when {
        diffHours < 24 -> "Hôm nay"
        diffHours in 24..48 -> "Ngày mai"
        else -> "Tuần này"
    }

    // 2. Suy luận Urgency (độ khẩn cấp)
    val urgency = if (!isCompleted && priority == "Cao") {
        if (diffHours in 0..12) "Gấp • ${diffHours} giờ nữa"
        else if (diffHours < 0) "Quá hạn"
        else "Sắp đến"
    } else null

    // 3. Quy định Accent Color (Màu thẻ)
    val accent = when (category) {
        "deadline" -> Color(0xFFEF4444) // Đỏ
        "class" -> Color(0xFF38BDF8)    // Xanh dương
        else -> Color(0xFFF59E0B)       // Vàng cam (assignment)
    }

    // 4. Format thời gian hiển thị (Due String)
    val formatter = SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault())
    val dueStr = formatter.format(Date(dueAtMillis))

    return TaskItem(
        id = this.id.toString(),
        group = group,
        subject = this.subject,
        title = this.title,
        due = dueStr,
        category = this.category,
        accent = accent,
        urgency = urgency,
        place = this.place,
        completed = this.isCompleted
    )
}
