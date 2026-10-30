package com.example.apphotrohoctap.ui.grades

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudyFlowGradesScreen(
    onBack: () -> Unit = {},
    onAddSubject: () -> Unit = {},
    onSubjectClick: (Long) -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: GradeViewModel = viewModel(factory = GradeViewModelFactory(context))
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    // Tính toán GPA tổng quát
    val subjectPairs = subjects.filter { it.isCompleted }.map { it.credits to it.currentAccumulatedScore }
    val (totalCredits, overallGpa) = GradeCalculator.calculateOverallGpa(subjectPairs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCF8FF))
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(44.dp),
                color = Color.Transparent,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("‹", fontSize = 30.sp, color = Color(0xFF1A1A2A))
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "Điểm số & Mục tiêu",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2A)
            )
        }

        // Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color(0xFF4143D5), // Brand Blue
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("GPA Tích lũy (Hệ 4.0)", fontSize = 12.sp, color = Color(0xCCFFFFFF))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        String.format(java.util.Locale.US, "%.2f", overallGpa),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Tín chỉ tích lũy", fontSize = 12.sp, color = Color(0xCCFFFFFF))
                    Spacer(Modifier.height(4.dp))
                    Text("$totalCredits", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Section Title & Add Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Danh sách Môn học", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A))
            Surface(
                onClick = onAddSubject,
                color = Color(0xFFE1E0FF), // Pale Blue
                shape = CircleShape
            ) {
                Text(
                    "＋ Thêm môn",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4143D5)
                )
            }
        }

        // Subject List
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (subjects.isEmpty()) {
                item {
                    Text(
                        "Bạn chưa thêm môn học nào. Hãy thêm để theo dõi điểm số nhé!",
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color(0xFF767586)
                    )
                }
            }
            items(subjects, key = { it.subjectId }) { subject ->
                SubjectGradeCard(subject = subject, onClick = { onSubjectClick(subject.subjectId) })
            }
        }
        
        com.example.apphotrohoctap.ui.navigation.AppBottomNavigation(
            currentTab = com.example.apphotrohoctap.ui.navigation.BottomNavTab.GRADES,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
private fun SubjectGradeCard(subject: SubjectGradeUiModel, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Biểu tượng / Initials
            val initials = subject.subjectName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
            Surface(
                color = Color(0xFFF5F2FF),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(initials, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4143D5))
                }
            }

            Spacer(Modifier.width(12.dp))

            // Nội dung chính
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    subject.subjectName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2A),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${subject.credits} TC", fontSize = 12.sp, color = Color(0xFF767586))
                    Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFE3E0F7)))
                    Text("Mục tiêu: ${subject.targetGrade}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4143D5))
                }
                
                Spacer(Modifier.height(6.dp))
                
                // Trạng thái dự đoán
                if (subject.isCompleted) {
                    Text("Điểm tổng kết: ${String.format(java.util.Locale.US, "%.1f", subject.currentAccumulatedScore)} (${subject.finalLetterGrade})", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                } else if (subject.requiredScoreToTarget != null) {
                    Text("Cần ${String.format(java.util.Locale.US, "%.1f", subject.requiredScoreToTarget)} điểm để đạt ${subject.targetGrade}", fontSize = 12.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Medium)
                } else {
                    Text("Mục tiêu bất khả thi!", fontSize = 12.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}


