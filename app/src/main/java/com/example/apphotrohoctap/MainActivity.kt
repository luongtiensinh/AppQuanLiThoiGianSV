package com.example.apphotrohoctap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.apphotrohoctap.ui.login.StudyFlowLoginScreen
import com.example.apphotrohoctap.ui.home.StudyFlowHomeScreen
import com.example.apphotrohoctap.ui.registration.StudyFlowRegisterScreen
import com.example.apphotrohoctap.ui.schedule.StudyFlowScheduleScreen
import com.example.apphotrohoctap.ui.schedule.StudyFlowAddScheduleScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowTasksScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowAddTaskScreen
import com.example.apphotrohoctap.ui.courses.StudyFlowCourseDetailScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowNotesScreen
import com.example.apphotrohoctap.ui.settings.StudyFlowSettingsScreen
import com.example.apphotrohoctap.ui.theme.AppHoTroHocTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppHoTroHocTapTheme {
                var showRegistration by remember { mutableStateOf(false) }
                var showHome by remember { mutableStateOf(false) }
                var showSchedule by remember { mutableStateOf(false) }
                var showTasks by remember { mutableStateOf(false) }
                var showAddTask by remember { mutableStateOf(false) }
                var showAddSchedule by remember { mutableStateOf(false) }
                var showCourseDetail by remember { mutableStateOf(false) }
                var showNotes by remember { mutableStateOf(false) }
                var showSettings by remember { mutableStateOf(false) }
                if (showAddSchedule) {
                    StudyFlowAddScheduleScreen(
                        onBack = { showAddSchedule = false },
                        onSave = { showAddSchedule = false }
                    )
                } else if (showAddTask) {
                    StudyFlowAddTaskScreen(
                        onBack = { showAddTask = false },
                        onSave = { showAddTask = false }
                    )
                } else if (showCourseDetail) {
                    StudyFlowCourseDetailScreen(onBack = { showCourseDetail = false })
                } else if (showSettings) {
                    StudyFlowSettingsScreen(
                        onTabSelected = { tab ->
                            when (tab) {
                                "Trang chủ" -> { showSettings = false; showHome = true }
                                "Lịch biểu" -> { showSettings = false; showSchedule = true }
                                "Nhiệm vụ" -> { showSettings = false; showTasks = true }
                                "Ghi chú" -> { showSettings = false; showNotes = true }
                            }
                        },
                        onLogout = {
                            // Đăng xuất: Tắt cờ hiện cài đặt và bật lại màn hình Đăng nhập
                            showSettings = false
                            showRegistration = false
                            // Màn hình login sẽ hiển thị vì tất cả các cờ khác đều là false
                        }
                    )
                } else if (showNotes) {
                    StudyFlowNotesScreen(
                        onTabSelected = { tab ->
                            when (tab) {
                                "Trang chủ" -> { showNotes = false; showHome = true }
                                "Lịch biểu" -> { showNotes = false; showSchedule = true }
                                "Nhiệm vụ" -> { showNotes = false; showTasks = true }
                                "Cài đặt" -> { showNotes = false; showSettings = true }
                            }
                        }
                    )
                } else if (showTasks) {
                    StudyFlowTasksScreen(
                        onAddTask = { showAddTask = true },
                        onTabSelected = { tab ->
                            when (tab) {
                                "Trang chủ" -> { showTasks = false; showHome = true }
                                "Lịch biểu" -> { showTasks = false; showSchedule = true }
                                "Ghi chú" -> { showTasks = false; showNotes = true }
                                "Cài đặt" -> { showTasks = false; showSettings = true }
                            }
                        }
                    )
                } else if (showSchedule) {
                    StudyFlowScheduleScreen(
                        onAddEvent = { showAddSchedule = true },
                        onTabSelected = { tab ->
                            when (tab) {
                                "Trang chủ" -> { showSchedule = false; showHome = true }
                                "Nhiệm vụ" -> { showSchedule = false; showTasks = true }
                                "Ghi chú" -> { showSchedule = false; showNotes = true }
                                "Cài đặt" -> { showSchedule = false; showSettings = true }
                            }
                        }
                    )
                } else if (showHome) {
                    StudyFlowHomeScreen(
                        onAddTask = { showAddTask = true },
                        onCourseClick = { showCourseDetail = true },
                        onTabSelected = { tab ->
                            when (tab) {
                                "Lịch biểu" -> { showHome = false; showSchedule = true }
                                "Nhiệm vụ" -> { showHome = false; showTasks = true }
                                "Ghi chú" -> { showHome = false; showNotes = true }
                                "Cài đặt" -> { showHome = false; showSettings = true }
                            }
                        }
                    )
                } else if (showRegistration) {
                    StudyFlowRegisterScreen(
                        onBack = { showRegistration = false },
                        onSignIn = { showRegistration = false }
                    )
                } else {
                    StudyFlowLoginScreen(
                        onRegister = { showRegistration = true },
                        onLogin = { showHome = true }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppHoTroHocTapTheme {
        Greeting("Android")
    }
}
