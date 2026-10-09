package com.example.apphotrohoctap.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

private val SettingsBg = Color(0xFFFCF8FF)
private val SettingsInk = Color(0xFF1A1A2A)
private val SettingsText = Color(0xFF464555)
private val SettingsMuted = Color(0xFF767586)
private val SettingsBrand = Color(0xFF4143D5)
private val SettingsPale = Color(0xFFE8E6FC)
private val SettingsCardShadow = Color(0x0F5B5FEF)

@Composable
fun StudyFlowSettingsScreen(onTabSelected: (String) -> Unit = {}) {
    var deadlineReminder by remember { mutableStateOf(true) }
    var classReminder by remember { mutableStateOf(true) }
    var weeklySummary by remember { mutableStateOf(false) }
    var darkMode by remember { mutableStateOf(false) }
    var selectedAccent by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(SettingsBg)) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .statusBarsPadding().padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SettingsSectionHeader("TÀI KHOẢN", "Đã xác minh SV")
            SettingsCard {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box {
                        Surface(color = SettingsPale, shape = CircleShape) {
                            Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) { Text("ML", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SettingsBrand) }
                        }
                        Box(Modifier.align(Alignment.BottomEnd).padding(1.dp).size(13.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nguyễn Mai Linh", fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, color = SettingsInk)
                            Text("✦", fontSize = 12.sp, color = SettingsBrand)
                        }
                        Text("K66 - Khoa Công nghệ Thông tin", fontSize = 10.sp, lineHeight = 14.sp, color = SettingsText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("linh.nm21@sis.hust.edu.vn", fontSize = 10.sp, lineHeight = 14.sp, color = SettingsMuted)
                    }
                    SettingsPill("Chỉnh sửa", SettingsPale, SettingsBrand)
                }
                SettingsDivider()
                SettingsNavigationRow("▣", "Học kỳ & Khóa học", "6 học phần đang kích hoạt", "HK II (2023 - 2024)")
                SettingsDivider()
                SettingsNavigationRow("♧", "Bảo mật & Mật khẩu", null, "Bảo vệ 2 lớp", trailingColor = Color(0xFF10A37F))
            }

            Spacer(Modifier.height(22.dp))
            SettingsSectionHeader("THÔNG BÁO", "Âm thanh & Rung")
            SettingsCard {
                SettingsToggleRow("♧", "Nhắc nhở Deadline & Hạn chót", "Thông báo đẩy khi sắp đến hạn…", deadlineReminder) { deadlineReminder = it }
                SettingsDivider()
                SettingsToggleRow("▣", "Nhắc nhở Buổi học", "Báo trước giờ vào lớp & số phòng…", classReminder) { classReminder = it }
                SettingsDivider()
                SettingsNavigationRow("⌛", "Báo trước thời gian", "Áp dụng cho tất cả bài học", "Trước 60 phút")
                SettingsDivider()
                SettingsToggleRow("▥", "Tổng kết tiến độ tuần", "Gửi báo cáo học tập vào tối Chủ…", weeklySummary) { weeklySummary = it }
            }

            Spacer(Modifier.height(22.dp))
            SettingsSectionHeader("GIAO DIỆN & HIỂN THỊ", "Academic Rhythm")
            SettingsCard {
                SettingsToggleRow("☾", "Giao diện Tối (Dark Mode)", "Nền xanh navy trầm, dịu mắt ban…", darkMode) { darkMode = it }
                SettingsDivider()
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsIconTile("⊙")
                        Column(Modifier.weight(1f)) {
                            Text("Màu nhấn ứng dụng (Accent Color)", fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium, color = SettingsInk)
                            Text("Màu tím Indigo StudyFlow thanh lịch", fontSize = 10.sp, lineHeight = 14.sp, color = SettingsMuted)
                        }
                    }
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(start = 52.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        listOf(SettingsBrand, Color(0xFF8B5CF6), Color(0xFF0284C7), Color(0xFFF43F5E), Color(0xFF10B981)).forEachIndexed { index, color ->
                            Surface(onClick = { selectedAccent = index }, modifier = Modifier.size(if (selectedAccent == index) 42.dp else 40.dp), color = color, shape = CircleShape) {
                                Box(contentAlignment = Alignment.Center) { if (selectedAccent == index) Text("✓", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                            }
                        }
                    }
                }
                SettingsDivider()
                SettingsNavigationRow("Tт", "Cỡ chữ nội dung", null, "Tiêu chuẩn (M)")
            }

            Spacer(Modifier.height(22.dp))
            SettingsSectionHeader("DỮ LIỆU & ĐỒNG BỘ", "● Trực tuyến", statusColor = Color(0xFF10B981))
            SettingsCard {
                SettingsNavigationRow("☁", "Đồng bộ đám mây", "Đã đồng bộ 2 phút trước", "Đã kết nối", trailingColor = Color(0xFF10A37F))
                SettingsDivider()
                SettingsNavigationRow("⇧", "Sao lưu & Xuất dữ liệu", "Xuất lịch học & bài tập ra file CSV/JSON", "›")
                SettingsDivider()
                SettingsNavigationRow("⟳", "Dung lượng bộ nhớ tạm", "34.8 MB chiếm dụng", "Xóa cache")
            }

            Spacer(Modifier.height(22.dp))
            SettingsSectionHeader("KHÁC")
            SettingsCard {
                SettingsNavigationRow("ⓘ", "Về StudyFlow", null, "v2.4.0 (Build 142)")
                SettingsDivider()
                SettingsNavigationRow("✉", "Góp ý & Báo lỗi", null, "›")
                SettingsDivider()
                SettingsNavigationRow("▤", "Điều khoản sử dụng & Quyền riêng tư", null, "›")
                SettingsDivider()
                SettingsNavigationRow("⇥", "Đăng xuất tài khoản", null, "›", danger = true)
            }
            Column(Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("♧  StudyFlow for Students", fontSize = 10.sp, color = SettingsMuted)
                Text("Thiết kế tối ưu cho sinh viên đại học", fontSize = 9.sp, color = SettingsMuted)
            }
        }
        SettingsBottomNavigation(onTabSelected)
    }
}

