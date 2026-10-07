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
import com.example.apphotrohoctap.ui.tasks.StudyFlowTasksScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowAddTaskScreen
import com.example.apphotrohoctap.ui.courses.StudyFlowCourseDetailScreen
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
                var showCourseDetail by remember { mutableStateOf(false) }
                if (showAddTask) {
                    StudyFlowAddTaskScreen(
                        onBack = { showAddTask = false },
                        onSave = { showAddTask = false }
                    )
                } else if (showCourseDetail) {
                    StudyFlowCourseDetailScreen(onBack = { showCourseDetail = false })
                } else if (showTasks) {
                    StudyFlowTasksScreen(
                        onAddTask = { showAddTask = true },
                        onTabSelected = { tab ->
                            when (tab) {
                                "Trang chủ" -> { showTasks = false; showSchedule = false; showHome = true }
                                "Lịch biểu" -> { showTasks = false; showHome = false; showSchedule = true }
                            }
                        }
                    )
                } else if (showSchedule) {
                    StudyFlowScheduleScreen(
                        onAddEvent = { showAddTask = true },
                        onTabSelected = { tab ->
                            when (tab) {
                                "Trang chủ" -> { showSchedule = false; showHome = true }
                                "Nhiệm vụ" -> { showSchedule = false; showTasks = true }
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
