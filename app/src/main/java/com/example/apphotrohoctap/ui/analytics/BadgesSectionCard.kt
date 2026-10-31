package com.example.apphotrohoctap.ui.analytics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BadgesSectionCard(
    badges: List<BadgeItem>,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Huy Hiệu & Thành Tựu 🏆",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2A)
                )
                val unlockedCount = GamificationEngine.getUnlockedCount(badges)
                Text(
                    text = "$unlockedCount/${badges.size} Đã đạt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4143D5)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                badges.forEach { badge ->
                    BadgeChipItem(badge)
                }
            }
        }
    }
}

@Composable
private fun BadgeChipItem(badge: BadgeItem) {
    val bgColor = if (badge.isUnlocked) Color(0xFFF5F2FF) else Color(0xFFF5F5F7)
    val borderColor = if (badge.isUnlocked) Color(0xFF4143D5) else Color(0xFFE3E0F7)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .width(140.dp)
            .alpha(if (badge.isUnlocked) 1.0f else 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = if (badge.isUnlocked) Color(0xFFE1E0FF) else Color(0xFFE3E0F7),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(badge.iconEmoji, fontSize = 22.sp)
                }
            }

            Text(
                badge.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (badge.isUnlocked) Color(0xFF1A1A2A) else Color(0xFF767586),
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                badge.description,
                fontSize = 10.sp,
                color = Color(0xFF767586),
                maxLines = 2,
                lineHeight = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.height(26.dp)
            )

            Surface(
                color = if (badge.isUnlocked) Color(0xFF16A34A) else Color(0xFFC6C5D7),
                shape = CircleShape
            ) {
                Text(
                    badge.progressText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
