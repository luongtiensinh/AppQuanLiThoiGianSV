package com.example.apphotrohoctap.ui.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.apphotrohoctap.R

@Composable
fun StudyFlowTasksScreen(
    onTabSelected: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddTask: () -> Unit = {}
) {
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModelFactory(LocalContext.current))
    val tasks by viewModel.tasks.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    val visibleTasks = tasks.filter { task ->
        val isDone = task.completed
        val matchesFilter = when (selectedFilter) {
            "class" -> task.category == "class"
            "deadline" -> task.category == "deadline"
            "assignment" -> task.category == "assignment"
            "done" -> isDone
            else -> true
        }
        matchesFilter && (searchText.isBlank() || "${task.subject} ${task.title}".contains(searchText.trim(), ignoreCase = true))
    }

    Box(Modifier.fillMaxSize().background(TasksBackground)) {
        Column(Modifier.fillMaxSize()) {
            TasksHeader(onNotificationClick, onProfileClick)
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SearchAndFilters(
                        searchText = searchText,
                        onSearchChange = { searchText = it },
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        tasks = tasks
                    )
                    TodayProgressCard(
                        tasks = tasks
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    listOf("Hôm nay", "Ngày mai", "Tuần này").forEach { group ->
                        val groupTasks = visibleTasks.filter { it.group == group }
                        if (groupTasks.isNotEmpty()) {
                            TaskGroup(
                                title = group,
                                date = when (group) {
                                    "Hôm nay" -> "Thứ Tư, 24/04"
                                    "Ngày mai" -> "Thứ Năm, 25/04"
                                    else -> "Đến 28/04"
                                },
                                tasks = groupTasks,
                                onToggleComplete = { viewModel.toggleCompleted(it) }
                            )
                        }
                    }
                    if (visibleTasks.isEmpty()) {
                        Text("Không tìm thấy nhiệm vụ phù hợp.", modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), color = TasksSecondary, fontSize = 13.sp)
                    }
                    FriendlyBottomCard(onAddTask)
                }
            }
            TasksBottomNavigation(onTabSelected)
        }
        Surface(
            onClick = onAddTask,
            modifier = Modifier.align(Alignment.BottomEnd).navigationBarsPadding()
                .padding(end = 16.dp, bottom = 96.dp).size(56.dp),
            color = TasksBright,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) { TasksIcon("home_add.svg", Modifier.size(16.333.dp), "Thêm nhiệm vụ") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudyFlowTasksScreenPreview() {
    MaterialTheme { StudyFlowTasksScreen() }
}
