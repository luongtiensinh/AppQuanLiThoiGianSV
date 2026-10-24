package com.example.apphotrohoctap.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.R
import kotlinx.coroutines.delay

private val FocusBackground = Color(0xFFFCF8FF)
private val FocusInk = Color(0xFF1A1A2A)
private val FocusText = Color(0xFF464555)
private val FocusMuted = Color(0xFF767586)
private val FocusBrand = Color(0xFF5B5FEF)
private val FocusPale = Color(0xFFEFECFF)
private enum class FocusMode(val title: String, val durationLabel: String, val durationSeconds: Int) {
    FOCUS("Tập trung", "25p", 25 * 60),
    SHORT_BREAK("Nghỉ ngắn", "5p", 5 * 60),
    LONG_BREAK("Nghỉ dài", "15p", 15 * 60)
}

@Composable
fun StudyFlowFocusScreen(onClose: () -> Unit = {}) {
    var mode by remember { mutableStateOf(FocusMode.FOCUS) }
    var remainingSeconds by remember { mutableIntStateOf(FocusMode.FOCUS.durationSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var showFocusCompletedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1_000L)
            remainingSeconds -= 1
        } else if (remainingSeconds == 0) {
            isRunning = false
            if (mode == FocusMode.FOCUS) showFocusCompletedDialog = true
        }
    }

    val timerLabel = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)
    val screenModifier = Modifier.fillMaxSize()
        .background(FocusBackground)
        .then(if (showFocusCompletedDialog) Modifier.blur(2.dp).alpha(.45f) else Modifier)

    Column(screenModifier) {
        FocusHeader(onClose)
        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            FocusTaskSelector()
            Text(
                "Chạm để đổi nhiệm vụ hoặc chọn Không liên kết",
                Modifier.padding(top = 6.dp),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = FocusMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))
            FocusCircularTimer(timerLabel)

            Spacer(Modifier.height(24.dp))
            FocusCycleIndicator()

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                FocusMode.entries.forEach { item ->
                    FocusModeChip(item, selected = item == mode) {
                        showFocusCompletedDialog = false
                        mode = item
                        remainingSeconds = item.durationSeconds
                        isRunning = false
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
            FocusControlDeck(
                isRunning = isRunning,
                onReset = {
                    showFocusCompletedDialog = false
                    remainingSeconds = mode.durationSeconds
                    isRunning = false
                },
                onToggleRunning = {
                    if (remainingSeconds == 0) remainingSeconds = mode.durationSeconds
                    isRunning = !isRunning
                },
                onSkip = {
                    showFocusCompletedDialog = false
                    mode = if (mode == FocusMode.FOCUS) FocusMode.SHORT_BREAK else FocusMode.FOCUS
                    remainingSeconds = mode.durationSeconds
                    isRunning = false
                }
            )
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showFocusCompletedDialog) {
        FocusCompletedDialog(
            onStartBreak = {
                showFocusCompletedDialog = false
                mode = FocusMode.SHORT_BREAK
                remainingSeconds = FocusMode.SHORT_BREAK.durationSeconds
                isRunning = true
            },
            onContinueStudying = { showFocusCompletedDialog = false }
        )
    }
}

@Composable
private fun FocusHeader(onClose: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().height(64.dp)
            .background(Color(0xCCFCF8FF)).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(onClick = onClose, modifier = Modifier.size(44.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) { FocusIcon("focus_header_close.svg", Modifier.size(14.dp), "Đóng") }
            }
            Image(
                painter = painterResource(R.drawable.studyflow_logo),
                contentDescription = "StudyFlow",
                modifier = Modifier.size(20.dp)
            )
            Text("Góc Tập Trung", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = FocusInk, maxLines = 1)
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/studyflow/home_profile.png").build(),
            contentDescription = "Hồ sơ cá nhân",
            modifier = Modifier.size(32.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun FocusTaskSelector() {
    Surface(
        modifier = Modifier.widthIn(max = 358.dp),
        shape = CircleShape,
        color = FocusPale,
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FocusIcon("focus_task.svg", Modifier.size(width = 18.333.dp, height = 13.333.dp), "")
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = FocusText)) { append("Đang làm: ") }
                        withStyle(SpanStyle(color = FocusInk, fontWeight = FontWeight.SemiBold)) {
                            append("Nộp bài tập lớn Android")
                        }
                    },
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FocusIcon("focus_task_dropdown.svg", Modifier.size(width = 7.5.dp, height = 3.75.dp), "Đổi nhiệm vụ")
        }
    }
}

