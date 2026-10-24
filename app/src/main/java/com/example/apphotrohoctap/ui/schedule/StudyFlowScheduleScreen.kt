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
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphotrohoctap.data.toUiModel
import com.example.apphotrohoctap.R
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio

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

@Composable
fun StudyFlowScheduleScreen(
    onTabSelected: (String) -> Unit = {},
    onNotificationClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onAddEvent: () -> Unit = {},
    onEditEvent: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val handleNotificationClick = onNotificationClick ?: { com.example.apphotrohoctap.ui.common.showComingSoon(context) }
    val handleProfileClick = onProfileClick ?: { com.example.apphotrohoctap.ui.common.showComingSoon(context) }
    val viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModelFactory(context))
    val schedules by viewModel.schedules.collectAsState()

    val now = System.currentTimeMillis()
    var selectedDateMillis by remember { mutableStateOf(now) }
    var currentMonthMillis by remember { mutableStateOf(now) }
    
    val weekDays = com.example.apphotrohoctap.ui.common.AcademicCalendar.generateWeekDays(selectedDateMillis)
    var selectedView by remember { mutableStateOf("Tuần") }
    var selectedDay by remember(selectedDateMillis) { mutableStateOf(com.example.apphotrohoctap.ui.common.AcademicCalendar.currentDayString(selectedDateMillis)) }

    val selectedWeekdayStr = weekDays.find { it.dayOfMonth == selectedDay }?.weekdayLabel ?: "T4"
    val selectedDayInt = when (selectedWeekdayStr) {
        "T2" -> 1
        "T3" -> 2
        "T4" -> 3
        "T5" -> 4
        "T6" -> 5
        "T7" -> 6
        else -> 7
    }

    // For Week View and timeline underneath Month View
    val dayClasses = schedules
        .filter { it.dayOfWeek == selectedDayInt }
        .map { it.toUiModel(now) }
        
    var scheduleToDelete by remember { mutableStateOf<com.example.apphotrohoctap.data.ScheduleEntity?>(null) }
    
    if (scheduleToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            title = { Text("Xóa lịch học", fontWeight = FontWeight.SemiBold) },
            text = { Text("Bạn có chắc chắn muốn xóa lịch học này không? Nếu đây là lịch lặp hàng tuần, hành động này sẽ xóa toàn bộ chuỗi lặp (các task và ghi chú liên quan sẽ không bị ảnh hưởng).") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        scheduleToDelete?.let { viewModel.deleteSchedule(it) }
                        scheduleToDelete = null
                    }
                ) { Text("Xóa", color = ScheduleRed) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { scheduleToDelete = null }) { Text("Hủy") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(ScheduleBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScheduleHeader(handleNotificationClick, handleProfileClick)
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
                        MonthInformationBanner(
                            dateMillis = selectedDateMillis,
                            onToday = {
                                val current = System.currentTimeMillis()
                                selectedDateMillis = current
                                currentMonthMillis = current
                                selectedDay = com.example.apphotrohoctap.ui.common.AcademicCalendar.currentDayString(current)
                            }
                        )
                        MonthCalendarWidget(
                            currentMonthMillis = currentMonthMillis,
                            selectedDateMillis = selectedDateMillis,
                            schedules = schedules,
                            onMonthChange = { currentMonthMillis = it },
                            onDateSelected = { millis -> 
                                selectedDateMillis = millis
                                selectedDay = com.example.apphotrohoctap.ui.common.AcademicCalendar.currentDayString(millis)
                            }
                        )
                        DailySummary(dayClasses.size)
                        DailyTimeline(
                            classes = dayClasses,
                            onEdit = onEditEvent,
                            onDelete = { classId -> scheduleToDelete = schedules.find { it.id == classId } }
                        )
                    } else {
                        MonthInformationBanner(
                            dateMillis = selectedDateMillis,
                            onPrevious = {
                                val cal = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                                cal.add(java.util.Calendar.DAY_OF_YEAR, -7)
                                selectedDateMillis = cal.timeInMillis
                                selectedDay = com.example.apphotrohoctap.ui.common.AcademicCalendar.currentDayString(selectedDateMillis)
                            },
                            onNext = {
                                val cal = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                                cal.add(java.util.Calendar.DAY_OF_YEAR, 7)
                                selectedDateMillis = cal.timeInMillis
                                selectedDay = com.example.apphotrohoctap.ui.common.AcademicCalendar.currentDayString(selectedDateMillis)
                            },
                            onToday = {
                                val current = System.currentTimeMillis()
                                selectedDateMillis = current
                                selectedDay = com.example.apphotrohoctap.ui.common.AcademicCalendar.currentDayString(current)
                            }
                        )
                        WeekDateCarousel(weekDays, selectedDay, onDaySelected = { 
                            selectedDay = it 
                            // Convert string day back to selectedDateMillis approx (for seamless switching to month view)
                            val dayInt = it.toIntOrNull()
                            if (dayInt != null) {
                                val cal = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                                cal.set(java.util.Calendar.DAY_OF_MONTH, dayInt)
                                selectedDateMillis = cal.timeInMillis
                            }
                        })
                        DailySummary(dayClasses.size)
                        DailyTimeline(
                            classes = dayClasses,
                            onEdit = onEditEvent,
                            onDelete = { classId ->
                                scheduleToDelete = schedules.find { it.id == classId }
                            }
                        )
                        val weekNumber = com.example.apphotrohoctap.ui.common.AcademicCalendar.weekOfSemester(selectedDateMillis)
                        val weekLabel = if (weekNumber > 0) "tuần $weekNumber" else ""
                        AttendanceSummary(weekLabel)
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
    val context = LocalContext.current
    val now = System.currentTimeMillis()
    val semesterLabel = com.example.apphotrohoctap.ui.common.AcademicCalendar.semesterLabel(now)
    
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Thời khóa biểu", fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = (-0.5).sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
                Text(semesterLabel, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleMuted)
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
                    onClick = { com.example.apphotrohoctap.ui.common.showComingSoon(context) },
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
private fun MonthInformationBanner(
    dateMillis: Long,
    onPrevious: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
    onToday: (() -> Unit)? = null
) {
    val monthYearLabel = com.example.apphotrohoctap.ui.common.AcademicCalendar.monthYearLabel(dateMillis)
    val weekNumber = com.example.apphotrohoctap.ui.common.AcademicCalendar.weekOfSemester(dateMillis)
    val weekLabel = if (weekNumber > 0) "Tuần $weekNumber" else ""
    
    Surface(color = SchedulePale, shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScheduleIcon(1, Modifier.width(13.5.dp).height(15.dp), "")
            Spacer(Modifier.width(8.dp))
            Text(monthYearLabel, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
            if (weekLabel.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFC6C5D7)))
                Spacer(Modifier.width(8.dp))
                Text(weekLabel, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
            }
            Spacer(Modifier.weight(1f))
            
            if (onPrevious != null && onNext != null) {
                androidx.compose.material3.IconButton(onClick = onPrevious, modifier = Modifier.size(24.dp)) {
                    Text("‹", fontSize = 18.sp, color = ScheduleBrandBlue, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(4.dp))
            }
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(enabled = onToday != null) { onToday?.invoke() }
                    .padding(horizontal = 4.dp, vertical = 2.dp), 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hôm nay", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
                Spacer(Modifier.width(2.dp))
                ScheduleIcon(2, Modifier.width(4.317.dp).height(7.dp), "")
            }
            
            if (onPrevious != null && onNext != null) {
                Spacer(Modifier.width(4.dp))
                androidx.compose.material3.IconButton(onClick = onNext, modifier = Modifier.size(24.dp)) {
                    Text("›", fontSize = 18.sp, color = ScheduleBrandBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarWidget(
    currentMonthMillis: Long,
    selectedDateMillis: Long,
    schedules: List<com.example.apphotrohoctap.data.ScheduleEntity>,
    onMonthChange: (Long) -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
    cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
    val month = cal.get(java.util.Calendar.MONTH)
    val year = cal.get(java.util.Calendar.YEAR)
    
    val daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    var firstDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1
    if (firstDayOfWeek == 0) firstDayOfWeek = 7
    
    val monthName = "Tháng ${month + 1}, $year"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.IconButton(onClick = { 
                    val navCal = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
                    navCal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                    navCal.add(java.util.Calendar.MONTH, -1)
                    onMonthChange(navCal.timeInMillis)
                }) { Text("‹", fontSize = 24.sp, color = ScheduleInk) }
                
                Text(monthName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ScheduleInk)
                
                androidx.compose.material3.IconButton(onClick = { 
                    val navCal = java.util.Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
                    navCal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                    navCal.add(java.util.Calendar.MONTH, 1)
                    onMonthChange(navCal.timeInMillis)
                }) { Text("›", fontSize = 24.sp, color = ScheduleInk) }
            }
            
            // Weekdays
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach {
                    Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, color = ScheduleSecondary, fontWeight = FontWeight.SemiBold)
                }
            }
            
            // Days Grid
            val gridItems = List(firstDayOfWeek - 1) { 0 } + (1..daysInMonth).toList()
            val rows = gridItems.chunked(7)
            
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    row.forEach { day ->
                        if (day == 0) {
                            Spacer(Modifier.weight(1f))
                        } else {
                            val dayCal = java.util.Calendar.getInstance().apply {
                                timeInMillis = currentMonthMillis
                                set(java.util.Calendar.DAY_OF_MONTH, day)
                                set(java.util.Calendar.HOUR_OF_DAY, 0)
                                set(java.util.Calendar.MINUTE, 0)
                                set(java.util.Calendar.SECOND, 0)
                                set(java.util.Calendar.MILLISECOND, 0)
                            }
                            val dayMillis = dayCal.timeInMillis
                            
                            val selCal = java.util.Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                            val isSelected = day == selCal.get(java.util.Calendar.DAY_OF_MONTH) && month == selCal.get(java.util.Calendar.MONTH) && year == selCal.get(java.util.Calendar.YEAR)
                            
                            val nowCal = java.util.Calendar.getInstance()
                            val isToday = day == nowCal.get(java.util.Calendar.DAY_OF_MONTH) && month == nowCal.get(java.util.Calendar.MONTH) && year == nowCal.get(java.util.Calendar.YEAR)
                            
                            val hasEvents = schedules.any { s ->
                                val dow = if (dayCal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY) 7 else dayCal.get(java.util.Calendar.DAY_OF_WEEK) - 1
                                s.dayOfWeek == dow && dayMillis in s.startDateMillis..s.endDateMillis
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ScheduleBrightBlue else Color.Transparent)
                                    .clickable { onDateSelected(dayMillis) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(
                                        day.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else if (isToday) ScheduleBrandBlue else ScheduleInk
                                    )
                                    if (hasEvents) {
                                        Box(Modifier.padding(top = 2.dp).size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else ScheduleBrandBlue))
                                    }
                                }
                            }
                        }
                    }
                    repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun WeekDateCarousel(weekDays: List<com.example.apphotrohoctap.ui.common.WeekDayUi>, selectedDay: String, onDaySelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        weekDays.forEach { uiObj ->
            val weekday = uiObj.weekdayLabel
            val dayStr = uiObj.dayOfMonth
            val selected = dayStr == selectedDay
            Surface(
                onClick = { onDaySelected(dayStr) },
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
                        weekday,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) Color.White.copy(alpha = 0.9f) else ScheduleSecondary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        dayStr,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selected) Color.White else if (weekday == "CN") ScheduleRed else ScheduleSecondary
                    )
                    Box(
                        modifier = Modifier.padding(top = 4.dp).size(6.dp)
                            .clip(CircleShape)
                            .background(
                                when {
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
private fun DailySummary(classCount: Int) {
    Surface(color = Color(0xFFEFECFF), shape = CircleShape) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(ScheduleBrandBlue))
            Text("Hôm nay có $classCount ca học", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleSecondary)
        }
    }
}

@Composable
private fun DailyTimeline(classes: List<StudyClass>, onEdit: (Long) -> Unit, onDelete: (Long) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (classes.isEmpty()) {
            Text(
                "Không có lịch học nào.",
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                color = ScheduleSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        } else {
            classes.forEach { item ->
                val startTimeStr = item.time.substringBefore(" - ")
                TimelineEventRow(time = startTimeStr, content = {
                    DynamicClassCard(item, onEdit, onDelete)
                }, topSpacing = 8.dp)
            }
        }
        TimelineDividerRow("20:00", note = "Kết thúc lịch học buổi tối", showMoon = true)
    }
}

@Composable
private fun DynamicClassCard(item: StudyClass, onEdit: (Long) -> Unit, onDelete: (Long) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        ScheduleClassCard(accent = item.leadingColor, onLongClick = { expanded = true }) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = item.statusColor, shape = CircleShape) {
                    Row(Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (item.status == "Đang diễn ra") Box(Modifier.size(6.dp).clip(CircleShape).background(ScheduleBrandBlue))
                        Text(item.status, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = item.statusTextColor)
                    }
                }
                Spacer(Modifier.width(6.dp))
                Text(item.session, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Medium, color = ScheduleMuted)
                Spacer(Modifier.weight(1f))
                Text(item.time, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold, color = ScheduleBrandBlue)
                androidx.compose.material3.IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(24.dp).padding(start = 4.dp)
                ) {
                    Text("⋮", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ScheduleSecondary)
                }
            }
            Text(item.title, fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
            ScheduleMetaRow(4, Modifier.size(12.dp), "Phòng ${item.place}")
            ScheduleMetaRow(5, Modifier.size(10.667.dp), "GV: ${item.teacher}")
        }
        
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text("Sửa lịch học") },
                onClick = {
                    expanded = false
                    onEdit(item.id)
                }
            )
            androidx.compose.material3.DropdownMenuItem(
                text = { Text("Xóa lịch học", color = ScheduleRed) },
                onClick = {
                    expanded = false
                    onDelete(item.id)
                }
            )
        }
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun ScheduleClassCard(accent: Color, onLongClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        val baseModifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(color = accent, size = androidx.compose.ui.geometry.Size(6.dp.toPx(), size.height))
            }
        
        Box(
            modifier = if (onLongClick != null) {
                baseModifier.combinedClickable(
                    onClick = {},
                    onLongClick = onLongClick
                )
            } else baseModifier
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
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
private fun AttendanceSummary(weekLabel: String) {
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
                Text(if (weekLabel.isNotEmpty()) "Tiến độ điểm danh $weekLabel" else "Tiến độ điểm danh", fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = ScheduleInk)
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
