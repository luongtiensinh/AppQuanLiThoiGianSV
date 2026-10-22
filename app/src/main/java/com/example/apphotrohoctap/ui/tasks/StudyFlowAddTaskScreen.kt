package com.example.apphotrohoctap.ui.tasks

import android.widget.Toast
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.data.TaskEntity
import com.example.apphotrohoctap.ui.common.DateTimeUtils
import com.example.apphotrohoctap.ui.common.StudyFlowDatePickerDialog
import com.example.apphotrohoctap.ui.common.StudyFlowTimePickerDialog
import java.util.Calendar

@Composable
fun StudyFlowAddTaskScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModelFactory(context))
    
    var title by rememberSaveable { mutableStateOf("") }
    var selectedCourse by rememberSaveable { mutableStateOf(addTaskCourses.firstOrNull() ?: "") }
    var selectedCategory by rememberSaveable { mutableStateOf("Deadline") }
    
    var selectedDateUtcMillis by rememberSaveable { mutableStateOf(DateTimeUtils.getLocalDayUtcMillis(0)) }
    var selectedHour by rememberSaveable { mutableStateOf(23) }
    var selectedMinute by rememberSaveable { mutableStateOf(59) }
    
    var reminderEnabled by rememberSaveable { mutableStateOf(true) }
    var selectedPriority by rememberSaveable { mutableStateOf("Cao") }
    var notes by rememberSaveable { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMoreCourses by remember { mutableStateOf(false) }
    var showSubtaskDialog by remember { mutableStateOf(false) }
    var showPastDeadlineDialog by remember { mutableStateOf(false) }
    
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
                    selectedDateUtcMillis = selectedDateUtcMillis,
                    onOpenDatePicker = { showDatePicker = true },
                    onDateSelected = { selectedDateUtcMillis = it },
                    onOpenOtherDate = { showDatePicker = true }
                )
                ReminderCard(
                    selectedHour = selectedHour,
                    selectedMinute = selectedMinute,
                    reminderEnabled = reminderEnabled,
                    onOpenTimePicker = { showTimePicker = true },
                    onTimeSelected = { h, m -> selectedHour = h; selectedMinute = m },
                    onReminderChange = { reminderEnabled = it }
                )
                PriorityCard(selectedPriority = selectedPriority, onPrioritySelected = { selectedPriority = it })
                NotesCard(
                    notes = notes,
                    onNotesChange = { notes = it },
                    subtasks = subtasks,
                    onAttach = {
                        Toast.makeText(context, "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show()
                    },
                    onAddSubtask = { showSubtaskDialog = true }
                )
            }
            SaveFooter(enabled = title.isNotBlank(), onSave = {
                val dueAtMillis = DateTimeUtils.toDueAtMillis(selectedDateUtcMillis, selectedHour, selectedMinute)
                
                if (dueAtMillis < System.currentTimeMillis()) {
                    showPastDeadlineDialog = true
                } else {
                    val categoryKey = when (selectedCategory) {
                        "Deadline" -> "deadline"
                        "Lịch học" -> "class"
                        else -> "assignment"
                    }
                    
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

                    viewModel.addTaskWithSubtasks(newEntity, subtasks)
                    onSave()
                }
            })
        }
    }

    if (showDatePicker) {
        StudyFlowDatePickerDialog(
            initialMillis = selectedDateUtcMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis -> 
                selectedDateUtcMillis = millis
                showDatePicker = false 
            }
        )
    }
    if (showTimePicker) {
        StudyFlowTimePickerDialog(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m -> 
                selectedHour = h
                selectedMinute = m
                showTimePicker = false
            }
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
    if (showPastDeadlineDialog) {
        AlertDialog(
            onDismissRequest = { showPastDeadlineDialog = false },
            title = { Text("Cảnh báo", fontWeight = FontWeight.SemiBold) },
            text = { Text("Hạn chót đã qua, bạn vẫn muốn lưu?") },
            confirmButton = {
                TextButton(onClick = {
                    showPastDeadlineDialog = false
                    val dueAtMillis = DateTimeUtils.toDueAtMillis(selectedDateUtcMillis, selectedHour, selectedMinute)
                    val categoryKey = when (selectedCategory) {
                        "Deadline" -> "deadline"
                        "Lịch học" -> "class"
                        else -> "assignment"
                    }
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
                    viewModel.addTaskWithSubtasks(newEntity, subtasks)
                    onSave()
                }) { Text("Lưu") }
            },
            dismissButton = { TextButton(onClick = { showPastDeadlineDialog = false }) { Text("Hủy") } }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowAddTaskScreenPreview() {
    MaterialTheme { StudyFlowAddTaskScreen() }
}
