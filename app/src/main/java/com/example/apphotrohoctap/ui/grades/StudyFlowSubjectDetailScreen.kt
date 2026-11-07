package com.example.apphotrohoctap.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StudyFlowSubjectDetailScreen(
    subjectId: Long,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: GradeViewModel = viewModel(factory = GradeViewModelFactory(context))
    val subjectState by viewModel.getSubject(subjectId).collectAsStateWithLifecycle(initialValue = null)
    val subject = subjectState

    var editingComponent by rememberSaveable { mutableStateOf<GradeComponentUiModel?>(null) }
    var tempScoreText by rememberSaveable { mutableStateOf("") }
    
    // States for Edit Mode
    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var showEditSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var editSubjectName by rememberSaveable { mutableStateOf("") }
    var editCredits by rememberSaveable { mutableStateOf("") }
    var editTargetGrade by rememberSaveable { mutableStateOf("") }

    var editingComponentDetails by remember { mutableStateOf<GradeComponentUiModel?>(null) }
    var tempCompName by remember { mutableStateOf("") }
    var tempCompWeight by remember { mutableStateOf("") }

    var showAddComponentDialog by remember { mutableStateOf(false) }

    var errorText by rememberSaveable { mutableStateOf<String?>(null) }

    if (subject == null) return

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    subject.subjectName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2A),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text("${subject.credits} tín chỉ • Mục tiêu: ${subject.targetGrade}", fontSize = 12.sp, color = Color(0xFF767586))
            }
            
            // Edit Toggle
            IconButton(onClick = { 
                isEditMode = !isEditMode 
                if (isEditMode) {
                    editSubjectName = subject.subjectName
                    editCredits = subject.credits.toString()
                    editTargetGrade = subject.targetGrade
                }
            }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = if (isEditMode) Color(0xFF4143D5) else Color(0xFF767586)
                )
            }
        }

        if (isEditMode) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Thông tin môn học", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = editSubjectName,
                        onValueChange = { editSubjectName = it },
                        label = { Text("Tên môn") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editCredits,
                            onValueChange = { editCredits = it },
                            label = { Text("Tín chỉ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editTargetGrade,
                            onValueChange = { editTargetGrade = it },
                            label = { Text("Mục tiêu") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Button(
                        onClick = {
                            val cred = editCredits.toIntOrNull()
                            if (editSubjectName.isNotBlank() && cred != null && cred > 0 && editTargetGrade.isNotBlank()) {
                                viewModel.updateSubject(subject.subjectId, editSubjectName, cred, editTargetGrade)
                                isEditMode = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4143D5))
                    ) {
                        Text("Lưu thông tin")
                    }
                }
            }
        }

        // Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tích lũy hệ 10 hiện tại:", fontSize = 14.sp, color = Color(0xFF464555))
                    Text(String.format(java.util.Locale.US, "%.2f", subject.currentAccumulatedScore), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4143D5))
                }
                Divider(color = Color(0xFFE3E0F7))
                if (subject.isCompleted) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Điểm tổng kết (hệ chữ):", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A))
                        Text(subject.finalLetterGrade ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                } else if (subject.requiredScoreToTarget != null) {
                    Text("Để đạt được ${subject.targetGrade}, trung bình các cột điểm còn lại bạn cần đạt:", fontSize = 13.sp, color = Color(0xFF464555), lineHeight = 18.sp)
                    
                    val required = subject.requiredScoreToTarget
                    val warningColor = when {
                        required > 8.0 -> Color(0xFFDC2626) // Khó - Đỏ
                        required > 5.0 -> Color(0xFFD97706) // Trung bình - Vàng
                        else -> Color(0xFF16A34A) // Dễ - Xanh
                    }
                    
                    Text(
                        String.format(java.util.Locale.US, "%.2f điểm", required),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = warningColor
                    )
                } else {
                    Text("Bạn không thể đạt được mục tiêu ${subject.targetGrade} nữa, hãy cố gắng hết sức nhé!", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFBA1A1A))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Cột điểm thành phần", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A))
            if (isEditMode) {
                IconButton(onClick = {
                    tempCompName = ""
                    tempCompWeight = ""
                    showAddComponentDialog = true
                }) {
                    Icon(Icons.Default.Add, "Thêm cột", tint = Color(0xFF4143D5))
                }
            }
        }
        
        // Cảnh báo tổng trọng số
        val totalWeight = subject.components.sumOf { it.weightPercent }
        if (kotlin.math.abs(totalWeight - 100.0) > 0.1) {
            Text(
                "Lưu ý: Tổng trọng số hiện tại là $totalWeight% (Nên là 100%). Hãy chỉnh sửa lại.",
                color = Color(0xFFDC2626),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(subject.components, key = { it.componentId }) { comp ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 1.dp,
                    onClick = {
                        if (isEditMode) {
                            editingComponentDetails = comp
                            tempCompName = comp.componentName
                            tempCompWeight = comp.weightPercent.toString()
                        } else {
                            editingComponent = comp
                            tempScoreText = comp.achievedScore?.toString() ?: ""
                            errorText = null
                        }
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(comp.componentName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2A))
                            Text("Trọng số: ${comp.weightPercent}%", fontSize = 12.sp, color = Color(0xFF767586))
                        }
                        if (isEditMode) {
                            IconButton(onClick = { viewModel.deleteComponent(comp.componentId) }) {
                                Icon(Icons.Default.Delete, "Xóa", tint = Color(0xFFDC2626))
                            }
                        } else {
                            if (comp.achievedScore != null) {
                                Text("${comp.achievedScore}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4143D5))
                            } else {
                                Text("Chưa nhập", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFFBA1A1A))
                            }
                        }
                    }
                }
            }
            if (isEditMode) {
                item {
                    TextButton(
                        onClick = {
                            viewModel.deleteSubject(subjectId)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Xóa Môn Học Này", color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }

    // Dialog nhập điểm (Mode thường)
    if (editingComponent != null && !isEditMode) {
        AlertDialog(
            onDismissRequest = { editingComponent = null },
            title = { Text("Nhập điểm", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(editingComponent!!.componentName, fontSize = 14.sp, color = Color(0xFF464555))
                    OutlinedTextField(
                        value = tempScoreText,
                        onValueChange = { 
                            tempScoreText = it
                            errorText = null
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("Ví dụ: 8.5") },
                        isError = errorText != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorText != null) {
                        Text(errorText!!, color = Color(0xFFBA1A1A), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = tempScoreText.toDoubleOrNull()
                        if (tempScoreText.isBlank()) {
                            viewModel.updateComponentScore(subject.subjectId, editingComponent!!.componentId, null)
                            editingComponent = null
                        } else if (score == null || score < 0.0 || score > 10.0) {
                            errorText = "Điểm phải từ 0 đến 10"
                        } else {
                            viewModel.updateComponentScore(subject.subjectId, editingComponent!!.componentId, score)
                            editingComponent = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4143D5))
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingComponent = null }) {
                    Text("Hủy", color = Color(0xFF767586))
                }
            }
        )
    }

    // Dialog thêm component
    if (showAddComponentDialog) {
        AlertDialog(
            onDismissRequest = { showAddComponentDialog = false },
            title = { Text("Thêm cột điểm") },
            text = {
                Column {
                    OutlinedTextField(value = tempCompName, onValueChange = { tempCompName = it }, label = { Text("Tên cột") })
                    OutlinedTextField(value = tempCompWeight, onValueChange = { tempCompWeight = it }, label = { Text("Trọng số (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(onClick = {
                    val w = tempCompWeight.toDoubleOrNull()
                    if (tempCompName.isNotBlank() && w != null) {
                        viewModel.addComponent(subjectId, tempCompName, w)
                        showAddComponentDialog = false
                    }
                }) { Text("Lưu") }
            },
            dismissButton = {
                TextButton(onClick = { showAddComponentDialog = false }) { Text("Hủy") }
            }
        )
    }

    // Dialog sửa component
    if (editingComponentDetails != null && isEditMode) {
        AlertDialog(
            onDismissRequest = { editingComponentDetails = null },
            title = { Text("Sửa cột điểm") },
            text = {
                Column {
                    OutlinedTextField(value = tempCompName, onValueChange = { tempCompName = it }, label = { Text("Tên cột") })
                    OutlinedTextField(value = tempCompWeight, onValueChange = { tempCompWeight = it }, label = { Text("Trọng số (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(onClick = {
                    val w = tempCompWeight.toDoubleOrNull()
                    if (tempCompName.isNotBlank() && w != null) {
                        val uiModel = editingComponentDetails!!.copy(
                            componentName = tempCompName,
                            weightPercent = w
                        )
                        viewModel.updateComponentFull(uiModel, subjectId)
                        editingComponentDetails = null
                    }
                }) { Text("Lưu") }
            },
            dismissButton = {
                TextButton(onClick = { editingComponentDetails = null }) { Text("Hủy") }
            }
        )
    }
}
