package com.example.apphotrohoctap.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphotrohoctap.ui.schedule.StudyClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val HomeBackground = Color(0xFFFCF8FF)
private val HomeBrandBlue = Color(0xFF4143D5)
private val HomeBrightBlue = Color(0xFF5B5FEF)
private val HomeInk = Color(0xFF1A1A2A)
private val HomeSecondaryInk = Color(0xFF464555)
private val HomeMutedInk = Color(0xFF767586)
private val HomeDivider = Color(0xFFE3E0F7)
private val HomeCardShadow = Color(0x105B5FEF)

@Composable
fun StudyFlowHomeScreen(
    onNotificationClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onSeeAllClasses: (() -> Unit)? = null,
    onCourseClick: (String) -> Unit = {},
    onFilterDeadlines: (() -> Unit)? = null,
    onStartFocus: (() -> Unit)? = null,
    onAddTask: () -> Unit = {},
    onTabSelected: (String) -> Unit = {},
    pomodoroState: com.example.apphotrohoctap.ui.pomodoro.PomodoroUiState? = null
) {
    val localContext = LocalContext.current
    val handleNotificationClick = onNotificationClick ?: { com.example.apphotrohoctap.ui.common.showComingSoon(localContext) }
    val handleProfileClick = onProfileClick ?: { com.example.apphotrohoctap.ui.common.showComingSoon(localContext) }
    val handleSeeAllClasses = onSeeAllClasses ?: { com.example.apphotrohoctap.ui.common.showComingSoon(localContext) }
    val handleFilterDeadlines = onFilterDeadlines ?: { com.example.apphotrohoctap.ui.common.showComingSoon(localContext) }
    val handleStartFocus = onStartFocus ?: { com.example.apphotrohoctap.ui.common.showComingSoon(localContext) }
    val context = LocalContext.current.applicationContext
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(handleNotificationClick, handleProfileClick)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 112.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    GreetingSection()
                    GoalProgressCard(uiState.tasksDone, uiState.tasksTotalThisWeek)

                    val dailyFocusList = listOf(
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("T2", 25),
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("T3", 50),
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("T4", 75),
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("T5", 25),
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("T6", (pomodoroState?.completedFocusSessions ?: 0) * 25),
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("T7", 0),
                        com.example.apphotrohoctap.ui.analytics.DailyFocusData("CN", 0)
                    )
                    val totalFocusMins = dailyFocusList.sumOf { it.focusMinutes }
                    val weeklyReportData = com.example.apphotrohoctap.ui.analytics.WeeklyReportData(
                        totalFocusMinutes = totalFocusMins,
                        completedTasksCount = uiState.tasksDone,
                        totalTasksCount = (uiState.tasksDone + uiState.tasksToday).coerceAtLeast(1),
                        dailyFocusList = dailyFocusList
                    )

                    val completionRate = if (weeklyReportData.totalTasksCount > 0) {
                        uiState.tasksDone.toFloat() / weeklyReportData.totalTasksCount
                    } else 0f

                    val badgesList = com.example.apphotrohoctap.ui.analytics.GamificationEngine.evaluateBadges(
                        completedTasksCount = uiState.tasksDone,
                        completedPomodoroSessions = pomodoroState?.completedFocusSessions ?: 0,
                        hasLateNightTask = uiState.tasksDone > 0,
                        weeklyCompletionRate = completionRate
                    )

                    com.example.apphotrohoctap.ui.analytics.WeeklyAnalyticsCard(reportData = weeklyReportData)
                    com.example.apphotrohoctap.ui.analytics.BadgesSectionCard(badges = badgesList)

                    StatisticsRow(uiState.tasksToday, uiState.tasksUrgent, uiState.deadlinesThisWeek, uiState.tasksDone)
                    TodayScheduleSection(uiState.todaySchedules, handleSeeAllClasses, onCourseClick)
                    UpcomingDeadlinesSection(uiState.upcomingDeadlines, handleFilterDeadlines)
                    FocusSessionCard(
                        onStart = handleStartFocus,
                        pomodoroState = pomodoroState
                    )
                }
            }
            com.example.apphotrohoctap.ui.navigation.AppBottomNavigation(
                currentTab = com.example.apphotrohoctap.ui.navigation.BottomNavTab.HOME,
                onTabSelected = onTabSelected
            )
        }

        Surface(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 96.dp)
                .size(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0x595B5FEF)),
            shape = RoundedCornerShape(16.dp),
            color = HomeBrightBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                HomeIcon("home_add.svg", Modifier.size(16.333.dp), "Thêm nhiệm vụ hoặc lịch mới")
            }
        }
    }
}

