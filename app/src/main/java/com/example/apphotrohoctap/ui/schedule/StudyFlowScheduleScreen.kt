package com.example.apphotrohoctap.ui.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.R

private val ScheduleBackground = Color(0xFFFCF8FF)
private val ScheduleBrandBlue = Color(0xFF4143D5)
private val ScheduleBrightBlue = Color(0xFF5B5FEF)
private val ScheduleInk = Color(0xFF1A1A2A)
private val ScheduleSecondary = Color(0xFF464555)
private val ScheduleMuted = Color(0xFF555962)
private val ScheduleLine = Color(0xFFE3E0F7)
private val SchedulePale = Color(0xFFF5F2FF)
private val ScheduleRed = Color(0xFFBA1A1A)
private val ScheduleRedPale = Color(0xFFFFEDEA)

private data class CalendarDay(val weekday: String, val day: String, val hasEvents: Boolean = false)

private val calendarDays = listOf(
    CalendarDay("T2", "22", true),
    CalendarDay("T3", "23", true),
    CalendarDay("T4", "24", true),
    CalendarDay("T5", "25", true),
    CalendarDay("T6", "26", true),
    CalendarDay("T7", "27"),
    CalendarDay("CN", "28")
)

@Composable
fun StudyFlowScheduleScreen(
    onTabSelected: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddEvent: () -> Unit = {}
) {
    var selectedView by remember { mutableStateOf("Tuần") }
    var selectedDay by remember { mutableStateOf("24") }

    Box(modifier = Modifier.fillMaxSize().background(ScheduleBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScheduleHeader(onNotificationClick, onProfileClick)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScheduleControls(selectedView, onViewChange = { selectedView = it })
                    if (selectedView == "Tháng") {
                        MonthInformationBanner()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(SchedulePale, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Chế độ xem Tháng\nđang được phát triển",
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScheduleSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Vui lòng sử dụng chế độ xem Tuần",
                                    fontSize = 12.sp,
                                    color = ScheduleMuted
                                )
                            }
                        }
                    } else {
                        MonthInformationBanner()
                        WeekDateCarousel(selectedDay, onDaySelected = { selectedDay = it })
                        DailySummary()
                        DailyTimeline()
                        AttendanceSummary()
                    }
                }
            }
            ScheduleBottomNavigation(onTabSelected)
        }

        Surface(
            onClick = onAddEvent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 96.dp)
                .size(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0x595B5FEF)),
            shape = RoundedCornerShape(16.dp),
            color = ScheduleBrightBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                ScheduleIcon(15, Modifier.size(16.333.dp), "Thêm lịch học")
            }
        }
    }
}

@Composable
private fun ScheduleHeader(onNotificationClick: () -> Unit, onProfileClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        color = ScheduleBackground.copy(alpha = 0.92f),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(
                    painter = painterResource(R.drawable.studyflow_logo),
                    contentDescription = "StudyFlow",
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text("StudyFlow", fontSize = 18.sp, lineHeight = 18.sp, letterSpacing = (-0.45).sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
                    Text("Lịch biểu", fontSize = 15.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Surface(onClick = onNotificationClick, modifier = Modifier.size(48.dp), color = Color.Transparent, shape = CircleShape) {
                        Box(contentAlignment = Alignment.Center) {
                            ScheduleIcon(16, Modifier.width(16.dp).height(20.dp), "Thông báo")
                        }
                    }
                    Box(
                        Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp).size(8.dp)
                            .clip(CircleShape).background(ScheduleRed)
                    )
                }
                Surface(onClick = onProfileClick, modifier = Modifier.size(48.dp), color = Color.Transparent, shape = CircleShape) {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/studyflow/home_profile.png")
                                .build(),
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
private fun ScheduleControls(selectedView: String, onViewChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Thời khóa biểu", fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.5).sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
                Text("Học kỳ II (2023 - 2024)", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = Color(0xFFEFECFF), shape = CircleShape, shadowElevation = 1.dp) {
                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf("Tuần", "Tháng").forEach { label ->
                            val selected = selectedView == label
                            Surface(
                                onClick = { onViewChange(label) },
                                shape = CircleShape,
                                color = if (selected) ScheduleBrightBlue else Color.Transparent,
                                shadowElevation = if (selected) 1.dp else 0.dp
                            ) {
                                Text(
                                    label,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) Color.White else ScheduleSecondary
                                )
                            }
                        }
                    }
                }
                Surface(
                    onClick = {},
                    modifier = Modifier.size(36.dp),
                    color = Color(0xFFEFECFF),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) { ScheduleIcon(0, Modifier.size(15.dp), "Lọc môn học") }
                }
            }
        }
    }
}

