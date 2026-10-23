package com.example.apphotrohoctap.ui.courses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphotrohoctap.ui.schedule.StudyClass
import com.example.apphotrohoctap.ui.tasks.TaskItem
import com.example.apphotrohoctap.data.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CourseBg = Color(0xFFFCF8FF)
private val CoursePale = Color(0xFFF5F2FF)
private val CourseChip = Color(0xFFEDE9FF)
private val CourseBrand = Color(0xFF4143D5)
private val CourseBright = Color(0xFF5B5FEF)
private val CourseInk = Color(0xFF1A1A2A)
private val CourseText = Color(0xFF555565)
private val CourseMuted = Color(0xFF747483)
private val CourseRed = Color(0xFFFF4747)
private val CourseAmber = Color(0xFFFFA500)
private val CourseGreen = Color(0xFF2FC496)

private data class CourseTask(
    val title: String,
    val date: String,
    val owner: String,
    val progress: Float,
    val progressText: String,
    val detail: String,
    val color: Color,
    val done: Boolean = false
)

@Composable
fun StudyFlowCourseDetailScreen(courseName: String, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val viewModel: CourseViewModel = viewModel(factory = CourseViewModelFactory(context))
    
    // Nạp tên môn học vào ViewModel lần đầu tiên (hoặc khi đổi môn)
    LaunchedEffect(courseName) {
        viewModel.setCourseName(courseName)
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("Bài tập") }
    BackHandler(onBack = onBack)

    Column(Modifier.fillMaxSize().background(CourseBg)) {
        CourseHeader(onBack = onBack, uiState = uiState)
        Box(Modifier.weight(1f)) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CourseOverview(uiState)
                CourseTabs(selectedTab) { selectedTab = it }
                if (selectedTab == "Lịch học") {
                    CourseScheduleTab(uiState.schedules)
                } else if (selectedTab == "Bài tập") {
                    CourseTasksTab(uiState.tasks) { taskId -> viewModel.toggleTaskCompletion(taskId) }
                } else if (selectedTab == "Ghi chú") {
                    CourseNotesTab(uiState.notes)
                }
            }
            Surface(
                onClick = { com.example.apphotrohoctap.ui.common.showComingSoon(context) }, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 20.dp).size(width = 154.dp, height = 56.dp),
                color = CourseBright, shape = RoundedCornerShape(18.dp), shadowElevation = 8.dp
            ) {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("+", fontSize = 26.sp, color = Color.White); Spacer(Modifier.width(8.dp))
                    Text("Thêm ${if (selectedTab == "Bài tập") "bài tập" else selectedTab.lowercase()}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CourseHeader(onBack: () -> Unit, uiState: CourseUiState) {
    Surface(color = CourseBg.copy(alpha = .98f), shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(onClick = onBack, modifier = Modifier.size(40.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 36.sp, lineHeight = 40.sp, color = CourseInk) }
            }
            Text(uiState.courseName, Modifier.padding(start = 12.dp), fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold, color = CourseInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CourseOverview(uiState: CourseUiState) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(color = CourseBright, shape = RoundedCornerShape(16.dp)) { Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) { Text("▦", fontSize = 31.sp, color = Color.White) } }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Badge(uiState.courseCode, CourseChip, CourseBrand)
                    }
                    Text(uiState.courseName, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = CourseInk)
                    Text("Giảng viên: ${uiState.lecturer}", fontSize = 13.sp, color = CourseMuted)
                }
            }
            Surface(color = CoursePale, shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("♙", fontSize = 22.sp, color = CourseBright)
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text("Giảng viên", fontSize = 11.sp, color = CourseMuted)
                        Text(uiState.lecturer, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CourseInk)
                    }
                    Surface(onClick = { com.example.apphotrohoctap.ui.common.showComingSoon(context) }, color = Color.White, shape = RoundedCornerShape(10.dp)) { Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) { Text("✉", fontSize = 20.sp, color = CourseBright) } }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoCard("♙", "Phòng học", uiState.room, "", Modifier.weight(1f))
                val firstSch = uiState.schedules.firstOrNull()
                InfoCard("▣", "Lịch học", if (firstSch != null) firstSch.time else "Chưa xếp", "", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InfoCard(icon: String, label: String, value: String, detail: String, modifier: Modifier) {
    Surface(modifier, color = CoursePale, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 17.sp, color = CourseBright); Spacer(Modifier.width(6.dp)); Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CourseMuted) }
            Text(value, fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold, color = CourseInk, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(detail, fontSize = 11.sp, lineHeight = 15.sp, color = CourseText, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun Badge(text: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = CircleShape) { Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = fg) }
}

