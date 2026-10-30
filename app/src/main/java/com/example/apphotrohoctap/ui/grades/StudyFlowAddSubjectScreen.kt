package com.example.apphotrohoctap.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudyFlowAddSubjectScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: GradeViewModel = viewModel(factory = GradeViewModelFactory(context))

    var subjectName by remember { mutableStateOf("") }
    var creditsText by remember { mutableStateOf("") }
    var targetGrade by remember { mutableStateOf("A") }

    data class TempComponent(val name: String, val weight: String)
    var components by remember { mutableStateOf(listOf(
        TempComponent("Chuyên cần", "10"),
        TempComponent("Giữa kỳ", "30"),
        TempComponent("Cuối kỳ", "60")
    )) }

    var errorText by remember { mutableStateOf<String?>(null) }

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
            Text("Thêm môn học mới", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Tên môn
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Tên môn học") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Số tín chỉ
                OutlinedTextField(
                    value = creditsText,
                    onValueChange = { creditsText = it },
                    label = { Text("Số tín chỉ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                // Mục tiêu điểm
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mục tiêu điểm", fontSize = 12.sp, color = Color(0xFF464555), modifier = Modifier.padding(bottom = 4.dp))
                    Surface(color = Color(0xFFF5F2FF), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            listOf("A", "B+", "B").forEach { grade ->
                                val selected = grade == targetGrade
                                Surface(
                                    onClick = { targetGrade = grade },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) Color(0xFF4143D5) else Color.Transparent,
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(grade, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (selected) Color.White else Color(0xFF767586))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Text("Điểm thành phần (Tổng phải = 100%)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A), modifier = Modifier.padding(top = 8.dp))
            
            components.forEachIndexed { index, comp ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = comp.name,
                        onValueChange = { newName ->
                            val newList = components.toMutableList()
                            newList[index] = comp.copy(name = newName)
                            components = newList
                        },
                        label = { Text("Tên (VD: Giữa kỳ)") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = comp.weight,
                        onValueChange = { newWeight ->
                            val newList = components.toMutableList()
                            newList[index] = comp.copy(weight = newWeight)
                            components = newList
                        },
                        label = { Text("Trọng số (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(onClick = {
                        val newList = components.toMutableList()
                        newList.removeAt(index)
                        components = newList
                    }) {
                        Text("×", fontSize = 24.sp, color = Color(0xFFBA1A1A))
                    }
                }
            }

            TextButton(
                onClick = { components = components + TempComponent("", "") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("＋ Thêm cột điểm")
            }

            if (errorText != null) {
                Text(errorText!!, color = Color(0xFFBA1A1A), fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        // Save Button
        Surface(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            onClick = {
                val credits = creditsText.toIntOrNull() ?: 0
                val totalWeight = components.sumOf { it.weight.toDoubleOrNull() ?: 0.0 }
                
                if (subjectName.isBlank()) {
                    errorText = "Vui lòng nhập tên môn học"
                } else if (credits <= 0) {
                    errorText = "Số tín chỉ không hợp lệ"
                } else if (components.isEmpty()) {
                    errorText = "Phải có ít nhất 1 điểm thành phần"
                } else if (kotlin.math.abs(totalWeight - 100.0) > 0.1) {
                    errorText = "Tổng trọng số phải bằng 100% (Hiện tại: $totalWeight%)"
                } else {
                    val finalComponents = components.map { Pair(it.name, it.weight.toDouble()) }
                    viewModel.addSubject(subjectName, credits, targetGrade, finalComponents)
                    onBack()
                }
            },
            color = Color(0xFF4143D5),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
                Text("Lưu môn học", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
