package com.example.apphotrohoctap.ui.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EditorBackground = Color(0xFFFCF8FF)
private val EditorInk = Color(0xFF1A1A2A)
private val EditorText = Color(0xFF464555)
private val EditorMuted = Color(0xFF767586)
private val EditorBrand = Color(0xFF4143D5)
private val EditorBright = Color(0xFF5B5FEF)
private val EditorPale = Color(0xFFEFECFF)

private data class NoteLabelColor(val name: String, val color: Color)

private val noteLabelColors = listOf(
    NoteLabelColor("Màu chàm", Color(0xFF5B5FEF)),
    NoteLabelColor("Màu xanh dương", Color(0xFF3B82F6)),
    NoteLabelColor("Màu lục ngọc", Color(0xFF10B981)),
    NoteLabelColor("Màu hổ phách", Color(0xFFF59E0B)),
    NoteLabelColor("Màu hồng hoa hồng", Color(0xFFF43F5E)),
    NoteLabelColor("Màu tím biếc", Color(0xFF8B5CF6))
)

@Composable
fun StudyFlowAddNoteScreen(onBack: () -> Unit = {}, onSave: () -> Unit = {}) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var selectedLabelColor by remember { mutableIntStateOf(0) }
    var courseMenuExpanded by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)

    Column(Modifier.fillMaxSize().background(EditorBackground)) {
        EditorTopBar(onBack = onBack, onSave = onSave)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                placeholder = { Text("Tiêu đề ghi chú", fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7A6B4)) },
                textStyle = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, color = EditorInk),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = EditorBrand
                )
            )

            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    Surface(onClick = { courseMenuExpanded = true }, color = EditorPale, shape = CircleShape) {
                        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("▤", fontSize = 11.sp, color = EditorBrand)
                            Text(selectedCourse.ifBlank { "Chọn môn học" }, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EditorBrand, maxLines = 1)
                            Text("⌄", fontSize = 10.sp, color = EditorBrand)
                        }
                    }
                    DropdownMenu(expanded = courseMenuExpanded, onDismissRequest = { courseMenuExpanded = false }) {
                        listOf("CSDL Nâng cao", "Lập trình Di động", "Điện toán đám mây", "Tiếng Anh CNTT").forEach { course ->
                            DropdownMenuItem(text = { Text(course, fontSize = 12.sp) }, onClick = { selectedCourse = course; courseMenuExpanded = false })
                        }
                    }
                }
                Surface(color = Color(0xFFF5F2FF), shape = CircleShape) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("▱", fontSize = 10.sp, color = EditorMuted)
                        Text("Học kỳ 1 • 2026", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = EditorText)
                    }
                }
            }

            LabelColorSelector(selectedLabelColor, onColorSelected = { selectedLabelColor = it })

            TextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 360.dp).padding(top = 14.dp),
                placeholder = {
                    Text(
                        "Viết ghi chú của bạn ở đây... Bạn có thể lập dàn ý bài giảng, ghi chú ôn thi hoặc tổng hợp công thức môn học.",
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        color = EditorMuted
                    )
                },
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 26.sp, color = EditorText),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = EditorBrand
                )
            )

            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("◷  Chỉnh sửa lần cuối: 10/10/2026 • 20:45", modifier = Modifier.weight(1f), fontSize = 9.sp, color = EditorMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${body.length} ký tự", fontSize = 9.sp, color = EditorMuted)
            }
        }
    }
}

@Composable
private fun EditorTopBar(onBack: () -> Unit, onSave: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(color = EditorPale, shape = CircleShape) {
            Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(EditorBrand))
                Text("Bản nháp tự lưu", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = EditorBrand)
            }
        }
        Surface(onClick = onSave, color = EditorBrand, shape = CircleShape) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("✓", fontSize = 11.sp, color = Color.White)
                Text("Lưu", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun LabelColorSelector(selectedIndex: Int, onColorSelected: (Int) -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(12.dp), shadowElevation = 1.dp) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Màu nhãn:", fontSize = 9.sp, color = EditorText)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                noteLabelColors.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    Surface(
                        onClick = { onColorSelected(index) },
                        modifier = Modifier.size(24.dp),
                        color = item.color,
                        shape = CircleShape,
                        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null,
                        shadowElevation = if (selected) 2.dp else 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) { if (selected) Text("✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                }
            }
        }
    }
}
