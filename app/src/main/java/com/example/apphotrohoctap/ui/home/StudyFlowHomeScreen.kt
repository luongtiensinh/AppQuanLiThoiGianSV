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

private val HomeBackground = Color(0xFFFCF8FF)
private val HomeBrandBlue = Color(0xFF4143D5)
private val HomeBrightBlue = Color(0xFF5B5FEF)
private val HomeInk = Color(0xFF1A1A2A)
private val HomeSecondaryInk = Color(0xFF464555)
private val HomeMutedInk = Color(0xFF767586)
private val HomeDivider = Color(0xFFE3E0F7)
private val HomeCardShadow = Color(0x105B5FEF)

private data class StudyClass(
    val time: String,
    val session: String,
    val status: String,
    val title: String,
    val place: String,
    val teacher: String,
    val leadingColor: Color,
    val timeColor: Color = HomeSecondaryInk,
    val statusColor: Color = Color(0xFFE8E6FC),
    val statusTextColor: Color = HomeSecondaryInk,
    val statusIcon: String? = null,
    val placeIcon: String = "home_location.svg",
    val timeIcon: String = "home_clock.svg"
)

private data class DeadlineTask(
    val subject: String,
    val urgency: String,
    val title: String,
    val dueDate: String,
    val progress: Float,
    val progressLabel: String,
    val accent: Color,
    val urgencyBackground: Color,
    val urgencyColor: Color,
    val timeColor: Color,
    val urgencyIcon: String? = null,
    val dateIcon: String = "home_date.svg"
)

@Composable
fun StudyFlowHomeScreen(
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSeeAllClasses: () -> Unit = {},
    onFilterDeadlines: () -> Unit = {},
    onStartFocus: () -> Unit = {},
    onAddTask: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(onNotificationClick, onProfileClick)
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
                    GoalProgressCard()
                    StatisticsRow()
                    TodayScheduleSection(onSeeAllClasses)
                    UpcomingDeadlinesSection(onFilterDeadlines)
                    FocusSessionCard(onStartFocus)
                }
            }
            HomeBottomNavigation(onTabSelected)
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
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/studyflow/home_profile.png")
                                .build(),
                            contentDescription = "Hồ sơ cá nhân",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GreetingSection() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Chào Minh Anh",
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
                    Text("Học kỳ 2", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeBrandBlue)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HomeIcon("home_calendar.svg", Modifier.width(12.dp).height(13.333.dp), "")
            Text("Thứ Tư, 24 Tháng 4", fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk)
            Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFC6C5D7)))
            Text("Tuần học thứ 8", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = HomeBrandBlue)
        }
    }
}

@Composable
private fun GoalProgressCard() {
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
                    "78%",
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
                Text("14 / 18 hoàn thành", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
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
                        .fillMaxWidth(0.78f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HomeIcon("home_sparkle.svg", Modifier.size(14.667.dp), "")
            Text(
                "Cố lên! Bạn chỉ còn 4 bài tập nữa là hoàn thành tuần 8.",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color(0xFFF5F2FF)
            )
        }
    }
}

@Composable
private fun StatisticsRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        StatisticCard("home_tasks.svg", Modifier.width(15.dp).height(11.306.dp), "Hôm nay", "5", "2 ưu tiên", Color(0xFFE1E0FF), Color(0xFFBA1A1A), Modifier.weight(1f))
        StatisticCard("home_alarm.svg", Modifier.width(15.975.dp).height(14.738.dp), "Hạn nộp", "3", "Gần: 23:59", Color(0xFFFFDAD6), Color(0xFFBA1A1A), Modifier.weight(1f))
        StatisticCard("home_done.svg", Modifier.size(15.dp), "Đã xong", "12", "+4 tuần này", Color(0xFFE1E0FF), HomeSecondaryInk, Modifier.weight(1f))
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
        modifier = modifier.height(107.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(color = iconBackground, shape = RoundedCornerShape(8.dp)) {
                        Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                            HomeIcon(icon, iconSize, "")
                        }
                    }
                    Text(label, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, color = HomeSecondaryInk, maxLines = 1)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(value, fontSize = 24.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, color = HomeInk)
                Text(caption, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = captionColor, maxLines = 1, overflow = TextOverflow.Clip)
            }
        }
    }
}

