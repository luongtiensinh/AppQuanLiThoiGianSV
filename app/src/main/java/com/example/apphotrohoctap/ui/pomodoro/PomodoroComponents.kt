package com.example.apphotrohoctap.ui.pomodoro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun PomodoroTimerArc(
    remainingMillis: Long,
    totalMillis: Long,
    mode: PomodoroMode,
    modifier: Modifier = Modifier
) {
    val progressColor = if (mode == PomodoroMode.FOCUS) Color(0xFF4143D5) else Color(0xFF16A34A)
    val trackColor = if (mode == PomodoroMode.FOCUS) Color(0xFFE3E0F7) else Color(0xFFDCFCE7)
    
    // Tính góc quét. Nếu time = total thì là 360 độ (full). 
    // Góc quét (sweepAngle) sẽ giảm dần về 0 khi remainingMillis về 0.
    val progressRatio = if (totalMillis > 0) remainingMillis.toFloat() / totalMillis else 0f
    val sweepAngle = progressRatio * 360f

    val totalSeconds = remainingMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.aspectRatio(1f)
    ) {
        // Giải thích việc dùng Canvas:
        // Cần vẽ 2 vòng cung đè lên nhau với độ dày (strokeWidth) tùy chỉnh và bo tròn 2 đầu (StrokeCap.Round).
        // Compose CircularProgressIndicator mặc định không hỗ trợ track background màu khác có cùng độ dày dễ dàng 
        // như Canvas. Việc tính sweepAngle thủ công cho phép kiểm soát 100% hướng xoay và điểm xuất phát (-90 độ là góc 12h).
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            
            // Vẽ track nền
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Vẽ cung tiến độ
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Text(
            text = timeString,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2A) // Ink color
        )
    }
}

@Composable
fun PomodoroControls(
    status: PomodoroStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onSkip: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(onClick = onReset, color = Color(0xFFF5F2FF), shape = CircleShape) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Text("↻", fontSize = 24.sp, color = Color(0xFF4143D5))
            }
        }
        
        Surface(
            onClick = {
                if (status == PomodoroStatus.RUNNING) onPause() else onStart()
            }, 
            color = Color(0xFF4143D5), 
            shape = CircleShape,
            shadowElevation = 4.dp
        ) {
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Text(
                    if (status == PomodoroStatus.RUNNING) "⏸" else "▶", 
                    fontSize = 32.sp, 
                    color = Color.White
                )
            }
        }
        
        Surface(onClick = onSkip, color = Color(0xFFF5F2FF), shape = CircleShape) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Text("⏭", fontSize = 24.sp, color = Color(0xFF4143D5))
            }
        }
    }
}

@Composable
fun ModeSelector(currentMode: PomodoroMode, onModeSelect: (PomodoroMode) -> Unit) {
    Surface(color = Color(0xFFF5F2FF), shape = RoundedCornerShape(24.dp)) {
        Row(Modifier.padding(4.dp)) {
            listOf(
                PomodoroMode.FOCUS to "Tập trung",
                PomodoroMode.SHORT_BREAK to "Nghỉ ngắn",
                PomodoroMode.LONG_BREAK to "Nghỉ dài"
            ).forEach { (mode, label) ->
                val isSelected = currentMode == mode
                Surface(
                    onClick = { onModeSelect(mode) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color.White else Color.Transparent,
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Text(
                        label,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color(0xFF4143D5) else Color(0xFF767586)
                    )
                }
            }
        }
    }
}

@Composable
fun CycleDots(completedCount: Int) {
    val currentCycle = completedCount % 4
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until 4) {
            val color = if (i < currentCycle) Color(0xFF4143D5) else Color(0xFFE3E0F7)
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        }
    }
}

@Composable
internal fun TaskSelector(
    linkedTaskTitle: String?,
    incompleteTasks: List<com.example.apphotrohoctap.ui.tasks.TaskItem>,
    onTaskSelect: (String?) -> Unit
) {
    val expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    Box {
        Surface(
            onClick = { expanded.value = true },
            color = Color(0xFFF5F2FF),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = linkedTaskTitle ?: "Không liên kết với bài tập nào",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (linkedTaskTitle != null) Color(0xFF4143D5) else Color(0xFF767586),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("⌄", fontSize = 14.sp, color = Color(0xFF767586))
            }
        }
        
        androidx.compose.material3.DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text("Không liên kết") },
                onClick = { 
                    onTaskSelect(null)
                    expanded.value = false
                }
            )
            incompleteTasks.forEach { task ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(task.title, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
                    onClick = { 
                        onTaskSelect(task.title)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
