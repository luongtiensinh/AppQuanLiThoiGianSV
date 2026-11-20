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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphotrohoctap.data.NoteEntity
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
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context))

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var selectedLabelColor by remember { mutableIntStateOf(0) }
    var courseMenuExpanded by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)

    var isSaving by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(EditorBackground).imePadding()) {
        EditorTopBar(onBack = onBack, onSave = {
            if (isSaving) return@EditorTopBar
            isSaving = true
            
            if (title.isBlank()) {
                android.widget.Toast.makeText(context, "Vui lòng nhập tiêu đề ghi chú", android.widget.Toast.LENGTH_SHORT).show()
                isSaving = false
                return@EditorTopBar
            }
            
            val argb = noteLabelColors[selectedLabelColor].color.toArgb()
            val hexColor = String.format("#%06X", (0xFFFFFF and argb))
            
            val note = NoteEntity(
                title = title,
                content = body,
                courseName = selectedCourse,
                colorKey = hexColor,
                updatedAt = System.currentTimeMillis()
            )
            viewModel.addNote(note) { success ->
                isSaving = false
                if (success) {
                    onSave()
                } else {
                    android.widget.Toast.makeText(context, "Lỗi lưu dữ liệu. Vui lòng thử lại.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        })
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Trang trí bằng Thẻ hướng dẫn (Guidance Banner) cho Ghi chú
            Surface(color = Color(0xFFE8E6FC), shape = RoundedCornerShape(20.dp), shadowElevation = 2.dp) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(color = EditorBrand, shape = RoundedCornerShape(12.dp)) {
                        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { 
                            Text("✍️", fontSize = 18.sp) 
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tạo ghi chú học tập", fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, color = EditorInk)
                        Text(
                            "Tóm tắt bài giảng, ghi lại công thức hay lập dàn ý cho bài thuyết trình sắp tới của bạn!",
                            modifier = Modifier.padding(top = 4.dp),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = EditorText
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(Modifier.padding(16.dp)) {
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

                    val availableCourses by viewModel.availableCourses.collectAsState()

            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(onClick = { courseMenuExpanded = true }, color = EditorPale, shape = CircleShape) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("▤", fontSize = 11.sp, color = EditorBrand)
                            
                            Box(modifier = Modifier.weight(1f)) {
                                androidx.compose.foundation.text.BasicTextField(
                                    value = selectedCourse,
                                    onValueChange = { selectedCourse = it },
                                    textStyle = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EditorBrand),
                                    singleLine = true,
                                    cursorBrush = androidx.compose.ui.graphics.SolidColor(EditorBrand),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { innerTextField ->
                                        Box(contentAlignment = Alignment.CenterStart) {
                                            if (selectedCourse.isBlank()) {
                                                Text("Chọn / Nhập môn học", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EditorBrand, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                            
                            Text("⌄", fontSize = 10.sp, color = EditorBrand)
                        }
                    }
                    DropdownMenu(
                        expanded = courseMenuExpanded, 
                        onDismissRequest = { courseMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        if (availableCourses.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Chưa có môn học trong lịch", fontSize = 12.sp, color = EditorMuted) },
                                onClick = { },
                                enabled = false
                            )
                        } else {
                            availableCourses.forEach { course ->
                                DropdownMenuItem(
                                    text = { Text(course, fontSize = 12.sp, color = EditorInk) }, 
                                    onClick = { selectedCourse = course; courseMenuExpanded = false }
                                )
                            }
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
                    Text("◷  Bản nháp mới tạo", modifier = Modifier.weight(1f), fontSize = 9.sp, color = EditorMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${body.length} ký tự", fontSize = 9.sp, color = EditorMuted)
                }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(36.dp),
                color = Color.Transparent,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("←", fontSize = 24.sp, color = EditorInk)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(color = EditorPale, shape = CircleShape) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(EditorBrand))
                    Text("Bản nháp tự lưu", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = EditorBrand)
                }
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