@Composable
private fun CourseTabs(selected: String, onTabSelected: (String) -> Unit) {
    val tabs = listOf("Lịch học" to "home_calendar.svg", "Bài tập" to "home_done.svg", "Ghi chú" to "home_document.svg")
    Surface(Modifier.padding(horizontal = 24.dp), color = CourseChip, shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            tabs.forEach { (title, icon) ->
                val active = title == selected
                Surface(onClick = { onTabSelected(title) }, modifier = Modifier.weight(1f).height(44.dp), color = if (active) Color.White else Color.Transparent, shape = RoundedCornerShape(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        CourseIcon(icon, Modifier.size(17.dp), ""); Spacer(Modifier.width(5.dp))
                        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (active) CourseBrand else CourseText)
                        if (title == "Bài tập") { Spacer(Modifier.width(5.dp)); Badge("3", CourseBrand, Color.White) }
                        if (title == "Ghi chú") { Spacer(Modifier.width(5.dp)); Badge("4", CourseChip, CourseText) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseTaskCard(task: CourseTask, index: Int, checked: Boolean, onCheck: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
        Row(Modifier.fillMaxWidth().drawBehind { drawRect(task.color, size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)) }) {
            Surface(onClick = onCheck, modifier = Modifier.padding(start = 22.dp, top = 24.dp).size(22.dp), color = if (checked) task.color else Color.White, shape = RoundedCornerShape(3.dp), border = if (checked) null else BorderStroke(1.5.dp, Color(0xFF9D9DA8))) {
                Box(contentAlignment = Alignment.Center) { if (checked) Text("✓", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
            Column(Modifier.weight(1f).padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Badge(task.date, when (task.color) { CourseRed -> Color(0xFFFFEEEE); CourseAmber -> Color(0xFFFFF4E5); else -> Color(0xFFEAF8F4) }, task.color)
                    Spacer(Modifier.weight(1f)); Text(if (index == 0) "♧ ${task.owner}" else "♙ ${task.owner}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = CourseMuted)
                }
                Text(task.title, fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold, color = if (task.done) CourseMuted else CourseInk, textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (task.done) "Hoàn thành 100%" else "Tiến độ: ${(task.progress * 100).toInt()}%${if (index == 0) " (4/6 mục con)" else ""}", Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CourseText)
                    Text(task.progressText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = task.color)
                }
                Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(CourseChip)) { Box(Modifier.fillMaxWidth(task.progress).height(6.dp).clip(CircleShape).background(task.color)) }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("♧  ${task.detail}", Modifier.weight(1f), fontSize = 11.sp, lineHeight = 16.sp, color = CourseMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (index == 0) CourseAction("Nộp\nbài", task.color) else if (index == 1) CourseAction("Chi tiết", CourseChip, CourseInk)
                }
            }
        }
    }
}

@Composable
private fun CourseAction(label: String, color: Color, textColor: Color = Color.White) {
    val context = LocalContext.current
    Surface(onClick = { com.example.apphotrohoctap.ui.common.showComingSoon(context) }, color = color, shape = CircleShape) { Text(label, Modifier.padding(horizontal = 13.dp, vertical = 7.dp), fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor) }
}

@Composable
private fun NextClassCard() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Buổi học kế tiếp", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CourseInk); Spacer(Modifier.weight(1f)); Text("Phòng máy B1", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CourseBrand)
        }
        Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = CourseChip, shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("TH 4", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CourseBrand); Text("01", fontSize = 20.sp, color = CourseBrand); Text("TH 5", fontSize = 9.sp, color = CourseMuted) }
                }
                Column(Modifier.weight(1f).padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Badge("Ca thực hành", CourseChip, CourseBrand); Text("  07:30 - 09:50", fontSize = 12.sp, color = CourseText) }
                    Text("Kiểm tra giữa kỳ đồ án nhóm", fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, color = CourseInk)
                    Text("⌖ Phòng B1-302 • Mang theo laptop & file schema", fontSize = 11.sp, lineHeight = 15.sp, color = CourseText)
                }
            }
        }
    }
}

@Composable
private fun MaterialsCard() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text("Tài liệu & Ghi chú học tập", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CourseInk); Spacer(Modifier.weight(1f)); Text("Xem tất cả (4)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CourseBrand) }
        Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { CourseIcon("home_document.svg", Modifier.size(20.dp), ""); Text("  Cập nhật 2 ngày trước", Modifier.weight(1f), fontSize = 11.sp, color = CourseMuted); Badge("Lý thuyết", CourseChip, CourseText) }
                Text("Tổng hợp công thức phân mảnh CSDL ngang & dọc", fontSize = 14.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold, color = CourseInk)
                Text("Ghi chép các quy tắc completeness, reconstruction và disjointness kèm ví dụ bài tập tuần 6.", fontSize = 12.sp, lineHeight = 17.sp, color = CourseText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = CourseChip, shape = RoundedCornerShape(8.dp)) { Box(Modifier.size(48.dp, 42.dp)) }
                    Surface(color = CourseChip, shape = RoundedCornerShape(8.dp)) { Box(Modifier.size(48.dp, 42.dp)) }
                    Surface(color = CoursePale, shape = RoundedCornerShape(8.dp)) { Text("▧  Slide_Chuong4_Transactions...", Modifier.padding(horizontal = 10.dp, vertical = 12.dp), fontSize = 10.sp, color = CourseText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
            }
        }
    }
}