@Composable
private fun HomeHeader(onNotificationClick: () -> Unit, onProfileClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = HomeBackground.copy(alpha = 0.92f),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.studyflow_logo),
                    contentDescription = "StudyFlow",
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        "StudyFlow",
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        letterSpacing = (-0.45).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeInk
                    )
                    Text(
                        "Trang Chủ",
                        fontSize = 15.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeBrandBlue
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        onClick = onNotificationClick,
                        modifier = Modifier.size(48.dp),
                        color = Color.Transparent,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            HomeIcon("home_notifications.svg", Modifier.width(16.dp).height(20.dp), "Thông báo")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFBA1A1A))
                    )
                }
                Surface(
                    onClick = onProfileClick,
                    modifier = Modifier.size(48.dp),
                    color = Color.Transparent,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        HomeIcon("home_nav_settings.svg", Modifier.size(24.dp), "Cài đặt")
                    }
                }
            }
        }
    }
}

@Composable
private fun GreetingSection() {
    val now = System.currentTimeMillis()
    val weekNumber = com.example.apphotrohoctap.ui.common.AcademicCalendar.weekOfSemester(now)
    val weekLabel = if (weekNumber > 0) "Tuần học thứ $weekNumber" else "Chưa bắt đầu học kỳ"
    val semesterLabel = com.example.apphotrohoctap.ui.common.AcademicCalendar.semesterLabelShort(now).substringBefore(" (") // Lấy phần "HK I" hoặc "Học kỳ 1"
    val dateLabel = com.example.apphotrohoctap.ui.common.AcademicCalendar.formatTodayVi(now)

    val sessionManager = com.example.apphotrohoctap.data.SessionManager(androidx.compose.ui.platform.LocalContext.current)
    val fullName = sessionManager.getRegisteredFullName() ?: "bạn"
    val firstName = fullName.split(" ").lastOrNull() ?: "bạn"
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Chào $firstName",
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.6).sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeInk
                )
                Text("👋", fontSize = 22.sp, lineHeight = 32.sp)
            }
            Surface(color = Color(0xFFE8E6FC), shape = CircleShape) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(HomeBrandBlue))
                    Text(semesterLabel, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeBrandBlue)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HomeIcon("home_calendar.svg", Modifier.width(12.dp).height(13.333.dp), "")
            Text(dateLabel, fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk)
            Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFC6C5D7)))
            Text(weekLabel, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = HomeBrandBlue)
        }
    }
}

@Composable
private fun GoalProgressCard(tasksDone: Int, tasksTotal: Int) {
    val total = if (tasksTotal == 0) 1 else tasksTotal
    val progress = (tasksDone.toFloat() / total).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0x475B5FEF))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(HomeBrandBlue, HomeBrightBlue, Color(0xFF5E62F4))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        HomeIcon("home_goal.svg", Modifier.size(16.667.dp), "")
                    }
                }
                Text(
                    "Mục tiêu học tập tuần này",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.35).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(color = Color.White, shape = CircleShape, shadowElevation = 1.dp) {
                Text(
                    "${percentage}%",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeBrandBlue
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Tiến độ bài tập & đồ án", fontSize = 12.sp, lineHeight = 16.sp, color = Color(0xFFF9F6FF))
                Spacer(Modifier.weight(1f))
                Text("$tasksDone / $tasksTotal hoàn thành", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeIcon("home_sparkle.svg", Modifier.size(14.667.dp), "")
            Text(
                if (tasksDone == tasksTotal && tasksTotal > 0) "Tuyệt vời! Bạn đã hoàn thành toàn bộ mục tiêu tuần này."
                else "Cố lên! Bạn chỉ còn ${tasksTotal - tasksDone} bài tập nữa là hoàn thành tuần này.",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color(0xFFF5F2FF)
            )
        }
    }
}

