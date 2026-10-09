package com.example.apphotrohoctap.ui.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
internal fun TaskTitleCard(title: String, onTitleChange: (String) -> Unit) {
    FormCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                FieldLabel("Tên công việc")
                Text("*", color = AddTaskUrgent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Text("Bắt buộc", fontSize = 10.sp, lineHeight = 14.sp, color = AddTaskSecondary)
        }
        FormTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = "Nhập tên công việc",
            trailingText = "⊗",
            onTrailingClick = { onTitleChange("") }
        )
    }
}

@Composable
internal fun CourseAndCategoryCard(
    selectedCourse: String,
    selectedCategory: String,
    showMoreCourses: Boolean,
    onCourseSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onShowMoreCourses: () -> Unit
) {
    FormCard(spacing = 8.dp) {
        FieldLabel("Môn học & Loại công việc")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (addTaskCourses + "Môn khác").forEach { course ->
                ChoicePill(
                    label = course,
                    selected = if (course == "Môn khác") showMoreCourses else course == selectedCourse,
                    onClick = if (course == "Môn khác") onShowMoreCourses else ({ onCourseSelected(course) }),
                    compact = true
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Lịch học", "Deadline", "Bài tập", "Ghi chú").forEach { category ->
                ChoicePill(category, category == selectedCategory, { onCategorySelected(category) }, compact = true)
            }
        }
    }
}

@Composable
internal fun DateCard(selectedDate: String, onOpenDatePicker: () -> Unit, onDateSelected: (String) -> Unit) {
    FormCard(spacing = 8.dp) {
        FieldLabel("Ngày thực hiện / Hạn chót")
        Surface(onClick = onOpenDatePicker, color = AddTaskField, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                AddTaskIcon("home_calendar.svg", Modifier.size(16.dp), "")
                Text(selectedDate, modifier = Modifier.weight(1f).padding(start = 10.dp), fontSize = 12.sp, color = AddTaskInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                AddTaskIcon("home_chevron.svg", Modifier.size(width = 10.dp, height = 6.dp), "Mở bộ chọn ngày")
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Hôm nay", "Ngày mai", "Cuối tuần").forEachIndexed { index, label ->
                ChoicePill(
                    label = label,
                    selected = (index == 0 && selectedDate == "Hôm nay") || (index == 1 && selectedDate == "Ngày mai"),
                    onClick = { onDateSelected(if (index == 0) "Hôm nay" else if (index == 1) "Ngày mai" else "Thứ Bảy, 27 Tháng 4, 2024") },
                    compact = true
                )
            }
        }
    }
}

@Composable
internal fun ReminderCard(
    selectedTime: String,
    reminderEnabled: Boolean,
    onOpenTimePicker: () -> Unit,
    onTimeSelected: (String) -> Unit,
    onReminderChange: (Boolean) -> Unit
) {
    FormCard(spacing = 8.dp) {
        FieldLabel("Giờ nộp / Giờ nhắc nhở")
        Surface(onClick = onOpenTimePicker, color = AddTaskField, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                AddTaskIcon("home_clock.svg", Modifier.size(17.dp), "")
                Text(selectedTime, modifier = Modifier.weight(1f).padding(start = 10.dp), fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.45.sp, fontWeight = FontWeight.SemiBold, color = AddTaskInk)
                Surface(color = AddTaskChip, shape = RoundedCornerShape(6.dp)) {
                    Text("Hết ngày", Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = AddTaskSecondary)
                }
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("23:59" to "23:59 (Hết ngày)", "12:00" to "12:00 (Trưa)", "17:00" to "17:00 (Cuối buổi chiều)").forEach { (time, label) ->
                ChoicePill(label, selectedTime == time, { onTimeSelected(time) }, compact = true)
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(color = AddTaskChip, shape = RoundedCornerShape(8.dp)) {
                Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { AddTaskIcon("home_alarm.svg", Modifier.size(16.dp), "") }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Nhắc nhở trước 1 tiếng", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = AddTaskInk)
                Text("Thông báo đẩy vào lúc 22:59", fontSize = 11.sp, lineHeight = 16.sp, color = AddTaskSecondary)
            }
            Switch(checked = reminderEnabled, onCheckedChange = onReminderChange)
        }
    }
}

@Composable
internal fun PriorityCard(selectedPriority: String, onPrioritySelected: (String) -> Unit) {
    FormCard(spacing = 8.dp) {
        FieldLabel("Mức độ ưu tiên")
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Thấp" to Color(0xFF10B981), "Vừa" to Color(0xFFF59E0B), "Cao" to AddTaskUrgent).forEach { (priority, color) ->
                val selected = selectedPriority == priority
                Surface(
                    onClick = { onPrioritySelected(priority) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    color = if (selected && priority == "Cao") AddTaskUrgentPale else if (selected) AddTaskChip else AddTaskField,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = if (selected) 1.dp else 0.dp
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
                        Spacer(Modifier.width(6.dp))
                        Text(priority, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = if (selected && priority == "Cao") Color(0xFF93000A) else AddTaskInk)
                    }
                }
            }
        }
    }
}

@Composable
internal fun NotesCard(
    notes: String,
    onNotesChange: (String) -> Unit,
    subtasks: List<String>,
    onAttach: () -> Unit,
    onAddSubtask: () -> Unit
) {
    FormCard(spacing = 8.dp) {
        FieldLabel("Ghi chú & Tài liệu")
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier.fillMaxWidth().height(112.dp),
            placeholder = { Text("Thêm ghi chú cho công việc") },
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp, color = AddTaskInk),
            colors = formFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )
        if (subtasks.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                subtasks.forEach { Text("• $it", fontSize = 11.sp, color = AddTaskSecondary) }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip("Đính kèm link/tệp", "home_document.svg", onAttach)
            ActionChip("Thêm việc con", "home_tasks.svg", onAddSubtask)
        }
    }
}