@Composable
private fun TodayScheduleSection(onSeeAll: () -> Unit) {
    val classes = listOf(
        StudyClass("07:30 - 09:50", "Ca sáng", "Đang diễn ra", "Cơ sở dữ liệu nâng cao (IT3020)", "Phòng B1-302", "TS. Nguyễn Thanh Hải", Color(0xFF5E62F4), HomeBrandBlue, Color(0xFFE1E0FF), Color(0xFF2B2BC5)),
        StudyClass("13:00 - 15:20", "Ca chiều", "Có bài nộp", "Lập trình Ứng dụng Di động", "Lab 405", "ThS. Lê Hoàng Long", Color(0xFF555962), statusBackgroundRed, Color(0xFFFFDAD6), Color(0xFF93000A), "home_homework.svg", "home_classroom.svg", "home_laptop.svg"),
        StudyClass("18:00 - 19:30", "Trực tuyến", "Sắp tới", "Tiếng Anh chuyên ngành CNTT", "MS Teams", "Cô Sarah Evans", Color(0xFFC6C5D7), placeIcon = "home_class_link.svg", timeIcon = "home_laptop.svg")
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Lịch hôm nay", fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.45).sp, fontWeight = FontWeight.SemiBold, color = HomeInk)
                Surface(color = HomeDivider, shape = CircleShape) {
                    Text("3 tiết", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
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
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            classes.forEach { classItem -> StudyClassCard(classItem) }
        }
    }
}

private val statusBackgroundRed = Color(0xFFFFDAD6)

@Composable
private fun StudyClassCard(item: StudyClass) {
    Card(
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
private fun UpcomingDeadlinesSection(onFilterClick: () -> Unit) {
    val tasks = listOf(
        DeadlineTask("CSDL Nâng cao", "Còn 6 giờ", "Báo cáo Đồ án Bản Ký: Thiết kế schema & Indexing", "Hôm nay, 23:59", 0.65f, "65%", Color(0xFFBA1A1A), Color(0xFFFFDAD6), Color(0xFFBA1A1A), Color(0xFFBA1A1A), "home_alert.svg", "home_date.svg"),
        DeadlineTask("Mobile Dev • CS304", "Còn 1 ngày", "Thiết kế UI Mockup ứng dụng Jetpack Compose", "Ngày mai, 17:00", 0.30f, "30%", Color(0xFF555962), Color(0xFFE3E0F7), Color(0xFF555962), HomeSecondaryInk, "home_document.svg", "home_calendar.svg"),
        DeadlineTask("Toán Rời Rạc • MA102", "Chưa bắt đầu", "Bài tập tuần 8: Ứng dụng bài toán đồ thị Euler", "Chủ Nhật, 28/04", 0f, "0%", HomeBrandBlue, Color(0xFFE8E6FC), HomeSecondaryInk, HomeSecondaryInk, dateIcon = "home_calendar_alt.svg")
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Deadline sắp tới", fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.45).sp, fontWeight = FontWeight.SemiBold, color = HomeInk)
                Surface(color = Color(0xFFFFDAD6), shape = CircleShape) {
                    Text("3", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93000A))
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
                        Surface(color = task.urgencyBackground, shape = CircleShape) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                task.urgencyIcon?.let { HomeIcon(it, Modifier.width(8.667.dp).height(10.292.dp), "") }
                                Text(task.urgency, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = task.urgencyColor, maxLines = 1)
                            }
                        }
                    }
                    Text(task.title, modifier = Modifier.height(38.5.dp), fontSize = 14.sp, lineHeight = 19.25.sp, fontWeight = FontWeight.SemiBold, color = HomeInk, maxLines = 2, overflow = TextOverflow.Clip)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HomeIcon(task.dateIcon, Modifier.width(14.2.dp).height(13.1.dp), "")
                        Text(task.dueDate, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = task.timeColor, maxLines = 1)
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
private fun FocusSessionCard(onStart: () -> Unit) {
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
                    Text("25 phút giải quyết dứt điểm bài tập IT3020", fontSize = 12.sp, lineHeight = 16.sp, color = HomeSecondaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                    Text("• 4 chu kỳ", fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = HomeSecondaryInk)
                }
                Spacer(Modifier.weight(1f))
                Surface(
                    onClick = onStart,
                    shape = CircleShape,
                    color = HomeBrandBlue,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.height(40.dp).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HomeIcon("home_play.svg", Modifier.width(8.25.dp).height(10.5.dp), "")
                        Text("Bắt đầu ngay", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

private data class HomeTab(val label: String, val icon: String)

private val homeTabs = listOf(
    HomeTab("Trang chủ", "home_nav_home.svg"),
    HomeTab("Lịch biểu", "home_nav_schedule.svg"),
    HomeTab("Nhiệm vụ", "home_nav_tasks.svg"),
    HomeTab("Ghi chú", "home_nav_notes.svg"),
    HomeTab("Cài đặt", "home_nav_settings.svg")
)

@Composable
private fun HomeBottomNavigation(onTabSelected: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        color = HomeBackground.copy(alpha = 0.96f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            homeTabs.forEachIndexed { index, tab ->
                val selected = index == 0
                Column(
                    modifier = Modifier.weight(1f).height(56.dp).clip(RoundedCornerShape(14.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        onClick = { onTabSelected(tab.label) },
                        modifier = Modifier.width(56.dp).height(32.dp),
                        color = if (selected) Color(0xFFEDE9FE) else Color.Transparent,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            HomeIcon(tab.icon, homeTabIconSize(index), "")
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        tab.label,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selected) Color(0xFF2B2BC5) else HomeSecondaryInk,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun homeTabIconSize(index: Int): Modifier = when (index) {
    0 -> Modifier.width(14.667.dp).height(16.5.dp)
    1 -> Modifier.width(16.5.dp).height(18.333.dp)
    2 -> Modifier.size(18.333.dp)
    3 -> Modifier.size(18.333.dp)
    else -> Modifier.width(18.425.dp).height(18.333.dp)
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
