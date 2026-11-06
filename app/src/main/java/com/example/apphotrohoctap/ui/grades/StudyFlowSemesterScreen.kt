package com.example.apphotrohoctap.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudyFlowSemesterScreen(
    onNavigateToGrades: (Long) -> Unit, // Truyền sang StudyFlowGradesScreen
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: GradeViewModel = viewModel(factory = GradeViewModelFactory(context))
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var newSemesterName by remember { mutableStateOf("") }

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
            Text("Quản lý Điểm số", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A))
        }

        Text(
            "Danh sách Học kỳ",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2A)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(semesters) { semester ->
                Surface(
                    onClick = { 
                        onNavigateToGrades(semester.semesterId) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                semester.semesterName, 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2A),
                                modifier = Modifier.weight(1f)
                            )
                            if (semester.isCurrent) {
                                Surface(
                                    color = Color(0xFFE0E7FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Hiện tại", 
                                        color = Color(0xFF4143D5), 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Tín chỉ tích lũy", fontSize = 12.sp, color = Color(0xFF767586))
                                Text("${semester.totalCredits}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4143D5))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("GPA (Hệ 4)", fontSize = 12.sp, color = Color(0xFF767586))
                                Text(String.format(java.util.Locale.US, "%.2f", semester.totalGpa4), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            }
        }

        // Nút thêm học kỳ
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
                .navigationBarsPadding(),
            containerColor = Color(0xFF4143D5),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm học kỳ")
        }
    }

    if (showAddDialog) {
        var isCurrent by remember { mutableStateOf(false) }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm Học kỳ mới", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newSemesterName,
                        onValueChange = { newSemesterName = it },
                        label = { Text("Tên học kỳ (VD: Học kỳ 1, 2023-2024)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isCurrent,
                            onCheckedChange = { isCurrent = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4143D5))
                        )
                        Text("Đặt làm học kỳ hiện tại", fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSemester(newSemesterName, isCurrent)
                        showAddDialog = false
                        newSemesterName = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4143D5))
                ) {
                    Text("Tạo mới")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Hủy", color = Color(0xFF767586))
                }
            }
        )
    }
}
