package com.example.apphotrohoctap.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeeklyAnalyticsCard(
    reportData: WeeklyReportData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Thống Kê Tuần Này 📊",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2A)
                    )
                    Text(
                        text = "Thời gian tập trung & tiến độ bài tập",
                        fontSize = 12.sp,
                        color = Color(0xFF767586)
                    )
                }

                Surface(
                    color = Color(0xFFE1E0FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${reportData.totalFocusMinutes} phút",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4143D5)
                    )
                }
            }

            // Canvas Bar Chart
            val maxMinutes = (reportData.dailyFocusList.maxOfOrNull { it.focusMinutes } ?: 60).coerceAtLeast(60)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                ) {
                    val barWidth = 14.dp.toPx()
                    val totalBars = reportData.dailyFocusList.size
                    val spacing = (size.width - (barWidth * totalBars)) / (totalBars + 1)

                    reportData.dailyFocusList.forEachIndexed { index, item ->
                        val x = spacing + index * (barWidth + spacing)
                        val heightFraction = item.focusMinutes.toFloat() / maxMinutes
                        val barHeight = (size.height * heightFraction).coerceAtLeast(8.dp.toPx())
                        val y = size.height - barHeight

                        // Background Track
                        drawRoundRect(
                            color = Color(0xFFF5F2FF),
                            topLeft = Offset(x, 0f),
                            size = Size(barWidth, size.height),
                            cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                        )

                        // Active Fill Bar
                        drawRoundRect(
                            color = if (item.focusMinutes > 0) Color(0xFF4143D5) else Color(0xFFC6C5D7),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                        )
                    }
                }

                // Days Labels Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    reportData.dailyFocusList.forEach { item ->
                        Text(
                            text = item.dayLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF767586),
                            modifier = Modifier.width(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Summary Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F2FF), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nhiệm vụ hoàn thành", fontSize = 11.sp, color = Color(0xFF767586))
                    Text(
                        "${reportData.completedTasksCount}/${reportData.totalTasksCount}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFE3E0F7))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tổng thời gian học", fontSize = 11.sp, color = Color(0xFF767586))
                    val hours = reportData.totalFocusMinutes / 60
                    val mins = reportData.totalFocusMinutes % 60
                    Text(
                        if (hours > 0) "${hours}h ${mins}p" else "${mins} phút",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4143D5)
                    )
                }
            }
        }
    }
}
