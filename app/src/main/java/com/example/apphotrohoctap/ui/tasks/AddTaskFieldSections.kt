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
internal fun DateCard(
    selectedDateUtcMillis: Long,
    onOpenDatePicker: () -> Unit,
    onDateSelected: (Long) -> Unit,
    onOpenOtherDate: () -> Unit
) {
    val todayMillis = com.example.apphotrohoctap.ui.common.DateTimeUtils.getLocalDayUtcMillis(0)
    val tomorrowMillis = com.example.apphotrohoctap.ui.common.DateTimeUtils.getLocalDayUtcMillis(1)
    
    // Convert to local midnight timestamp for safe formatting
    val localMidnightMillis = com.example.apphotrohoctap.ui.common.DateTimeUtils.toDueAtMillis(selectedDateUtcMillis, 0, 0)
    val formattedDate = com.example.apphotrohoctap.ui.common.DateTimeUtils.formatToDateString(localMidnightMillis)
    
    val displayStr = when (selectedDateUtcMillis) {
        todayMillis -> "Hôm nay ($formattedDate)"
        tomorrowMillis -> "Ngày mai ($formattedDate)"
        else -> formattedDate
    }

    FormCard(spacing = 8.dp) {
        FieldLabel("Ngày thực hiện / Hạn chót")
        Surface(onClick = onOpenDatePicker, color = AddTaskField, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                AddTaskIcon("home_calendar.svg", Modifier.size(16.dp), "")
                Text(displayStr, modifier = Modifier.weight(1f).padding(start = 10.dp), fontSize = 12.sp, color = AddTaskInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                AddTaskIcon("home_chevron.svg", Modifier.size(width = 10.dp, height = 6.dp), "Mở bộ chọn ngày")
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChoicePill("Hôm nay", selectedDateUtcMillis == todayMillis, { onDateSelected(todayMillis) }, compact = true)
            ChoicePill("Ngày mai", selectedDateUtcMillis == tomorrowMillis, { onDateSelected(tomorrowMillis) }, compact = true)
            val isOther = selectedDateUtcMillis != todayMillis && selectedDateUtcMillis != tomorrowMillis
            ChoicePill("Chọn ngày khác...", isOther, onOpenOtherDate, compact = true)
        }
    }
}

@Composable
internal fun ReminderCard(
    selectedHour: Int,
    selectedMinute: Int,
    reminderEnabled: Boolean,
    onOpenTimePicker: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onReminderChange: (Boolean) -> Unit
) {
    val selectedTimeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
    
    FormCard(spacing = 8.dp) {
        FieldLabel("Giờ nộp / Giờ nhắc nhở")
        Surface(onClick = onOpenTimePicker, color = AddTaskField, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                AddTaskIcon("home_clock.svg", Modifier.size(17.dp), "")
                Text(selectedTimeStr, modifier = Modifier.weight(1f).padding(start = 10.dp), fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.45.sp, fontWeight = FontWeight.SemiBold, color = AddTaskInk)
                Surface(color = AddTaskChip, shape = RoundedCornerShape(6.dp)) {
                    Text("Tùy chỉnh", Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = AddTaskSecondary)
                }
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val quickTimes = listOf(
                Triple(23, 59, "23:59 (Hết ngày)"), 
                Triple(12, 0, "12:00 (Trưa)"), 
                Triple(17, 0, "17:00 (Cuối buổi chiều)")
            )
            quickTimes.forEach { (h, m, label) ->
                ChoicePill(label, selectedHour == h && selectedMinute == m, { onTimeSelected(h, m) }, compact = true)
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
                
                // Calculate reminder time (1 hour before)
                var reminderHour = selectedHour - 1
                if (reminderHour < 0) reminderHour = 23
                val reminderTimeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", reminderHour, selectedMinute)
                Text("Thông báo đẩy vào lúc $reminderTimeStr", fontSize = 11.sp, lineHeight = 16.sp, color = AddTaskSecondary)
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
