package com.example.apphotrohoctap.ui.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DetailBackground = Color(0xFFFCF8FF)
private val DetailCard = Color.White
private val DetailPale = Color(0xFFF5F2FF)
private val DetailBrand = Color(0xFF4143D5)
private val DetailInk = Color(0xFF1A1A2A)
private val DetailMuted = Color(0xFF767586)
private val DetailText = Color(0xFF464555)
private val DetailDivider = Color(0xFFE3E0F7)
private val DetailRed = Color(0xFF93000A)
private val DetailRedPale = Color(0xFFFFDAD6)

@Composable
fun StudyFlowTaskDetailScreen(
    taskId: Long,
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModelFactory(LocalContext.current))
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val task = remember(tasks, taskId) { tasks.find { it.id == taskId.toString() } }

    var newSubtask by remember { mutableStateOf("") }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var pendingDateUtcMillis by rememberSaveable { mutableStateOf(0L) }

    var showEditNotesDialog by rememberSaveable { mutableStateOf(false) }
    var showEditTitleDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler(onBack = onBack)
    
    // Nếu task bị xóa hoặc không tìm thấy, quay về màn trước
    if (task == null) {
        return
    }
    val currentTask = task!!
    
    val checkedCount = currentTask.subtasks.count { it.isCompleted }
    val progress = if (currentTask.subtasks.isEmpty()) 0f else checkedCount.toFloat() / currentTask.subtasks.size

    Box(
        Modifier.fillMaxSize().background(DetailBackground)
            .then(if (showDeleteConfirmation) Modifier.blur(1.dp).alpha(.65f) else Modifier)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(onClick = onBack, modifier = Modifier.size(44.dp), color = Color.Transparent, shape = CircleShape) {
                    Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 30.sp, color = DetailInk) }
                }
                Text("Chi Tiết Bài Tập", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
            }

            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp).padding(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TaskDetailHeader(currentTask, onEditTitle = { showEditTitleDialog = true })
                TaskMetadataCard(currentTask, onEditTime = { showDatePicker = true })
                TaskProgressCard(checkedCount, currentTask.subtasks.size, progress)
                TaskSubtasksCard(
                    subtasks = currentTask.subtasks,
                    newSubtask = newSubtask,
                    onNewSubtaskChange = { newSubtask = it },
                    onToggle = { index -> 
                        val subtaskId = currentTask.subtasks[index].id
                        viewModel.toggleSubtask(currentTask.id.toLongOrNull() ?: 0L, subtaskId)
                    },
                    onDelete = { index ->
                        val subtaskId = currentTask.subtasks[index].id
                        viewModel.deleteSubtaskFromTask(subtaskId)
                    },
                    onAdd = {
                        if (newSubtask.isNotBlank()) {
                            viewModel.addSubtaskToExistingTask(currentTask.id.toLongOrNull() ?: 0L, newSubtask)
                            newSubtask = ""
                        }
                    }
                )
                TaskNotesCard(notes = currentTask.notes, onEditNotes = { showEditNotesDialog = true })
                Spacer(Modifier.height(56.dp))
            }
        }

        Surface(
            Modifier.align(Alignment.BottomCenter),
            color = DetailBackground.copy(alpha = .96f),
            shadowElevation = 6.dp
        ) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(onClick = { showDeleteConfirmation = true }, modifier = Modifier.weight(1f).height(48.dp), color = DetailRedPale, shape = RoundedCornerShape(12.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("▤  Xóa", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DetailRed) }
                }
                Surface(
                    onClick = { viewModel.toggleCompleted(currentTask.id) },
                    modifier = Modifier.weight(2f).height(48.dp),
                    color = DetailBrand,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (currentTask.completed) "✓  Đã hoàn thành" else "✓  Đánh dấu hoàn thành", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialUtc = getUtcDayMillisFromLocal(currentTask.dueAtMillis)
        com.example.apphotrohoctap.ui.common.StudyFlowDatePickerDialog(
            initialMillis = initialUtc,
            onDismiss = { showDatePicker = false },
            onConfirm = { dateUtcMillis ->
                pendingDateUtcMillis = dateUtcMillis
                showDatePicker = false
                showTimePicker = true
            }
        )
    }

    if (showTimePicker) {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = currentTask.dueAtMillis }
        val initialHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val initialMinute = cal.get(java.util.Calendar.MINUTE)

        com.example.apphotrohoctap.ui.common.StudyFlowTimePickerDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                showTimePicker = false
                val newDueAtMillis = com.example.apphotrohoctap.ui.common.DateTimeUtils.toDueAtMillis(
                    pendingDateUtcMillis,
                    hour,
                    minute
                )
                viewModel.updateTaskDetails(
                    id = currentTask.id,
                    title = currentTask.title,
                    notes = currentTask.notes,
                    priority = currentTask.priority,
                    dueAtMillis = newDueAtMillis
                )
            }
        )
    }

    if (showEditNotesDialog) {
        TaskEditNotesDialog(
            initialNotes = currentTask.notes,
            onDismiss = { showEditNotesDialog = false },
            onConfirm = { newNotes ->
                showEditNotesDialog = false
                viewModel.updateTaskDetails(
                    id = currentTask.id,
                    title = currentTask.title,
                    notes = newNotes,
                    priority = currentTask.priority,
                    dueAtMillis = currentTask.dueAtMillis
                )
            }
        )
    }

    if (showEditTitleDialog) {
        TaskEditTitleDialog(
            initialTitle = currentTask.title,
            initialPriority = currentTask.priority,
            onDismiss = { showEditTitleDialog = false },
            onConfirm = { newTitle, newPriority ->
                showEditTitleDialog = false
                viewModel.updateTaskDetails(
                    id = currentTask.id,
                    title = newTitle,
                    notes = currentTask.notes,
                    priority = newPriority,
                    dueAtMillis = currentTask.dueAtMillis
                )
            }
        )
    }

    if (showDeleteConfirmation) {
        TaskDeleteConfirmationDialog(
            taskTitle = currentTask.title,
            onDismiss = { showDeleteConfirmation = false },
            onConfirmDelete = {
                showDeleteConfirmation = false
                viewModel.deleteTask(currentTask.id)
                onDelete()
            }
        )
    }
}

