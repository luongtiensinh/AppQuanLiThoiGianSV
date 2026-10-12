package com.example.apphotrohoctap.ui.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AddScheduleBg = Color(0xFFFCF8FF)
private val AddScheduleInk = Color(0xFF1A1A2A)
private val AddScheduleText = Color(0xFF464555)
private val AddScheduleMuted = Color(0xFF767586)
private val AddScheduleBrand = Color(0xFF4143D5)
private val AddScheduleBright = Color(0xFF5B5FEF)
private val AddScheduleInput = Color(0xFFF5F2FF)
private val AddScheduleChip = Color(0xFFEFECFF)

@Composable
fun StudyFlowAddScheduleScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var selectedType by remember { mutableStateOf("Buổi học") }
    var courseName by remember { mutableStateOf("Lập trình Di động") }
    var courseCode by remember { mutableStateOf("IT4785") }
    var selectedDay by remember { mutableStateOf("T4") }
    var startPeriod by remember { mutableStateOf("Tiết 1") }
    var endPeriod by remember { mutableStateOf("Tiết 3") }
    var room by remember { mutableStateOf("A3-204") }
    var instructor by remember { mutableStateOf("TS. Trần Văn Minh") }
    var repeatsWeekly by remember { mutableStateOf(true) }
    var startDate by remember { mutableStateOf("15/01/2026") }
    var endDate by remember { mutableStateOf("28/05/2026") }
    var selectedColor by remember { mutableIntStateOf(4) }
    var notes by remember { mutableStateOf("Mang theo laptop cài sẵn Android Studio, chuẩn bị báo cáo tiến độ tuần 4.") }

    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(AddScheduleBg)) {
        AddScheduleHeader(onBack)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ScheduleTypeSelector(selectedType) { selectedType = it }
            SemesterHintCard()

            FormSection {
                FormTextField("Tên môn học", courseName, { courseName = it }, required = true, leading = "▤")
                FormTextField("Mã môn học (Không bắt buộc)", courseCode, { courseCode = it }, leading = "#")
            }

            FormSection {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Thứ trong tuần *", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleInk)
                    Text("Đang chọn: ${dayName(selectedDay)}", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleBrand)
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                        val selected = day == selectedDay
                        Surface(onClick = { selectedDay = day }, modifier = Modifier.size(width = 40.dp, height = 44.dp), color = if (selected) AddScheduleBrand else AddScheduleInput, shape = RoundedCornerShape(12.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(day, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (selected) Color.White else AddScheduleText) }
                        }
                    }
                }
            }

            FormSection {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Tiết học & Thời gian *", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleInk)
                    Text("Buổi sáng (Ca 1)", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = AddScheduleText)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PeriodPicker("Từ tiết", startPeriod, Modifier.weight(1f)) { startPeriod = it }
                    PeriodPicker("Đến tiết", endPeriod, Modifier.weight(1f)) { endPeriod = it }
                }
                Surface(color = AddScheduleChip, shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("◷", fontSize = 13.sp, color = AddScheduleBrand)
                        Text("${startPeriod.substringAfter("Tiết ")} - ${endPeriod.substringAfter("Tiết ")} • 07:00 - 09:45 (3 tiết học)", fontSize = 9.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = AddScheduleBrand)
                    }
                }
            }

            FormSection {
                FormTextField("Phòng học", room, { room = it }, leading = "⌂")
                FormTextField("Giảng viên phụ trách", instructor, { instructor = it }, leading = "♙")
            }

            FormSection {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Lặp lại hàng tuần", fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleInk)
                        Text("Áp dụng cố định cho cả học kỳ", fontSize = 9.sp, color = AddScheduleMuted)
                    }
                    Switch(
                        checked = repeatsWeekly,
                        onCheckedChange = { repeatsWeekly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AddScheduleBrand)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DateField("Ngày bắt đầu", startDate, { startDate = it }, Modifier.weight(1f))
                    DateField("Ngày kết thúc", endDate, { endDate = it }, Modifier.weight(1f))
                }
            }

            FormSection {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Màu thẻ hiển thị", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleInk)
                    Text(listOf("Xanh dương", "Đỏ san hô", "Xanh lá", "Cam hổ phách", "Tím Indigo", "Xanh mòng két")[selectedColor], fontSize = 9.sp, color = AddScheduleMuted)
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    listOf(Color(0xFF2563EB), Color(0xFFE11D48), Color(0xFF16A34A), Color(0xFFD97706), AddScheduleBright, Color(0xFF0D9488)).forEachIndexed { index, color ->
                        Surface(onClick = { selectedColor = index }, modifier = Modifier.size(40.dp), color = color, shape = CircleShape) {
                            Box(contentAlignment = Alignment.Center) { if (selectedColor == index) Text("✓", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                        }
                    }
                }
            }

            FormSection {
                Text("Ghi chú (Không bắt buộc)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleInk)
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 92.dp),
                    placeholder = { Text("Nhập ghi chú cho buổi học...", fontSize = 11.sp, color = AddScheduleMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AddScheduleInput,
                        unfocusedContainerColor = AddScheduleInput,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AddScheduleBrand
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        Surface(color = AddScheduleBg.copy(alpha = .96f), shadowElevation = 4.dp) {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AddScheduleBrand)
            ) {
                Text("✓", fontSize = 13.sp, color = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Lưu", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun AddScheduleHeader(onBack: () -> Unit) {
    Surface(color = AddScheduleBg.copy(alpha = .96f), shadowElevation = 2.dp) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(onClick = onBack, modifier = Modifier.size(width = 32.dp, height = 44.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.CenterStart) { Text("×", fontSize = 25.sp, color = AddScheduleText) }
            }
            Spacer(Modifier.width(8.dp))
            Text("Thêm Lịch học Mới", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleInk)
        }
    }
}

@Composable
private fun ScheduleTypeSelector(selected: String, onSelected: (String) -> Unit) {
    Surface(color = AddScheduleChip, shape = CircleShape) {
        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Buổi học" to "♧", "Sự kiện khác" to "▣").forEach { (title, icon) ->
                val active = title == selected
                Surface(onClick = { onSelected(title) }, modifier = Modifier.weight(1f), color = if (active) AddScheduleBright else Color.Transparent, shape = CircleShape, shadowElevation = if (active) 1.dp else 0.dp) {
                    Row(Modifier.padding(vertical = 9.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(icon, fontSize = 10.sp, color = if (active) Color.White else AddScheduleText)
                        Spacer(Modifier.width(5.dp))
                        Text(title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (active) Color.White else AddScheduleText)
                    }
                }
            }
        }
    }
}

@Composable
private fun SemesterHintCard() {
    Row(Modifier.fillMaxWidth().background(Color(0xFFF5F2FF), RoundedCornerShape(16.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(color = AddScheduleBright.copy(alpha = .1f), shape = RoundedCornerShape(12.dp)) { Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { Text("▣", fontSize = 18.sp, color = AddScheduleBrand) } }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Học kỳ 2 • Năm học 2025-2026", fontSize = 10.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = AddScheduleBrand)
            Text("Sắp xếp thời khóa biểu thông minh, giảm áp lực ôn thi.", fontSize = 9.sp, lineHeight = 14.sp, color = AddScheduleText, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun FormSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x0F5B5FEF)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun FormTextField(label: String, value: String, onValueChange: (String) -> Unit, required: Boolean = false, leading: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(if (required) "$label *" else label, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = AddScheduleText)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            leadingIcon = { Text(leading, fontSize = 13.sp, color = AddScheduleMuted) },
            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = AddScheduleText),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AddScheduleInput,
                unfocusedContainerColor = AddScheduleInput,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AddScheduleBrand
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun PeriodPicker(label: String, value: String, modifier: Modifier, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 9.sp, color = AddScheduleText)
        Box {
            Surface(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth(), color = AddScheduleInput, shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(value, modifier = Modifier.weight(1f), fontSize = 10.sp, color = AddScheduleText)
                    Text("⌄", fontSize = 12.sp, color = AddScheduleMuted)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                (1..12).forEach { period ->
                    DropdownMenuItem(text = { Text("Tiết $period", fontSize = 12.sp) }, onClick = { onSelected("Tiết $period"); expanded = false })
                }
            }
        }
    }
}

@Composable
private fun DateField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 9.sp, color = AddScheduleText)
        Surface(onClick = { onChange(if (value == "15/01/2026") "22/01/2026" else "15/01/2026") }, modifier = Modifier.fillMaxWidth(), color = AddScheduleInput, shape = RoundedCornerShape(12.dp)) {
            Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("▣", fontSize = 11.sp, color = AddScheduleBrand)
                Text(value, fontSize = 9.sp, color = AddScheduleText, maxLines = 1)
            }
        }
    }
}

private fun dayName(day: String): String = when (day) {
    "T2" -> "Thứ 2"
    "T3" -> "Thứ 3"
    "T4" -> "Thứ 4"
    "T5" -> "Thứ 5"
    "T6" -> "Thứ 6"
    "T7" -> "Thứ 7"
    else -> "Chủ nhật"
}