@Composable
private fun StatisticsRow(tasksToday: Int, tasksUrgent: Int, deadlinesThisWeek: Int, tasksDone: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        StatisticCard("home_tasks.svg", Modifier.width(15.dp).height(11.306.dp), "Hôm nay", tasksToday.toString(), "$tasksUrgent ưu tiên", Color(0xFFE1E0FF), Color(0xFFBA1A1A), Modifier.weight(1f))
        StatisticCard("home_alarm.svg", Modifier.width(15.975.dp).height(14.738.dp), "Hạn nộp", deadlinesThisWeek.toString(), "Tuần này", Color(0xFFFFDAD6), Color(0xFFBA1A1A), Modifier.weight(1f))
        StatisticCard("home_done.svg", Modifier.size(15.dp), "Đã xong", tasksDone.toString(), "Tổng cộng", Color(0xFFE1E0FF), HomeSecondaryInk, Modifier.weight(1f))
    }
}

@Composable
private fun StatisticCard(
    icon: String,
    iconSize: Modifier,
    label: String,
    value: String,
    caption: String,
    iconBackground: Color,
    captionColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.widthIn(min = 100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(color = iconBackground, shape = RoundedCornerShape(8.dp)) {
                    Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        HomeIcon(icon, iconSize, "")
                    }
                }
                Text(label, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = HomeSecondaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(value, fontSize = 24.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = HomeInk)
                Text(caption, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = captionColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun TodayScheduleSection(classes: List<StudyClass>, onSeeAll: () -> Unit, onCourseClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Lịch hôm nay", fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.45).sp, fontWeight = FontWeight.SemiBold, color = HomeInk)
                Surface(color = HomeDivider, shape = CircleShape) {
                    Text("${classes.size} tiết", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
                }
            }
            Spacer(Modifier.weight(1f))
            Surface(onClick = onSeeAll, color = Color.Transparent, shape = RoundedCornerShape(8.dp)) {
                Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Xem tất cả", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = HomeBrandBlue)
                    HomeIcon("home_chevron.svg", Modifier.width(4.933.dp).height(8.dp), "")
                }
            }
        }
        if (classes.isEmpty()) {
            Text("Không có lịch học nào.", modifier = Modifier.padding(16.dp), color = HomeSecondaryInk, fontSize = 12.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                classes.forEach { classItem -> StudyClassCard(classItem) { onCourseClick(classItem.title) } }
            }
        }
    }
}

private val statusBackgroundRed = Color(0xFFFFDAD6)

@Composable
private fun StudyClassCard(item: StudyClass, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(102.75.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        HomeIcon(item.timeIcon, Modifier.size(13.333.dp), "")
                        Text(item.time, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = item.timeColor)
                        Text("• ${item.session}", fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk, maxLines = 1)
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(color = item.statusColor, shape = CircleShape) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item.statusIcon?.let { HomeIcon(it, Modifier.width(10.5.dp).height(11.667.dp), "") }
                            if (item.status == "Đang diễn ra") Box(Modifier.size(6.dp).clip(CircleShape).background(HomeBrandBlue))
                            Text(item.status, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = item.statusTextColor, maxLines = 1)
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.title, fontSize = 18.sp, lineHeight = 24.75.sp, fontWeight = FontWeight.SemiBold, color = HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HomeIcon(item.placeIcon, Modifier.width(13.333.dp).height(10.667.dp), "")
                        Text(item.place, fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk, maxLines = 1)
                        Text("•", fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk)
                        Text("GV: ${item.teacher}", fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Box(Modifier.align(Alignment.CenterStart).fillMaxHeight().width(4.dp).background(item.leadingColor))
        }
    }
}

@Composable
private fun UpcomingDeadlinesSection(tasks: List<DeadlineTask>, onFilterClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Deadline sắp tới", fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.45).sp, fontWeight = FontWeight.SemiBold, color = HomeInk)
                Surface(color = Color(0xFFFFDAD6), shape = CircleShape) {
                    Text("${tasks.size}", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93000A))
                }
            }
            Spacer(Modifier.weight(1f))
            Surface(onClick = onFilterClick, color = Color(0xFFE8E6FC), shape = CircleShape) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HomeIcon("home_deadline.svg", Modifier.size(10.5.dp), "")
                    Text("Lọc", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tasks.forEach { task -> DeadlineCard(task) }
        }
    }
}