@Composable
private fun TaskDetailHeader(task: TaskItem, onEditTitle: () -> Unit) {
    Surface(color = DetailCard, shape = RoundedCornerShape(18.dp), shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(4.dp).height(124.dp).background(task.accent))
            Column(Modifier.weight(1f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = Color(0xFFEFECFF), shape = CircleShape) {
                        Row(Modifier.padding(horizontal = 9.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(Modifier.size(7.dp).clip(CircleShape).background(task.accent))
                            Text(task.subject, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DetailBrand)
                        }
                    }
                    Surface(
                        onClick = onEditTitle,
                        color = DetailPale,
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✎", fontSize = 14.sp, color = DetailBrand)
                        }
                    }
                }
                Text(task.title, fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = DetailInk, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    task.urgency?.let { DetailBadge("⚑ $it", DetailRedPale, DetailRed) }
                    DetailBadge("⚡ ${task.priority}", DetailPale, DetailBrand)
                }
            }
        }
    }
}

@Composable
private fun DetailBadge(text: String, background: Color, foreground: Color) {
    Surface(color = background, shape = CircleShape) {
        Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = foreground, maxLines = 1)
    }
}

@Composable
private fun TaskMetadataCard(task: TaskItem, onEditTime: () -> Unit) {
    Surface(color = DetailPale, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            MetadataRow("▤", "Thời gian", task.due, trailing = "Sửa", onTrailingClick = onEditTime)
            MetadataDivider()
            MetadataRow("⬡", "Phân loại", task.category.uppercase(), trailing = "Học phần chính")
            MetadataDivider()
            MetadataRow("⚡", "Mức ưu tiên", task.priority, trailing = "")
        }
    }
}

@Composable
private fun MetadataRow(
    icon: String,
    label: String,
    value: String,
    trailing: String,
    onTrailingClick: (() -> Unit)? = null
) {
    Row(Modifier.fillMaxWidth().heightIn(min = 54.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = Color(0xFFE3E0F7), shape = CircleShape) {
            Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) { Text(icon, fontSize = 16.sp, color = DetailBrand) }
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DetailMuted)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
        }
        if (trailing.isNotBlank()) {
            Text(
                trailing,
                fontSize = if (trailing == "Sửa") 12.sp else 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (trailing == "Sửa") DetailBrand else DetailText,
                modifier = if (onTrailingClick != null) Modifier.clip(RoundedCornerShape(4.dp)).clickable { onTrailingClick() }.padding(horizontal = 4.dp, vertical = 2.dp) else Modifier
            )
        }
    }
}

@Composable
private fun MetadataDivider() {
    Spacer(Modifier.fillMaxWidth().padding(start = 48.dp).height(1.dp).background(DetailDivider.copy(alpha = .6f)))
}

@Composable
private fun TaskProgressCard(checked: Int, total: Int, progress: Float) {
    Surface(color = DetailCard, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("☷  Tiến độ", Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                Text("$checked/$total", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DetailBrand)
                Text(" việc con  •  ${(progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DetailMuted)
            }
            Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(DetailDivider)) {
                Box(Modifier.fillMaxWidth(progress).height(6.dp).clip(CircleShape).background(DetailBrand))
            }
        }
    }
}