@Composable
private fun MonthInformationBanner() {
    Surface(color = SchedulePale, shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScheduleIcon(1, Modifier.width(13.5.dp).height(15.dp), "")
            Spacer(Modifier.width(8.dp))
            Text("Tháng 4, 2024", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFC6C5D7)))
            Spacer(Modifier.width(8.dp))
            Text("Tuần 8", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
            Spacer(Modifier.weight(1f))
            Text("Hôm nay", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
            Spacer(Modifier.width(2.dp))
            ScheduleIcon(2, Modifier.width(4.317.dp).height(7.dp), "")
        }
    }
}

@Composable
private fun WeekDateCarousel(selectedDay: String, onDaySelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        calendarDays.forEach { day ->
            val selected = day.day == selectedDay
            Surface(
                onClick = { onDaySelected(day.day) },
                modifier = Modifier.width(if (selected) 50.dp else 44.dp).height(if (selected) 72.dp else 68.dp),
                color = if (selected) ScheduleBrightBlue else Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = if (selected) 4.dp else 1.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        day.weekday,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) Color.White.copy(alpha = 0.9f) else ScheduleSecondary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        day.day,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selected) Color.White else if (day.weekday == "CN") ScheduleRed else ScheduleSecondary
                    )
                    Box(
                        modifier = Modifier.padding(top = 4.dp).size(6.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    !day.hasEvents -> Color.Transparent
                                    selected -> Color.White
                                    else -> ScheduleBrandBlue.copy(alpha = 0.4f)
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun DailySummary() {
    Surface(color = Color(0xFFEFECFF), shape = CircleShape) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(ScheduleBrandBlue))
            Text("Hôm nay có 3 ca học (5 tiết) • 1 bài nộp", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleSecondary)
        }
    }
}

@Composable
private fun DailyTimeline() {
    Column(modifier = Modifier.fillMaxWidth()) {
        TimelineEventRow(time = "07:00", content = { MorningClassCard() })
        CurrentTimeRow()
        TimelineDividerRow("10:00")
        TimelineDividerRow("12:00", note = "Giờ nghỉ trưa tự do")
        TimelineEventRow(time = "13:00", content = { AfternoonClassCard() }, topSpacing = 8.dp)
        TimelineEventRow(time = "15:30", content = { StudyGroupBlock() }, topSpacing = 12.dp)
        TimelineEventRow(time = "18:00", content = { EveningClassCard() }, topSpacing = 12.dp)
        TimelineDividerRow("20:00", note = "Kết thúc lịch học buổi tối", showMoon = true)
    }
}

@Composable
private fun TimelineEventRow(time: String, content: @Composable () -> Unit, topSpacing: androidx.compose.ui.unit.Dp = 0.dp) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = topSpacing), verticalAlignment = Alignment.Top) {
        Text(
            text = time,
            modifier = Modifier.width(40.dp).padding(top = 10.dp),
            textAlign = TextAlign.End,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScheduleMuted
        )
        Spacer(Modifier.width(12.dp))
        Box(Modifier.weight(1f)) { content() }
    }
}

