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
import com.example.apphotrohoctap.data.TaskEntity
import com.example.apphotrohoctap.data.toUiModel
import java.util.Calendar

@Composable
fun StudyFlowAddTaskScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var title by remember { mutableStateOf("Báo cáo tiến độ đồ án phần mềm nhóm 4") }
    var selectedCourse by remember { mutableStateOf(addTaskCourses.first()) }
    var selectedCategory by remember { mutableStateOf("Deadline") }
    var selectedDate by remember { mutableStateOf("Thứ Tư, 24 Tháng 4, 2024") }
    var selectedTime by remember { mutableStateOf("23:59") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var selectedPriority by remember { mutableStateOf("Cao") }
    var notes by remember {
        mutableStateOf("Yêu cầu nộp kèm file ZIP source code và file Word báo cáo thiết kế CSDL mức vật lý. Nhóm trưởng đại diện nộp trên LMS.")
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMoreCourses by remember { mutableStateOf(false) }
    var showSubtaskDialog by remember { mutableStateOf(false) }
    var subtaskText by remember { mutableStateOf("") }
    var subtasks by remember { mutableStateOf(emptyList<String>()) }

    BackHandler(onBack = onBack)

    Box(Modifier.fillMaxSize().background(AddTaskBackground)) {
        Column(Modifier.fillMaxSize()) {
            AddTaskHeader(onBack)
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GuidanceBanner()
                TaskTitleCard(title = title, onTitleChange = { title = it })
                CourseAndCategoryCard(
                    selectedCourse = selectedCourse,
                    selectedCategory = selectedCategory,
                    showMoreCourses = showMoreCourses,
                    onCourseSelected = { selectedCourse = it },
                    onCategorySelected = { selectedCategory = it },
                    onShowMoreCourses = { showMoreCourses = true }
                )
                DateCard(
                    selectedDate = selectedDate,
                    onOpenDatePicker = { showDatePicker = true },
                    onDateSelected = { selectedDate = it }
                )
                ReminderCard(
                    selectedTime = selectedTime,
                    reminderEnabled = reminderEnabled,
                    onOpenTimePicker = { showTimePicker = true },
                    onTimeSelected = { selectedTime = it },
                    onReminderChange = { reminderEnabled = it }
                )
                PriorityCard(selectedPriority = selectedPriority, onPrioritySelected = { selectedPriority = it })
                NotesCard(
                    notes = notes,
                    onNotesChange = { notes = it },
                    subtasks = subtasks,
                    onAttach = {},
                    onAddSubtask = { showSubtaskDialog = true }
                )
            }
            SaveFooter(enabled = title.isNotBlank(), onSave = {
                val categoryKey = when (selectedCategory) {
                    "Deadline" -> "deadline"
                    "Lịch học" -> "class"
                    else -> "assignment"
                }

                // Cách chuyển đổi String -> Milliseconds
                val calendar = Calendar.getInstance()
                when (selectedDate) {
                    "Ngày mai" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                    "Thứ Bảy, 27 Tháng 4, 2024" -> { calendar.set(2024, 3, 27) } // Index tháng từ 0
                }
                val timeParts = selectedTime.split(":")
                if (timeParts.size == 2) {
                    calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toIntOrNull() ?: 23)
                    calendar.set(Calendar.MINUTE, timeParts[1].toIntOrNull() ?: 59)
                }
                val dueAtMillis = calendar.timeInMillis

                val newEntity = TaskEntity(
                    subject = selectedCourse,
                    title = title,
                    dueAtMillis = dueAtMillis,
                    category = categoryKey,
                    priority = selectedPriority,
                    place = null,
                    notes = notes,
                    isCompleted = false
                )

                // Tạm thời gọi hàm Mapper để add vào UI giả lập (sẽ thay bằng ViewModel.insertTask sau)
                val newItem = newEntity.toUiModel(System.currentTimeMillis())
                sampleTasks.add(newItem)

                onSave()
            })
        }
    }

    if (showDatePicker) {
        ChoiceDialog(
            title = "Chọn ngày thực hiện / hạn chót",
            choices = listOf("Hôm nay", "Ngày mai", "Thứ Bảy, 27 Tháng 4, 2024"),
            onDismiss = { showDatePicker = false },
            onChoice = { selectedDate = it; showDatePicker = false }
        )
    }
    if (showTimePicker) {
        ChoiceDialog(
            title = "Chọn giờ nộp / nhắc nhở",
            choices = listOf("23:59", "12:00", "17:00"),
            onDismiss = { showTimePicker = false },
            onChoice = { selectedTime = it; showTimePicker = false }
        )
    }
    if (showMoreCourses) {
        ChoiceDialog(
            title = "Chọn môn học",
            choices = addTaskCourses + "Môn khác",
            onDismiss = { showMoreCourses = false },
            onChoice = { selectedCourse = it; showMoreCourses = false }
        )
    }
    if (showSubtaskDialog) {
        AlertDialog(
            onDismissRequest = { showSubtaskDialog = false },
            title = { Text("Thêm việc con", fontWeight = FontWeight.SemiBold) },
            text = {
                OutlinedTextField(
                    value = subtaskText,
                    onValueChange = { subtaskText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập tên việc con") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (subtaskText.isNotBlank()) subtasks = subtasks + subtaskText.trim()
                    subtaskText = ""
                    showSubtaskDialog = false
                }) { Text("Thêm") }
            },
            dismissButton = { TextButton(onClick = { showSubtaskDialog = false }) { Text("Hủy") } }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowAddTaskScreenPreview() {
    MaterialTheme { StudyFlowAddTaskScreen() }
}
