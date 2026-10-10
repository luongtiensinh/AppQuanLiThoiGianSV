package com.example.apphotrohoctap.ui.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.R

@Composable
internal fun TasksHeader(onNotificationClick: () -> Unit, onProfileClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().statusBarsPadding(), color = TasksBackground.copy(alpha = 0.96f), shadowElevation = 1.dp) {
        Row(
            Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.studyflow_logo), "StudyFlow", Modifier.size(32.dp))
                Column {
                    Text("StudyFlow", fontSize = 18.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold, color = TasksInk)
                    Text("Nhiệm Vụ Studyflow", fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold, color = TasksBrand)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Surface(onClick = onNotificationClick, color = Color.Transparent, shape = CircleShape, modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) { TasksIcon("home_notifications.svg", Modifier.width(16.dp).height(20.dp), "Thông báo") }
                    }
                    Box(Modifier.align(Alignment.TopEnd).padding(top = 9.dp, end = 9.dp).size(8.dp).clip(CircleShape).background(TasksRed))
                }
                Surface(onClick = onProfileClick, modifier = Modifier.size(44.dp), color = Color.Transparent, shape = CircleShape) {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/studyflow/home_profile.png").build(),
                            contentDescription = "Hồ sơ cá nhân",
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SearchAndFilters(
    searchText: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    tasks: List<TaskItem>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(TasksPale),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌕", modifier = Modifier.padding(start = 14.dp), fontSize = 24.sp, color = TasksSecondary)
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tìm kiếm bài tập, môn học...", fontSize = 12.sp, color = TasksSecondary) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = TasksSecondary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = TasksBrand
                )
            )
            Text("☷", modifier = Modifier.padding(horizontal = 14.dp), fontSize = 18.sp, color = TasksSecondary)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            taskFilters.forEach { filter ->
                val selected = filter.kind == selectedFilter
                Surface(
                    onClick = { onFilterSelected(filter.kind) },
                    color = if (selected) TasksBright else TasksChip,
                    shape = CircleShape,
                    shadowElevation = if (selected) 1.dp else 0.dp
                ) {
                    Row(
                        Modifier.height(32.dp).padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (filter.kind == "deadline") Box(Modifier.size(7.dp).clip(CircleShape).background(TasksRed))
                        Text(
                            "${filter.label} (${filterCount(filter.kind, tasks)})",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else TasksSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

internal fun filterCount(kind: String, tasks: List<TaskItem>): Int {
    return tasks.count { task ->
        val isDone = task.completed
        when (kind) {
            "all" -> true
            "class" -> task.category == "class"
            "deadline" -> task.category == "deadline"
            "assignment" -> task.category == "assignment"
            "done" -> isDone
            else -> false
        }
    }
}

@Composable
internal fun TodayProgressCard(tasks: List<TaskItem>) {
    val todayTasks = tasks.filter { it.group == "Hôm nay" }
    val total = todayTasks.size
    val done = todayTasks.count { it.completed }
    val progress = if (total > 0) done.toFloat() / total else 0f
    val percentage = (progress * 100).toInt()

    val urgentTask = todayTasks.find { it.urgency != null && !it.completed }

    Surface(color = TasksPale, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = TasksBrand.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { TasksIcon("home_done.svg", Modifier.size(20.dp), "") }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tiến độ hôm nay", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = TasksInk)
                    Text("$done/$total xong ($percentage%)", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, color = TasksBrand)
                }
                Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(TasksDivider)) {
                    Box(Modifier.fillMaxWidth(progress.coerceAtLeast(0.01f)).height(8.dp).clip(CircleShape).background(TasksBrand))
                }
                if (urgentTask != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(TasksRed))
                        Text("1 deadline gấp cần nộp ${urgentTask.due.replace(" hôm nay", "").lowercase()}", fontSize = 11.sp, lineHeight = 16.sp, color = TasksSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else if (done == total && total > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF34C38F)))
                        Text("Bạn đã hoàn thành tất cả nhiệm vụ!", fontSize = 11.sp, lineHeight = 16.sp, color = TasksSecondary, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
internal fun TaskGroup(title: String, date: String, tasks: List<TaskItem>, onToggleComplete: (String) -> Unit, onTaskClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold, color = TasksInk)
            Spacer(Modifier.width(8.dp))
            Surface(color = if (title == "Hôm nay") TasksRedPale else TasksChip, shape = CircleShape) {
                Text(
                    if (title == "Hôm nay") "${tasks.size} việc • 1 khẩn cấp" else "${tasks.size} việc",
                    Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (title == "Hôm nay") Color(0xFF93000A) else TasksSecondary
                )
            }
            Spacer(Modifier.weight(1f))
            Text(date, fontSize = 9.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = TasksSecondary)
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEach { task ->
                val isDone = task.completed
                TaskCard(task, isDone, onClick = onTaskClick) { onToggleComplete(task.id) }
            }
        }
    }
}

@Composable
internal fun TaskCard(task: TaskItem, isDone: Boolean, onClick: () -> Unit, onToggleComplete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.fillMaxWidth().drawBehind { drawRect(task.accent, size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)) }) {
            Surface(
                onClick = onToggleComplete,
                modifier = Modifier.padding(start = 12.dp, top = 14.dp).size(24.dp),
                color = if (isDone) Color(0xFF34C38F) else Color.White,
                shape = CircleShape,
                border = if (isDone) null else BorderStroke(1.5.dp, Color(0xFFC6C5D7))
            ) {
                Box(contentAlignment = Alignment.Center) { if (isDone) Text("✓", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp, top = 12.dp, bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (task.urgency != null && !isDone) {
                        val urgent = task.id == "report"
                        Surface(color = if (urgent) TasksRedPale else Color(0xFFFFF1D6), shape = CircleShape) {
                            Text(task.urgency, Modifier.padding(horizontal = 7.dp, vertical = 2.dp), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = if (urgent) Color(0xFF93000A) else Color(0xFF8A4B00), maxLines = 1)
                        }
                    }
                    Surface(color = Color(0xFFE1E0FF), shape = CircleShape) {
                        Text(task.subject, Modifier.padding(horizontal = 7.dp, vertical = 2.dp), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF05006C), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Text(
                    task.title,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDone) TasksMuted else TasksInk,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TasksIcon(if (isDone) "home_done.svg" else "home_clock.svg", Modifier.size(11.dp), "")
                    Text(task.due, fontSize = 10.sp, lineHeight = 14.sp, color = if (isDone) Color(0xFF198754) else TasksSecondary, maxLines = 1)
                    task.place?.let {
                        Spacer(Modifier.width(4.dp))
                        TasksIcon("home_location.svg", Modifier.size(10.dp), "")
                        Text(it, fontSize = 10.sp, lineHeight = 14.sp, color = TasksSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Surface(onClick = {}, modifier = Modifier.padding(top = 10.dp, end = 6.dp).size(32.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) { Text("⋮", fontSize = 18.sp, color = TasksSecondary) }
            }
        }
    }
}

@Composable
internal fun FriendlyBottomCard(onAddTask: () -> Unit) {
    Surface(color = TasksPale, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(color = TasksDivider, shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) { TasksIcon("home_done.svg", Modifier.size(24.dp), "") }
            }
            Text("Tất cả đều trong tầm kiểm soát!", fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold, color = TasksInk)
            Text(
                "Tuyệt vời! Bạn không còn deadline tồn đọng nào cho môn này. Chưa có việc nào, hãy thêm mới!",
                modifier = Modifier.width(300.dp),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TasksSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Surface(onClick = onAddTask, color = Color(0xFFE1E0FF), shape = CircleShape) {
                Text("＋ Thêm nhiệm vụ mới", Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF05006C))
            }
        }
    }
}

internal data class TasksTab(val title: String, val icon: String)

internal val tasksTabs = listOf(
    TasksTab("Trang chủ", "home_nav_home.svg"),
    TasksTab("Lịch biểu", "home_nav_schedule.svg"),
    TasksTab("Nhiệm vụ", "home_nav_tasks.svg"),
    TasksTab("Ghi chú", "home_nav_notes.svg"),
    TasksTab("Cài đặt", "home_nav_settings.svg")
)

@Composable
internal fun TasksBottomNavigation(onTabSelected: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), color = TasksBackground.copy(alpha = 0.96f), shadowElevation = 4.dp) {
        Row(Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            tasksTabs.forEachIndexed { index, tab ->
                Column(Modifier.weight(1f).height(56.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Surface(onClick = { onTabSelected(tab.title) }, modifier = Modifier.width(56.dp).height(32.dp), color = if (index == 2) Color(0xFFE1E0FF) else Color.Transparent, shape = CircleShape) {
                        Box(contentAlignment = Alignment.Center) { TasksIcon(tab.icon, Modifier.size(18.dp), "") }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(tab.title, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = if (index == 2) FontWeight.Bold else FontWeight.SemiBold, color = if (index == 2) TasksBrand else TasksSecondary, maxLines = 1)
                }
            }
        }
    }
}

@Composable
internal fun TasksIcon(assetName: String, modifier: Modifier, contentDescription: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/studyflow/$assetName").decoderFactory(SvgDecoder.Factory()).build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