@Composable
private fun TaskSubtasksCard(
    subtasks: List<Subtask>,
    newSubtask: String,
    onNewSubtaskChange: (String) -> Unit,
    onToggle: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onAdd: () -> Unit
) {
    Surface(color = DetailCard, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Việc con", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                Surface(Modifier.padding(start = 6.dp), color = Color(0xFFEFECFF), shape = CircleShape) {
                    Text("${subtasks.size}", Modifier.padding(horizontal = 7.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = DetailBrand)
                }
                Spacer(Modifier.weight(1f))
                Text("Chạm để cập nhật", fontSize = 9.sp, color = DetailMuted)
            }
            subtasks.forEachIndexed { index, item ->
                Row(
                    Modifier.fillMaxWidth().background(DetailPale, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onToggle(index) },
                        modifier = Modifier.size(38.dp),
                        colors = CheckboxDefaults.colors(checkedColor = DetailBrand, uncheckedColor = DetailDivider)
                    )
                    Text(
                        item.title,
                        Modifier.weight(1f).padding(start = 4.dp),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = if (item.isCompleted) DetailMuted else DetailInk,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(onClick = { onDelete(index) }, modifier = Modifier.size(32.dp), color = Color.Transparent, shape = CircleShape) {
                        Box(contentAlignment = Alignment.Center) { Text("▤", fontSize = 13.sp, color = DetailMuted) }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextField(
                    value = newSubtask,
                    onValueChange = onNewSubtaskChange,
                    modifier = Modifier.weight(1f).height(44.dp),
                    placeholder = { Text("＋  Thêm việc con mới...", fontSize = 11.sp, color = DetailMuted, maxLines = 1) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, color = DetailInk),
                    shape = CircleShape,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DetailPale, unfocusedContainerColor = DetailPale,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = DetailBrand
                    )
                )
                Surface(onClick = onAdd, color = DetailBrand, shape = CircleShape) {
                    Text("Thêm", Modifier.padding(horizontal = 13.dp, vertical = 9.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun TaskNotesCard(notes: String, onEditNotes: () -> Unit) {
    DetailSectionCard(
        title = "☷  Ghi chú",
        trailing = "✎  Chỉnh sửa",
        onTrailingClick = onEditNotes
    ) {
        val isNotBlank = notes.isNotBlank()
        Text(
            if (isNotBlank) notes else "Chưa có ghi chú. Nhấn Chỉnh sửa để thêm.",
            Modifier.fillMaxWidth().background(DetailPale, RoundedCornerShape(12.dp)).padding(12.dp),
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = if (isNotBlank) DetailText else DetailMuted
        )
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    trailing: String? = null,
    onTrailingClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(color = DetailCard, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                trailing?.let {
                    Text(
                        it,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DetailBrand,
                        modifier = if (onTrailingClick != null) Modifier.clip(RoundedCornerShape(4.dp)).clickable { onTrailingClick() }.padding(horizontal = 4.dp, vertical = 2.dp) else Modifier
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun TaskEditTitleDialog(
    initialTitle: String,
    initialPriority: String,
    onDismiss: () -> Unit,
    onConfirm: (newTitle: String, newPriority: String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf(initialTitle) }
    var priority by rememberSaveable { mutableStateOf(initialPriority) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().widthIn(max = 384.dp),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Chỉnh sửa bài tập", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DetailInk)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Tên bài tập *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { 
                            title = it
                            if (it.isNotBlank()) error = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập tên bài tập", fontSize = 14.sp) },
                        isError = error != null,
                        singleLine = true
                    )
                    if (error != null) {
                        Text(error!!, fontSize = 11.sp, color = DetailRed)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Mức ưu tiên", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Cao", "Trung bình", "Thấp").forEach { item ->
                            val selected = item == priority
                            Surface(
                                onClick = { priority = item },
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) DetailBrand else DetailPale,
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        item,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selected) Color.White else DetailText
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = DetailText)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                error = "Tên bài tập không được để trống"
                            } else {
                                onConfirm(title.trim(), priority)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DetailBrand),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskEditNotesDialog(
    initialNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (newNotes: String) -> Unit
) {
    var notes by rememberSaveable { mutableStateOf(initialNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().widthIn(max = 384.dp),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Chỉnh sửa Ghi chú", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DetailInk)

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp),
                    placeholder = { Text("Nhập ghi chú chi tiết cho bài tập...", fontSize = 14.sp) },
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = DetailText)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(notes) },
                        colors = ButtonDefaults.buttonColors(containerColor = DetailBrand),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun getUtcDayMillisFromLocal(dueAtMillis: Long): Long {
    val localCal = java.util.Calendar.getInstance().apply { timeInMillis = dueAtMillis }
    val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        set(java.util.Calendar.YEAR, localCal.get(java.util.Calendar.YEAR))
        set(java.util.Calendar.MONTH, localCal.get(java.util.Calendar.MONTH))
        set(java.util.Calendar.DAY_OF_MONTH, localCal.get(java.util.Calendar.DAY_OF_MONTH))
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return utcCal.timeInMillis
}

@Composable
private fun TaskDeleteConfirmationDialog(taskTitle: String, onDismiss: () -> Unit, onConfirmDelete: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().widthIn(max = 384.dp),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(color = DetailRedPale, shape = CircleShape) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Text("▤", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Xóa nhiệm vụ?", fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Hành động này không thể hoàn tác. Mọi việc con, ghi chú và tiến độ của “$taskTitle” sẽ bị xóa vĩnh viễn khỏi danh sách của bạn.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = DetailText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Hủy", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DetailText)
                    }
                    Button(
                        onClick = onConfirmDelete,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                    ) {
                        Text("▤  Xóa", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}
