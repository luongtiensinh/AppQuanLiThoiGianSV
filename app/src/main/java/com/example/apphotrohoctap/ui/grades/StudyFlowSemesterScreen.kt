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
    
    var editingSemester by remember { mutableStateOf<SemesterUiModel?>(null) }
    var editSemesterName by remember { mutableStateOf("") }
    var editIsCurrent by remember { mutableStateOf(false) }
    
    var deletingSemester by remember { mutableStateOf<SemesterUiModel?>(null) }

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
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            
                            var menuExpanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(24.dp)) {
                                    Text("⋮", fontSize = 18.sp, color = Color(0xFF1A1A2A), fontWeight = FontWeight.Bold)
                                }
                                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, modifier = Modifier.background(Color.White)) {
                                    DropdownMenuItem(text = { Text("Sửa học kỳ", color = Color(0xFF1A1A2A)) }, onClick = { 
                                        menuExpanded = false
                                        editingSemester = semester
                                        editSemesterName = semester.semesterName
                                        editIsCurrent = semester.isCurrent
                                    })
                                    DropdownMenuItem(text = { Text("Xóa học kỳ", color = Color(0xFFBA1A1A)) }, onClick = { 
                                        menuExpanded = false
                                        deletingSemester = semester
                                    })
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
                        val cleanName = newSemesterName.trim()
                        val success = viewModel.addSemester(cleanName, isCurrent)
                        if (success) {
                            showAddDialog = false
                            newSemesterName = ""
                        } else {
                            android.widget.Toast.makeText(context, "Vui lòng nhập tên học kỳ", android.widget.Toast.LENGTH_SHORT).show()
                        }
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

    if (editingSemester != null) {
        AlertDialog(
            onDismissRequest = { editingSemester = null },
            title = { Text("Sửa Học kỳ", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editSemesterName,
                        onValueChange = { editSemesterName = it },
                        label = { Text("Tên học kỳ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = editIsCurrent,
                            onCheckedChange = { editIsCurrent = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4143D5))
                        )
                        Text("Đặt làm học kỳ hiện tại", fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        editingSemester?.let {
                            val cleanName = editSemesterName.trim()
                            val success = viewModel.updateSemester(it.semesterId, cleanName, editIsCurrent)
                            if (success) {
                                editingSemester = null
                            } else {
                                android.widget.Toast.makeText(context, "Vui lòng nhập tên học kỳ", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4143D5))
                ) {
                    Text("Cập nhật")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSemester = null }) {
                    Text("Hủy", color = Color(0xFF767586))
                }
            }
        )
    }

    if (deletingSemester != null) {
        AlertDialog(
            onDismissRequest = { deletingSemester = null },
            title = { Text("Xóa Học kỳ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A)) },
            text = { 
                Text("Việc này sẽ xóa học kỳ '${deletingSemester?.semesterName}' và TOÀN BỘ các môn học, điểm số bên trong nó. Bạn có chắc chắn muốn xóa không?") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        deletingSemester?.let {
                            viewModel.deleteSemester(it.semesterId)
                        }
                        deletingSemester = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                ) {
                    Text("Xóa vĩnh viễn")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingSemester = null }) {
                    Text("Hủy", color = Color(0xFF767586))
                }
            }
        )
    }
}