@Composable
private fun CurrentTimeRow() {
    Row(modifier = Modifier.fillMaxWidth().height(36.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("09:15", modifier = Modifier.width(40.dp), textAlign = TextAlign.End, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleRed)
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFD32F2F))) {
            Box(Modifier.align(Alignment.CenterStart).padding(start = 0.dp).size(8.dp).clip(CircleShape).background(Color(0xFFD32F2F)))
        }
    }
}

@Composable
private fun TimelineDividerRow(time: String, note: String? = null, showMoon: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().height(38.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(time, modifier = Modifier.width(40.dp), textAlign = TextAlign.End, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleMuted)
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f).height(1.dp).background(ScheduleLine)) {
            if (note != null) {
            Surface(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                    color = ScheduleBackground,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = if (showMoon) 6.dp else 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(note, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Medium, color = ScheduleMuted)
                        if (showMoon) {
                            Spacer(Modifier.weight(1f))
                            ScheduleIcon(14, Modifier.size(15.dp), "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MorningClassCard() {
    ScheduleClassCard(accent = ScheduleBrightBlue) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFE1E0FF), shape = CircleShape) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(ScheduleBrandBlue))
                    Text("Đang diễn ra", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B2BC5))
                }
            }
            Spacer(Modifier.width(6.dp))
            Text("Tiết 1 - 3", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Medium, color = ScheduleMuted)
            Spacer(Modifier.weight(1f))
            Text("07:30 - 09:50", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
        }
        Text("Cơ sở dữ liệu nâng cao (IT3020)", fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
        ScheduleMetaRow(4, Modifier.size(12.dp), "Phòng B1-302")
        ScheduleMetaRow(5, Modifier.size(10.667.dp), "GV: TS. Nguyễn Thanh Hải")
        Spacer(Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ScheduleIcon(6, Modifier.size(9.333.dp), "")
            Spacer(Modifier.width(4.dp))
            Text("Điểm danh: Đã xác nhận", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Medium, color = ScheduleMuted)
            Spacer(Modifier.weight(1f))
            Text("Tài liệu bài giảng  ›", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
        }
    }
}

@Composable
private fun AfternoonClassCard() {
    ScheduleClassCard(accent = ScheduleRed) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = ScheduleRedPale, shape = CircleShape) {
                Row(Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ScheduleIcon(10, Modifier.size(10.5.dp), "")
                    Text("Có bài nộp", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF93000A))
                }
            }
            Spacer(Modifier.width(6.dp))
            Text("Tiết 6 - 8", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Medium, color = ScheduleMuted)
            Spacer(Modifier.weight(1f))
            Text("13:00 - 15:20", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
        }
        Text("Lập trình Ứng dụng Di động", fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ScheduleIcon(7, Modifier.width(9.75.dp).height(10.833.dp), "")
            Text("Lab 405", fontSize = 10.sp, lineHeight = 14.sp, color = ScheduleSecondary)
            ScheduleIcon(5, Modifier.size(10.667.dp), "")
            Text("GV: ThS. Lê Hoàng Long", fontSize = 10.sp, lineHeight = 14.sp, color = ScheduleSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Surface(color = ScheduleRedPale, shape = RoundedCornerShape(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                ScheduleIcon(9, Modifier.width(2.667.dp).height(12.dp), "")
                Spacer(Modifier.width(4.dp))
                Text("Nộp bài tập lớn Sprint 2 trước 15:00", modifier = Modifier.weight(1f), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF93000A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Chi tiết", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleRed)
            }
        }
    }
}

@Composable
private fun EveningClassCard() {
    ScheduleClassCard(accent = Color(0xFF4446DA)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFE1E0FF), shape = CircleShape) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ScheduleIcon(11, Modifier.width(10.833.dp).height(8.667.dp), "")
                    Text("Trực tuyến", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B2BC5))
                }
            }
            Spacer(Modifier.width(6.dp))
            Text("Ca tối", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleMuted)
            Spacer(Modifier.weight(1f))
            Text("18:00 - 19:30", fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
        }
        Text("Tiếng Anh chuyên ngành CNTT", fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk, maxLines = 2, overflow = TextOverflow.Ellipsis)
        ScheduleMetaRow(12, Modifier.width(13.333.dp).height(12.dp), "MS Teams (Phòng họp 02)")
        ScheduleMetaRow(5, Modifier.size(10.667.dp), "GV: Cô Sarah Evans")
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ScheduleIcon(13, Modifier.width(11.667.dp).height(5.833.dp), "")
            Spacer(Modifier.width(4.dp))
            Text("ID: teams.microsoft.com/stu-en", modifier = Modifier.weight(1f), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Surface(color = Color(0xFF4446DA), shape = CircleShape) {
                Text("Vào lớp học", modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun ScheduleClassCard(accent: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(color = accent, size = androidx.compose.ui.geometry.Size(6.dp.toPx(), size.height))
                }
                .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = content
        )
    }
}

