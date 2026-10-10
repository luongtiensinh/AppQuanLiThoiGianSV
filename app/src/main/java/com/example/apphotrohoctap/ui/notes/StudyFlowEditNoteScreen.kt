package com.example.apphotrohoctap.ui.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EditNoteBackground = Color(0xFFFCF8FF)
private val EditNoteInk = Color(0xFF1A1A2A)
private val EditNoteText = Color(0xFF464555)
private val EditNoteMuted = Color(0xFF767586)
private val EditNoteBrand = Color(0xFF4143D5)
private val EditNotePale = Color(0xFFEFECFF)

@Composable
fun StudyFlowEditNoteScreen(onBack: () -> Unit = {}) {
    var title by remember { mutableStateOf("Kiến trúc MVVM & Room Database trong Android") }
    var menuExpanded by remember { mutableStateOf(false) }
    var selectedTool by remember { mutableStateOf("B") }
    BackHandler(onBack = onBack)

    Box(Modifier.fillMaxSize().background(EditNoteBackground)) {
        Column(Modifier.fillMaxSize()) {
            EditNoteAppHeader(onBack)
            Row(
                Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Chỉnh sửa ghi chú", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = EditNoteInk)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                        Text("Tự động đồng bộ", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EditNoteText)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(onClick = onBack, color = EditNoteBrand, shape = CircleShape) {
                        Text("✓  Lưu", Modifier.padding(horizontal = 16.dp, vertical = 12.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Box {
                        Surface(onClick = { menuExpanded = true }, modifier = Modifier.size(44.dp), color = Color.Transparent, shape = CircleShape) {
                            Box(contentAlignment = Alignment.Center) { Text("⋮", fontSize = 23.sp, color = EditNoteInk) }
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Chia sẻ liên kết") }, onClick = { menuExpanded = false })
                            DropdownMenuItem(text = { Text("Lịch sử phiên bản") }, onClick = { menuExpanded = false })
                            DropdownMenuItem(text = { Text("Xóa ghi chú", color = Color(0xFFBA1A1A)) }, onClick = { menuExpanded = false })
                        }
                    }
                }
            }

            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp).padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 22.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold, color = EditNoteInk),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = EditNoteBrand
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    EditNoteChip("▣  Lập trình Di động", EditNotePale, EditNoteInk)
                    EditNoteChip("▱  Học kỳ 2 • Tuần 4", Color(0xFFF5F2FF), EditNoteText)
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        listOf(Color(0xFF4143D5), Color(0xFF0EA5E9), Color(0xFFF59E0B), Color(0xFF10B981)).forEachIndexed { index, color ->
                            Surface(onClick = {}, modifier = Modifier.size(if (index == 0) 26.dp else 22.dp), color = color, shape = CircleShape) {
                                if (index == 0) Box(contentAlignment = Alignment.Center) { Text("✓", fontSize = 11.sp, color = Color.White) }
                            }
                        }
                    }
                }

                Surface(color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 3.dp) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("1. Tổng quan kiến trúc MVVM", fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold, color = EditNoteBrand)
                            Text("Tách biệt hoàn toàn giao diện người dùng (View) khỏi dữ liệu nghiệp vụ (ViewModel) và tầng lưu trữ bền vững (Repository / Room Database).", fontSize = 12.sp, lineHeight = 19.sp, color = EditNoteText)
                        }
                        Surface(color = Color(0xFFF5F2FF), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("CÁC BƯỚC TRIỂN KHAI BÀI TẬP LỚN", fontSize = 10.sp, letterSpacing = 0.3.sp, fontWeight = FontWeight.SemiBold, color = EditNoteBrand)
                                EditNoteChecklistItem("Cấu hình dependencies", true)
                                EditNoteChecklistItem("Khai báo Entity và Database", true)
                                EditNoteChecklistItem("Định nghĩa DAO với các annotations @Query, @Insert", false)
                                EditNoteChecklistItem("Tích hợp StateFlow để quan sát dữ liệu phản ứng trong Jetpack Compose", false)
                            }
                        }
                        Surface(color = Color(0xFFE8E6FC).copy(alpha = .6f), shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("●  Lưu ý quan trọng của Giảng viên", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EditNoteBrand)
                                Text("• Tránh gọi truy vấn database trực tiếp trên Main Thread; bắt buộc sử dụng Dispatchers.IO với Kotlin Coroutines để bảo đảm hiệu năng render 60fps mượt mà.", fontSize = 12.sp, lineHeight = 19.sp, color = EditNoteInk)
                            }
                        }
                        Surface(color = EditNotePale, shape = RoundedCornerShape(12.dp)) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("KOTLIN EXAMPLE", fontSize = 9.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Bold, color = EditNoteMuted)
                                Text("@Dao\ninterface NoteDao {\n    @Query(\"SELECT * FROM notes_table\")\n    fun getAllNotes(): Flow<List<NoteEntity>>\n}", fontSize = 11.sp, lineHeight = 18.sp, color = EditNoteBrand)
                            }
                        }
                    }
                }

                Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("◷  Chỉnh sửa lần cuối: 09/10/2026 • 20:45", fontSize = 9.sp, color = EditNoteMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    EditNoteChip("382 từ • 4 mục kiểm tra", EditNotePale, EditNoteBrand)
                }
            }
        }

        Surface(Modifier.align(Alignment.BottomCenter), color = Color.White.copy(alpha = .96f), shadowElevation = 8.dp) {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                listOf("B", "I", "U", "☷", "☑", "‹›").forEach { tool ->
                    Surface(onClick = { selectedTool = tool }, modifier = Modifier.size(40.dp), color = if (selectedTool == tool) EditNoteBrand else Color.Transparent, shape = RoundedCornerShape(10.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text(tool, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (selectedTool == tool) Color.White else EditNoteInk) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditNoteAppHeader(onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(onClick = onBack, modifier = Modifier.size(36.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 28.sp, color = EditNoteInk) }
            }
            Text("▣", fontSize = 20.sp, color = EditNoteBrand)
            Text("Soạn Thảo Ghi Chú", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = EditNoteInk)
        }
        Surface(color = EditNotePale, shape = CircleShape) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Text("N", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditNoteBrand) }
        }
    }
}

@Composable
private fun EditNoteChip(text: String, background: Color, foreground: Color) {
    Surface(color = background, shape = CircleShape) {
        Text(text, Modifier.padding(horizontal = 9.dp, vertical = 6.dp), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = foreground, maxLines = 1)
    }
}

@Composable
private fun EditNoteChecklistItem(text: String, checked: Boolean) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Checkbox(
            checked = checked,
            onCheckedChange = {},
            modifier = Modifier.size(22.dp),
            colors = CheckboxDefaults.colors(checkedColor = EditNoteBrand, uncheckedColor = Color(0xFFE3E0F7))
        )
        Text(text, Modifier.weight(1f).padding(top = 3.dp), fontSize = 11.sp, lineHeight = 16.sp, color = EditNoteInk)
    }
}
