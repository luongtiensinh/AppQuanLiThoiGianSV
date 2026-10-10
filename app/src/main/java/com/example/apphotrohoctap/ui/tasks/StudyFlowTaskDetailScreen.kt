package com.example.apphotrohoctap.ui.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var subtasks by remember {
        mutableStateOf(
            listOf(
                DetailSubtask("Thiết kế layout Figma cho 5 màn hình", true),
                DetailSubtask("Tích hợp Retrofit gọi API thời tiết", true),
                DetailSubtask("Viết Room Database lưu cache offline", false),
                DetailSubtask("Đóng gói file APK và quay video demo (3 phút)", false)
            )
        )
    }
    var newSubtask by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)
    val checkedCount = subtasks.count { it.checked }
    val progress = if (subtasks.isEmpty()) 0f else checkedCount.toFloat() / subtasks.size

    Box(Modifier.fillMaxSize().background(DetailBackground)) {
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
                TaskDetailHeader(completed)
                TaskMetadataCard()
                TaskProgressCard(checkedCount, subtasks.size, progress)
                TaskSubtasksCard(
                    subtasks = subtasks,
                    newSubtask = newSubtask,
                    onNewSubtaskChange = { newSubtask = it },
                    onToggle = { index -> subtasks = subtasks.toMutableList().also { it[index] = it[index].copy(checked = !it[index].checked) } },
                    onDelete = { index -> subtasks = subtasks.filterIndexed { itemIndex, _ -> itemIndex != index } },
                    onAdd = {
                        if (newSubtask.isNotBlank()) {
                            subtasks = subtasks + DetailSubtask(newSubtask.trim(), false)
                            newSubtask = ""
                        }
                    }
                )
                TaskAttachmentCard()
                TaskNotesCard()
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
                Surface(onClick = onDelete, modifier = Modifier.weight(1f).height(48.dp), color = DetailRedPale, shape = RoundedCornerShape(12.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("▤  Xóa", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DetailRed) }
                }
                Surface(
                    onClick = { completed = !completed },
                    modifier = Modifier.weight(2f).height(48.dp),
                    color = DetailBrand,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (completed) "✓  Đã hoàn thành" else "✓  Đánh dấu hoàn thành", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

private data class DetailSubtask(val title: String, val checked: Boolean)

@Composable
private fun TaskDetailHeader(completed: Boolean) {
    Surface(color = DetailCard, shape = RoundedCornerShape(18.dp), shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(4.dp).height(124.dp).background(if (completed) Color(0xFF34C38F) else Color(0xFFBA1A1A)))
            Column(Modifier.weight(1f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Surface(color = Color(0xFFEFECFF), shape = CircleShape) {
                    Row(Modifier.padding(horizontal = 9.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(DetailBrand))
                        Text("Lập trình Di động", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DetailBrand)
                    }
                }
                Text("Nộp bài tập lớn Android", fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = DetailInk)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailBadge("⚑  Ưu tiên: Cao", DetailRedPale, DetailRed)
                    DetailBadge("◷  Còn 2 ngày 4 giờ", DetailRedPale, DetailRed)
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
private fun TaskMetadataCard() {
    Surface(color = DetailPale, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            MetadataRow("▣", "Hạn nộp", "Thứ Sáu, 12/10/2026", trailing = "Sửa")
            MetadataDivider()
            MetadataRow("◷", "Giờ", "23:59", trailing = "Sửa")
            MetadataDivider()
            MetadataRow("⬡", "Loại", "Bài tập lớn", trailing = "Học phần chính")
        }
    }
}

@Composable
private fun MetadataRow(icon: String, label: String, value: String, trailing: String) {
    Row(Modifier.fillMaxWidth().heightIn(min = 54.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = Color(0xFFE3E0F7), shape = CircleShape) {
            Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) { Text(icon, fontSize = 16.sp, color = DetailBrand) }
        }
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DetailMuted)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
        }
        Text(trailing, fontSize = if (trailing == "Sửa") 12.sp else 9.sp, fontWeight = FontWeight.SemiBold, color = if (trailing == "Sửa") DetailBrand else DetailText)
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
    subtasks: List<DetailSubtask>,
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
                        checked = item.checked,
                        onCheckedChange = { onToggle(index) },
                        modifier = Modifier.size(38.dp),
                        colors = CheckboxDefaults.colors(checkedColor = DetailBrand, uncheckedColor = DetailDivider)
                    )
                    Text(
                        item.title,
                        Modifier.weight(1f).padding(start = 4.dp),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = if (item.checked) DetailMuted else DetailInk,
                        textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None,
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
private fun TaskAttachmentCard() {
    DetailSectionCard(title = "♧  Tài liệu đính kèm") {
        Row(Modifier.fillMaxWidth().background(DetailPale, RoundedCornerShape(12.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("De_cuong_Bai_tap_lon_v2.pdf", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DetailInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("2.4 MB • Tải lên từ LMS", fontSize = 9.sp, color = DetailMuted)
            }
            Surface(onClick = {}, color = Color(0xFFE3E0F7), shape = CircleShape) {
                Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) { Text("↓", fontSize = 17.sp, color = DetailBrand) }
            }
        }
    }
}

@Composable
private fun TaskNotesCard() {
    DetailSectionCard(title = "☷  Ghi chú", trailing = "✎  Chỉnh sửa") {
        Text(
            "Yêu cầu giảng viên TS. Trần Văn Minh: Nộp source code trên GitHub và nộp file báo cáo PDF qua hệ thống LMS trước 23h59. Chú ý kiến trúc MVVM, tách biệt Repository và ViewModel. Video demo phải nói rõ các tính năng chính và xử lý ngoại lệ mất mạng.",
            Modifier.fillMaxWidth().background(DetailPale, RoundedCornerShape(12.dp)).padding(12.dp),
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = DetailText
        )
    }
}

@Composable
private fun DetailSectionCard(title: String, trailing: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = DetailCard, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DetailInk)
                trailing?.let { Text(it, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = DetailBrand) }
            }
            content()
        }
    }
}
