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