@Composable
private fun GroupTaskCard(checked: Set<String>, onChecked: (String) -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { CourseIcon("home_tasks.svg", Modifier.size(20.dp), ""); Text("  Đồ án lớn", Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CourseText); Badge("Tiến độ tốt", Color(0xFFE8F8F2), Color(0xFF209978)) }
            Text("Phân công nhiệm vụ đồ án nhóm bán kỳ", fontSize = 14.sp, lineHeight = 19.sp, fontWeight = FontWeight.Bold, color = CourseInk)
            listOf("Mình: Thiết kế ERD phân tán", "Tôi: Benchmark truy vấn và đo lường Indexing", "An: Soạn thảo slide báo cáo tiến độ").forEach { label ->
                val isChecked = label in checked
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(onClick = { onChecked(label) }, modifier = Modifier.size(18.dp), color = if (isChecked) CourseGreen else Color.White, shape = RoundedCornerShape(3.dp), border = if (isChecked) null else BorderStroke(1.5.dp, CourseBrand)) {
                        Box(contentAlignment = Alignment.Center) { if (isChecked) Text("✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                    Text(label, Modifier.padding(start = 8.dp), fontSize = 11.sp, lineHeight = 16.sp, color = CourseText, textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None)
                }
            }
        }
    }
}

@Composable
private fun CourseScheduleTab(schedules: List<StudyClass>) {
    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Lịch học", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CourseInk)
        if (schedules.isEmpty()) {
            Text("Chưa có lịch học nào.", fontSize = 13.sp, color = CourseMuted)
        } else {
            schedules.forEach { sch ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Badge(sch.session, CourseChip, CourseBrand)
                            Spacer(Modifier.weight(1f))
                            Text(sch.time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CourseBrand)
                        }
                        Text(sch.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CourseInk)
                        Text("Phòng: ${sch.place} • GV: ${sch.teacher}", fontSize = 12.sp, color = CourseMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseTasksTab(tasks: List<TaskItem>, onCheck: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Bài tập & Hạn chót", fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = CourseInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            Badge("${tasks.size} việc", CourseChip, CourseText)
        }
        if (tasks.isEmpty()) {
            Text("Chưa có bài tập nào.", fontSize = 13.sp, color = CourseMuted)
        } else {
            tasks.forEachIndexed { index, task ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
                    Row(Modifier.fillMaxWidth().drawBehind { drawRect(task.accent, size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)) }) {
                        Surface(onClick = { onCheck(task.id) }, modifier = Modifier.padding(start = 22.dp, top = 24.dp).size(22.dp), color = if (task.completed) task.accent else Color.White, shape = RoundedCornerShape(3.dp), border = if (task.completed) null else androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF9D9DA8))) {
                            Box(contentAlignment = Alignment.Center) { if (task.completed) Text("✓", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                        }
                        Column(Modifier.weight(1f).padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Badge(task.due, CoursePale, task.accent)
                                Spacer(Modifier.weight(1f))
                                Text(task.category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CourseBrand)
                            }
                            Text(task.title, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, color = CourseInk, textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseNotesTab(notes: List<NoteEntity>) {
    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Tài liệu & Ghi chú", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CourseInk)
        if (notes.isEmpty()) {
            Text("Chưa có tài liệu hoặc ghi chú nào.", fontSize = 13.sp, color = CourseMuted)
        } else {
            notes.forEach { note ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(note.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CourseInk)
                        Text(note.content, fontSize = 12.sp, color = CourseText, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        val sdf = SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault())
                        Text("Cập nhật: ${sdf.format(Date(note.updatedAtMillis))}", fontSize = 10.sp, color = CourseMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseTabPlaceholder(tab: String) {
    Surface(Modifier.padding(horizontal = 24.dp), color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("▣", fontSize = 28.sp, color = CourseBrand); Text("$tab môn học", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CourseInk); Text("Nội dung đang được cập nhật.", fontSize = 12.sp, color = CourseMuted)
        }
    }
}

@Composable
private fun CourseIcon(assetName: String, modifier: Modifier, description: String) {
    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/studyflow/$assetName").decoderFactory(SvgDecoder.Factory()).build(), contentDescription = description.takeIf(String::isNotBlank), modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun CourseDetailPreview() { MaterialTheme { StudyFlowCourseDetailScreen("Tên môn học") } }
