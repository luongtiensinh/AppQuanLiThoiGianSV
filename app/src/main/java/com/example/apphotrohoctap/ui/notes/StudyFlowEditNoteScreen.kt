package com.example.apphotrohoctap.ui.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apphotrohoctap.data.NoteEntity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EditNoteBackground = Color(0xFFFCF8FF)
private val EditNoteInk = Color(0xFF1A1A2A)
private val EditNoteText = Color(0xFF464555)
private val EditNoteMuted = Color(0xFF767586)
private val EditNoteBrand = Color(0xFF4143D5)
private val EditNotePale = Color(0xFFEFECFF)

@Composable
fun StudyFlowEditNoteScreen(noteId: Long, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context))
    val notesEntities by viewModel.notes.collectAsState()
    
    val currentNote = notesEntities.find { it.id == noteId }

    var title by remember(currentNote) { mutableStateOf(currentNote?.title ?: "") }
    var body by remember(currentNote) { mutableStateOf(currentNote?.content ?: "") }
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    var selectedTool by remember { mutableStateOf("B") }
    BackHandler(onBack = onBack)

    // Quay lại nếu không tìm thấy note
    if (currentNote == null) {
        return
    }

    Box(Modifier.fillMaxSize().background(EditNoteBackground)) {
        Column(Modifier.fillMaxSize()) {
            EditNoteAppHeader(onBack)
            Row(
                Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Chỉnh sửa ghi chú", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = EditNoteInk)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                        Text("Tự động đồng bộ", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EditNoteText)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(onClick = {
                        viewModel.updateNote(currentNote.copy(title = title, content = body, updatedAtMillis = System.currentTimeMillis()))
                        android.widget.Toast.makeText(context, "Đã lưu", android.widget.Toast.LENGTH_SHORT).show()
                    }, color = EditNoteBrand, shape = CircleShape) {
                        Text("✓  Lưu", Modifier.padding(horizontal = 16.dp, vertical = 12.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Box {
                        Surface(onClick = { menuExpanded = true }, modifier = Modifier.size(44.dp), color = Color.Transparent, shape = CircleShape) {
                            Box(contentAlignment = Alignment.Center) { Text("⋮", fontSize = 23.sp, color = EditNoteInk) }
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Chia sẻ liên kết") }, onClick = { menuExpanded = false })
                            DropdownMenuItem(text = { Text("Lịch sử phiên bản") }, onClick = { menuExpanded = false })
                            DropdownMenuItem(text = { Text("Xóa ghi chú", color = Color(0xFFBA1A1A)) }, onClick = { menuExpanded = false; showDeleteDialog = true })
                        }
                    }
                }
            }

            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp).padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 22.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold, color = EditNoteInk),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = EditNoteBrand
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val paleColor = try { Color(android.graphics.Color.parseColor(currentNote.colorKey)).copy(alpha = 0.15f) } catch(e: Exception) { EditNotePale }
                    val fgColor = try { Color(android.graphics.Color.parseColor(currentNote.colorKey)) } catch(e: Exception) { EditNoteBrand }
                    
                    EditNoteChip("▣  ${currentNote.courseName.ifBlank { "Không phân loại" }}", paleColor, fgColor)
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        listOf(Color(0xFF4143D5), Color(0xFF0EA5E9), Color(0xFFF59E0B), Color(0xFF10B981)).forEachIndexed { index, color ->
                            val isSelected = currentNote.colorKey.equals(String.format("#%06X", (0xFFFFFF and color.value.toInt())), ignoreCase = true)
                            Surface(onClick = {
                                val hexColor = String.format("#%06X", (0xFFFFFF and color.value.toInt()))
                                viewModel.updateNote(currentNote.copy(colorKey = hexColor))
                            }, modifier = Modifier.size(if (isSelected) 26.dp else 22.dp), color = color, shape = CircleShape) {
                                if (isSelected) Box(contentAlignment = Alignment.Center) { Text("✓", fontSize = 11.sp, color = Color.White) }
                            }
                        }
                    }
                }

                TextField(
                    value = body,
                    onValueChange = { body = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 360.dp),
                    textStyle = TextStyle(fontSize = 16.sp, lineHeight = 26.sp, color = EditNoteText),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = EditNoteBrand
                    )
                )

                Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy • HH:mm", java.util.Locale.getDefault())
                    val dateStr = sdf.format(java.util.Date(currentNote.updatedAtMillis))
                    Text("◷  Chỉnh sửa lần cuối: $dateStr", fontSize = 9.sp, color = EditNoteMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    EditNoteChip("${body.length} ký tự", EditNotePale, EditNoteBrand)
                }
            }
        }

        Surface(Modifier.align(Alignment.BottomCenter), color = Color.White.copy(alpha = .96f), shadowElevation = 8.dp) {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                listOf("B", "I", "U", "☷", "☑", "‹›").forEach { tool ->
                    Surface(onClick = { selectedTool = tool }, modifier = Modifier.size(40.dp), color = if (selectedTool == tool) EditNoteBrand else Color.Transparent, shape = RoundedCornerShape(10.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text(tool, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (selectedTool == tool) Color.White else EditNoteInk) }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        NoteDeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                showDeleteDialog = false
                viewModel.deleteNote(currentNote)
                onBack()
            }
        )
    }
}

@Composable
private fun EditNoteAppHeader(onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().height(64.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(onClick = onBack, modifier = Modifier.size(36.dp), color = Color.Transparent, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) { Text("‹", fontSize = 28.sp, color = EditNoteInk) }
            }
            Text("▣", fontSize = 20.sp, color = EditNoteBrand)
            Text("Soạn Thảo Ghi Chú", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = EditNoteInk)
        }
        Surface(color = EditNotePale, shape = CircleShape) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Text("N", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditNoteBrand) }
        }
    }
}

@Composable
private fun EditNoteChip(text: String, background: Color, foreground: Color) {
    Surface(color = background, shape = CircleShape) {
        Text(text, Modifier.padding(horizontal = 9.dp, vertical = 6.dp), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = foreground, maxLines = 1)
    }
}

@Composable
private fun NoteDeleteConfirmationDialog(onDismiss: () -> Unit, onConfirmDelete: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().widthIn(max = 384.dp),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 16.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(color = Color(0xFFFFDAD6), shape = CircleShape) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Text("▤", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Xóa ghi chú?", fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, color = EditNoteInk)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Hành động này không thể hoàn tác. Ghi chú của bạn sẽ bị xóa vĩnh viễn.",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = EditNoteText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Hủy", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = EditNoteText)
                    }
                    androidx.compose.material3.Button(
                        onClick = onConfirmDelete,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = CircleShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                    ) {
                        Text("▤  Xóa", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}