@Composable
private fun SettingsSectionHeader(title: String, trailing: String? = null, statusColor: Color = SettingsBrand) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, modifier = Modifier.weight(1f), fontSize = 9.sp, lineHeight = 14.sp, letterSpacing = .25.sp, fontWeight = FontWeight.SemiBold, color = SettingsMuted, maxLines = 1)
        trailing?.let { Text(it, fontSize = 9.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = statusColor, maxLines = 1) }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp), spotColor = SettingsCardShadow),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        content = content
    )
}

@Composable
private fun SettingsDivider() {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp).background(SettingsPale))
}

@Composable
private fun SettingsIconTile(icon: String, background: Color = SettingsPale, foreground: Color = SettingsBrand) {
    Surface(color = background, shape = RoundedCornerShape(12.dp)) {
        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { Text(icon, fontSize = 17.sp, color = foreground) }
    }
}

@Composable
private fun SettingsNavigationRow(icon: String, title: String, subtitle: String?, trailing: String, trailingColor: Color = SettingsBrand, danger: Boolean = false) {
    val color = if (danger) Color(0xFFBA1A1A) else SettingsInk
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsIconTile(icon, if (danger) Color(0xFFFFE9E7) else SettingsPale, if (danger) Color(0xFFBA1A1A) else SettingsBrand)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            subtitle?.let { Text(it, fontSize = 9.sp, lineHeight = 14.sp, color = SettingsMuted, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
        Text(trailing, fontSize = if (trailing.length > 14) 8.sp else 9.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = if (danger) Color(0xFFBA1A1A) else trailingColor, maxLines = 1)
        if (trailing == "›" || trailing == "HK II (2023 - 2024)" || trailing == "Tiêu chuẩn (M)") Text("›", fontSize = 14.sp, color = SettingsMuted)
    }
}

@Composable
private fun SettingsToggleRow(icon: String, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsIconTile(icon)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium, color = SettingsInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, fontSize = 9.sp, lineHeight = 14.sp, color = SettingsMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        SettingsSwitch(checked, onCheckedChange)
    }
}

@Composable
private fun SettingsSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(onClick = { onCheckedChange(!checked) }, modifier = Modifier.width(48.dp).height(24.dp), color = if (checked) SettingsBrand else Color(0xFFE3E0F7), shape = CircleShape) {
        Box(Modifier.fillMaxSize().padding(horizontal = 2.dp), contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart) {
            Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White))
        }
    }
}

@Composable
private fun SettingsPill(text: String, background: Color, foreground: Color) {
    Surface(color = background, shape = CircleShape) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 9.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = foreground)
    }
}

@Composable
private fun SettingsIcon(assetName: String, modifier: Modifier, contentDescription: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/studyflow/$assetName")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier
    )
}

@Composable
private fun SettingsBottomNavigation(onTabSelected: (String) -> Unit) {
    val items = listOf(
        "Trang chủ" to "home_nav_home.svg",
        "Lịch biểu" to "home_nav_schedule.svg",
        "Nhiệm vụ" to "home_nav_tasks.svg",
        "Ghi chú" to "home_nav_notes.svg",
        "Cài đặt" to "home_nav_settings.svg"
    )
    Surface(color = SettingsBg.copy(alpha = .96f), shadowElevation = 4.dp) {
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(80.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            items.forEachIndexed { index, (title, icon) ->
                val selected = title == "Cài đặt"
                Column(Modifier.weight(1f).height(56.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Surface(onClick = { if (!selected) onTabSelected(title) }, modifier = Modifier.width(56.dp).height(32.dp), color = if (selected) Color(0xFFE1E0FF) else Color.Transparent, shape = CircleShape) {
                        Box(contentAlignment = Alignment.Center) {
                            val iconModifier = when (index) {
                                4 -> Modifier.width(18.425.dp).height(18.333.dp)
                                else -> Modifier.size(18.333.dp)
                            }
                            SettingsIcon(icon, iconModifier, "")
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(title, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold, color = if (selected) SettingsBrand else SettingsText, maxLines = 1)
                }
            }
        }
    }
}