@Composable
private fun ScheduleMetaRow(iconIndex: Int, iconSize: Modifier, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        ScheduleIcon(iconIndex, iconSize, "")
        Text(text, fontSize = 10.sp, lineHeight = 14.sp, color = ScheduleSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun StudyGroupBlock() {
    Surface(color = Color(0xFFF2EEFF), shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = Color(0xFFE1E0FF), shape = CircleShape) {
                Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 18.sp, lineHeight = 22.sp, color = ScheduleBrandBlue)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Khung giờ trống (15:30 - 17:00)", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
                Text("Chạm để thêm tự học hoặc lịch họp nhóm", fontSize = 9.sp, lineHeight = 12.sp, color = ScheduleMuted)
            }
            Surface(color = Color.White, shape = RoundedCornerShape(8.dp)) {
                Text("Thêm +", modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
            }
        }
    }
}

@Composable
private fun AttendanceSummary() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2EEFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(color = Color(0xFFE1E0FF), shape = RoundedCornerShape(10.dp)) {
                Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                    ScheduleIcon(8, Modifier.width(12.038.dp).height(12.dp), "")
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Tiến độ điểm danh tuần 8", fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
                Text("Đạt 95% số tiết theo quy định", fontSize = 9.sp, lineHeight = 12.sp, color = ScheduleSecondary)
            }
            Surface(color = Color(0xFFE1E0FF), shape = CircleShape) {
                Text("Tốt", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
            }
        }
    }
}

private data class ScheduleTab(val title: String, val iconIndex: Int)

private val scheduleTabs = listOf(
    ScheduleTab("Trang chủ", 17),
    ScheduleTab("Lịch biểu", 18),
    ScheduleTab("Nhiệm vụ", 19),
    ScheduleTab("Ghi chú", 20),
    ScheduleTab("Cài đặt", 21)
)

@Composable
private fun ScheduleBottomNavigation(onTabSelected: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), color = ScheduleBackground.copy(alpha = 0.96f), shadowElevation = 4.dp) {
        Row(modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            scheduleTabs.forEach { tab ->
                val selected = tab.title == "Lịch biểu"
                Column(modifier = Modifier.weight(1f).height(56.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Surface(
                        onClick = { onTabSelected(tab.title) },
                        modifier = Modifier.width(56.dp).height(32.dp),
                        color = if (selected) Color(0xFFE1E0FF) else Color.Transparent,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val iconModifier = when (tab.iconIndex) {
                                17 -> Modifier.width(14.667.dp).height(16.5.dp)
                                18 -> Modifier.width(16.5.dp).height(18.333.dp)
                                21 -> Modifier.width(18.425.dp).height(18.333.dp)
                                else -> Modifier.size(18.333.dp)
                            }
                            ScheduleIcon(tab.iconIndex, iconModifier, "")
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(tab.title, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold, color = if (selected) ScheduleBrandBlue else ScheduleSecondary, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun ScheduleIcon(index: Int, modifier: Modifier, contentDescription: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/studyflow/calendar_page_${index.toString().padStart(2, '0')}.svg")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowScheduleScreenPreview() {
    MaterialTheme { StudyFlowScheduleScreen() }
}