@Composable
private fun DeadlineCard(task: DeadlineTask) {
    Card(
        modifier = Modifier.width(288.dp).height(170.5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            val urgencyBackground = if (task.isUrgent) Color(0xFFFFDAD6) else Color(0xFFE8E6FC)
            val urgencyColor = if (task.isUrgent) Color(0xFFBA1A1A) else HomeSecondaryInk
            val timeColor = if (task.isUrgent) Color(0xFFBA1A1A) else HomeSecondaryInk
            val urgencyIcon = if (task.isUrgent) "home_alert.svg" else null

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFEFECFF), shape = CircleShape) {
                            Text(task.subject, modifier = Modifier.widthIn(max = 130.dp).padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = HomeBrandBlue, maxLines = 1, overflow = TextOverflow.Clip)
                        }
                        Spacer(Modifier.weight(1f))

                        Surface(color = urgencyBackground, shape = CircleShape) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                urgencyIcon?.let { HomeIcon(it, Modifier.width(8.667.dp).height(10.292.dp), "") }
                                Text(task.urgency, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = urgencyColor, maxLines = 1)
                            }
                        }
                    }
                    Text(task.title, modifier = Modifier.height(38.5.dp), fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.SemiBold, color = HomeInk, maxLines = 2, overflow = TextOverflow.Clip)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HomeIcon("home_date.svg", Modifier.width(14.2.dp).height(13.1.dp), "")
                        Text(task.dueDate, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = timeColor, maxLines = 1)
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Tiến độ thực hiện", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
                        Spacer(Modifier.weight(1f))
                        Text(task.progressLabel, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeInk)
                    }
                    Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(HomeDivider)) {
                        if (task.progress > 0f) Box(Modifier.fillMaxWidth(task.progress).fillMaxHeight().clip(CircleShape).background(task.accent))
                    }
                }
            }
            Box(Modifier.align(Alignment.CenterStart).fillMaxHeight().width(4.dp).background(task.accent))
        }
    }
}

@Composable
private fun FocusSessionCard(onStart: () -> Unit, pomodoroState: com.example.apphotrohoctap.ui.pomodoro.PomodoroUiState?) {
    Card(
        modifier = Modifier.fillMaxWidth().height(132.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = Color(0xFFE1E0FF), shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp) {
                    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                        HomeIcon("home_timer.svg", Modifier.width(18.dp).height(21.dp), "")
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Góc tập trung Pomodoro", fontSize = 18.sp, lineHeight = 22.5.sp, fontWeight = FontWeight.SemiBold, color = HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    
                    val linkedTask = pomodoroState?.linkedTaskTitle
                    val subtitle = if (linkedTask != null) {
                        "Đang tập trung: $linkedTask"
                    } else {
                        "25 phút giải quyết dứt điểm bài tập"
                    }
                    Text(subtitle, fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = Color(0xFFE8E6FC), shape = CircleShape) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            HomeIcon("home_headphones.svg", Modifier.size(10.5.dp), "")
                            Text("Âm thanh lo-fi", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
                        }
                    }
                    Text("• ${pomodoroState?.completedFocusSessions ?: 0} chu kỳ", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
                }
                Spacer(Modifier.weight(1f))
                
                val isRunning = pomodoroState?.status == com.example.apphotrohoctap.ui.pomodoro.PomodoroStatus.RUNNING
                
                Surface(
                    onClick = onStart,
                    shape = CircleShape,
                    color = if (isRunning) Color(0xFFE1E0FF) else HomeBrandBlue,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.height(40.dp).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isRunning) {
                            val remSecs = pomodoroState.remainingMillis / 1000
                            val m = remSecs / 60
                            val s = remSecs % 60
                            val timeStr = String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s)
                            Text("Đang chạy • $timeStr", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = HomeBrandBlue)
                        } else {
                            HomeIcon("home_play.svg", Modifier.width(8.25.dp).height(10.5.dp), "")
                            Text("Bắt đầu ngay", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}





@Composable
private fun HomeIcon(assetName: String, modifier: Modifier, contentDescription: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/studyflow/$assetName")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowHomeScreenPreview() {
    MaterialTheme { StudyFlowHomeScreen() }
}