@Composable
private fun FocusCircularTimer(timerLabel: String) {
    Box(Modifier.size(240.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.padding(16.dp).fillMaxSize().blur(40.dp).background(Color(0x4DE1E0FF), CircleShape))
        FocusIcon("focus_timer_track.svg", Modifier.size(216.dp), "")
        FocusIcon("focus_timer_progress_arc.svg", Modifier.size(216.dp), "")
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                timerLabel,
                fontSize = 52.sp,
                lineHeight = 52.sp,
                letterSpacing = (-1.3).sp,
                fontWeight = FontWeight.Bold,
                color = FocusInk
            )
            Spacer(Modifier.height(12.dp))
            Surface(shape = CircleShape, color = Color(0xFFF5F2FF), shadowElevation = 1.dp) {
                Row(
                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                    Text(
                        "Đang tập trung",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        letterSpacing = .25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FocusText
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusCycleIndicator() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(2) { Box(Modifier.size(10.dp).background(FocusBrand, CircleShape)) }
            Box(Modifier.size(10.dp).border(2.dp, Color(0xFFE1E0FF), CircleShape).background(FocusBrand, CircleShape))
            Box(Modifier.size(10.dp).background(Color(0xFFDAD8EE), CircleShape))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Chu kỳ: Phiên 3/4", fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = FocusInk)
            Text("•", fontSize = 12.sp, lineHeight = 16.sp, color = FocusMuted)
            Text("Còn 1 phiên đến nghỉ dài", fontSize = 12.sp, lineHeight = 16.sp, color = FocusText)
        }
    }
}

@Composable
private fun FocusModeChip(mode: FocusMode, selected: Boolean, onClick: () -> Unit) {
    val minWidth = when (mode) {
        FocusMode.FOCUS -> 122.dp
        FocusMode.SHORT_BREAK -> 95.dp
        FocusMode.LONG_BREAK -> 84.dp
    }
    Surface(
        onClick = onClick,
        modifier = Modifier.widthIn(min = minWidth).height(32.dp),
        color = if (selected) FocusBrand else FocusPale,
        shape = CircleShape,
        shadowElevation = if (selected) 1.dp else 0.dp
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mode == FocusMode.FOCUS) {
                FocusIcon("focus_mode_clock.svg", Modifier.size(13.dp), "")
                Spacer(Modifier.width(6.dp))
            }
            Text(mode.title + "\n" + mode.durationLabel, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else FocusText, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FocusControlDeck(isRunning: Boolean, onReset: () -> Unit, onToggleRunning: () -> Unit, onSkip: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        FocusControl("focus_control_reset.svg", "Đặt lại", Modifier.size(width = 18.dp, height = 21.dp), onReset)
        Spacer(Modifier.width(32.dp))
        Surface(
            onClick = onToggleRunning,
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = FocusBrand,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                FocusIcon(
                    if (isRunning) "focus_pause.svg" else "focus_control_play.svg",
                    Modifier.size(width = 16.5.dp, height = 21.dp),
                    if (isRunning) "Tạm dừng" else "Bắt đầu"
                )
            }
        }
        Spacer(Modifier.width(32.dp))
        FocusControl("focus_control_skip.svg", "Bỏ qua", Modifier.size(width = 13.dp, height = 12.dp), onSkip)
    }
}

@Composable
private fun FocusControl(icon: String, label: String, iconSize: Modifier, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(onClick = onClick, modifier = Modifier.size(52.dp), shape = CircleShape, color = Color(0xFFF5F2FF), shadowElevation = 1.dp) {
            Box(contentAlignment = Alignment.Center) { FocusIcon(icon, iconSize, label) }
        }
        Text(label, fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, color = FocusText)
    }
}
@Composable
private fun FocusCompletedDialog(onStartBreak: () -> Unit, onContinueStudying: () -> Unit) {
    Dialog(
        onDismissRequest = onContinueStudying,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.size(84.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.fillMaxSize().background(Color(0xFFFFF3D6), CircleShape))
                    Box(Modifier.size(62.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        FocusIcon("focus_celebration.svg", Modifier.size(36.dp), "Hoàn thành phiên tập trung")
                    }
                    FocusIcon("focus_celebration_star.svg", Modifier.align(Alignment.TopEnd).size(22.dp), "")
                    FocusIcon("focus_celebration_bell.svg", Modifier.align(Alignment.BottomStart).size(21.dp), "")
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Hết giờ tập trung!",
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = FocusInk,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    buildAnnotatedString {
                        append("Bạn đã hoàn thành xuất sắc ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = FocusInk)) { append("1 phiên học tập (25 phút)") }
                        append(" cho nhiệm vụ ")
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = FocusInk)) { append("‘Nộp bài tập lớn Android’.") }
                    },
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = FocusText,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Hãy nghỉ 5 phút để nạp lại năng lượng nhé?",
                    Modifier.padding(top = 6.dp),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = FocusMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FocusStatCard(
                        icon = "focus_deep_time.svg",
                        title = "Thời gian",
                        value = "+25 phút sâu",
                        modifier = Modifier.weight(1f)
                    )
                    FocusStatCard(
                        icon = "focus_cycle.svg",
                        title = "Chu kỳ",
                        value = "3/4 hoàn tất",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = onStartBreak,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FocusBrand)
                ) {
                    FocusIcon("focus_coffee.svg", Modifier.size(16.dp), "")
                    Spacer(Modifier.width(9.dp))
                    Text("Bắt đầu nghỉ (5 phút)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                TextButton(onClick = onContinueStudying, modifier = Modifier.padding(top = 2.dp)) {
                    Text("Để sau, tiếp tục học", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FocusText)
                }
            }
        }
    }
}

@Composable
private fun FocusStatCard(icon: String, title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF7F5FF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAE7FA))
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FocusIcon(icon, Modifier.size(18.dp), "")
            Column {
                Text(title, fontSize = 11.sp, lineHeight = 14.sp, color = FocusMuted)
                Text(value, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = FocusInk, maxLines = 1)
            }
        }
    }
}

@Composable
private fun FocusIcon(assetName: String, modifier: Modifier, contentDescription: String, alpha: Float = 1f) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/studyflow/$assetName")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = contentDescription.takeIf(String::isNotBlank),
        modifier = modifier,
        alpha = alpha,
        contentScale = ContentScale.Fit
    )
}


