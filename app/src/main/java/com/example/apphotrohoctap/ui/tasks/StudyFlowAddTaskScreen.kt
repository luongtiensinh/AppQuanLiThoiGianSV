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

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StudyFlowAddTaskScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModelFactory(context))
    val formState by viewModel.addTaskFormState.collectAsStateWithLifecycle()
    
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showSubtaskDialog by rememberSaveable { mutableStateOf(false) }
    var showPastDeadlineDialog by rememberSaveable { mutableStateOf(false) }

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
                TaskTitleCard(title = formState.title, onTitleChange = { viewModel.updateTitle(it) })
                CourseAndCategoryCard(
                    selectedCategory = formState.selectedCategory,
                    onCategorySelected = { viewModel.updateCategory(it) }
                )
                DateCard(
                    selectedDateUtcMillis = formState.selectedDateUtcMillis,
                    onOpenDatePicker = { showDatePicker = true },
                    onDateSelected = { viewModel.updateDate(it) },
                    onOpenOtherDate = { showDatePicker = true }
                )
                ReminderCard(
                    selectedHour = formState.selectedHour,
                    selectedMinute = formState.selectedMinute,
                    reminderEnabled = formState.reminderEnabled,
                    onOpenTimePicker = { showTimePicker = true },
                    onTimeSelected = { h, m -> viewModel.updateTime(h, m) },
                    onReminderChange = { viewModel.updateReminder(it) }
                )
                PriorityCard(selectedPriority = formState.selectedPriority, onPrioritySelected = { viewModel.updatePriority(it) })
                NotesCard(
                    notes = formState.notes,
                    onNotesChange = { viewModel.updateNotes(it) },
                    subtasks = formState.subtasks,
                    onAttach = {
                        com.example.apphotrohoctap.ui.common.showComingSoon(context)
                    },
                    onAddSubtask = { showSubtaskDialog = true }
                )
            }
            SaveFooter(enabled = formState.title.isNotBlank(), onSave = {
                val success = viewModel.saveTask(forceSave = false)
                if (!success) {
                    showPastDeadlineDialog = true
                } else {
                    onSave()
                }
            })
        }
    }

    if (showDatePicker) {
        StudyFlowDatePickerDialog(
            initialMillis = formState.selectedDateUtcMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis -> 
                viewModel.updateDate(millis)
                showDatePicker = false 
            }
        )
    }
    if (showTimePicker) {
        StudyFlowTimePickerDialog(
            initialHour = formState.selectedHour,
            initialMinute = formState.selectedMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m -> 
                viewModel.updateTime(h, m)
                showTimePicker = false
            }
        )
    }
    if (showSubtaskDialog) {
        AlertDialog(
            onDismissRequest = { showSubtaskDialog = false },
            title = { Text("Thêm việc con", fontWeight = FontWeight.SemiBold) },
            text = {
                OutlinedTextField(
                    value = formState.subtaskText,
                    onValueChange = { viewModel.updateSubtaskText(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập tên việc con") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addSubtask()
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
                    viewModel.saveTask(forceSave = true)
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
