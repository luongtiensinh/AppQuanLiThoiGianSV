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
import com.example.apphotrohoctap.ui.pomodoro.StudyFlowPomodoroScreen
import com.example.apphotrohoctap.ui.registration.StudyFlowRegisterScreen
import com.example.apphotrohoctap.ui.schedule.StudyFlowScheduleScreen
import com.example.apphotrohoctap.ui.schedule.StudyFlowAddScheduleScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowTasksScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apphotrohoctap.ui.navigation.Screen
import com.example.apphotrohoctap.ui.tasks.StudyFlowTaskDetailScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowAddTaskScreen
import com.example.apphotrohoctap.ui.courses.StudyFlowCourseDetailScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowNotesScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowAddNoteScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowEditNoteScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowGradesScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowAddSubjectScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowSubjectDetailScreen
import com.example.apphotrohoctap.ui.settings.StudyFlowSettingsScreen
import com.example.apphotrohoctap.ui.theme.AppHoTroHocTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = com.example.apphotrohoctap.data.SessionManager(this)
        val startDest = if (sessionManager.isLoggedIn()) Screen.Home.route else Screen.Login.route
        
        setContent {
            AppHoTroHocTapTheme {
                val navController = rememberNavController()

                val navToTab: (String) -> Unit = { tab ->
                    val route = when (tab) {
                        "Trang chủ" -> Screen.Home.route
                        "Lịch biểu" -> Screen.Schedule.route
                        "Nhiệm vụ" -> Screen.Tasks.route
                        "Ghi chú" -> Screen.Notes.route
                        "Điểm số" -> Screen.Grades.route
                        "Cài đặt" -> Screen.Settings.route
                        else -> Screen.Home.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                
                val pomodoroViewModel: com.example.apphotrohoctap.ui.pomodoro.PomodoroViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                NavHost(navController = navController, startDestination = startDest) {
                    composable(Screen.Login.route) {
                        StudyFlowLoginScreen(
                            onRegister = { navController.navigate(Screen.Register.route) },
                            onLogin = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Register.route) {
                        StudyFlowRegisterScreen(
                            onBack = { navController.popBackStack() },
                            onSignIn = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Home.route) {
                        val pomodoroState by pomodoroViewModel.uiState.collectAsStateWithLifecycle()
                        StudyFlowHomeScreen(
                            onAddTask = { navController.navigate(Screen.AddTask.route) },
                            onStartFocus = { navController.navigate(Screen.Pomodoro.route) },
                            onCourseClick = { courseName -> navController.navigate(Screen.CourseDetail.createRoute(courseName)) },
                            onTabSelected = navToTab,
                            onProfileClick = { navController.navigate(Screen.Settings.route) },
                            pomodoroState = pomodoroState
                        )
                    }
                    composable(Screen.Pomodoro.route) {
                        StudyFlowPomodoroScreen(
                            pomodoroViewModel = pomodoroViewModel,
                            onClose = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Schedule.route) {
                        StudyFlowScheduleScreen(
                            onAddEvent = { navController.navigate(Screen.AddSchedule.route) },
                            onEditEvent = { scheduleId -> navController.navigate(Screen.EditSchedule.createRoute(scheduleId)) },
                            onTabSelected = navToTab
                        )
                    }
                    composable(Screen.Tasks.route) {
                        StudyFlowTasksScreen(
                            onAddTask = { navController.navigate(Screen.AddTask.route) },
                            onTaskClick = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId.toLongOrNull() ?: 0L)) },
                            onTabSelected = navToTab
                        )
                    }
                    composable(Screen.Notes.route) {
                        StudyFlowNotesScreen(
                            onCreateNote = { navController.navigate(Screen.AddNote.route) },
                            onEditNote = { noteId -> navController.navigate(Screen.EditNote.createRoute(noteId.toString())) },
                            onTabSelected = navToTab
                        )
                    }
                    composable(Screen.Grades.route) {
                        StudyFlowGradesScreen(
                            onBack = { navController.popBackStack() },
                            onAddSubject = { navController.navigate(Screen.AddSubject.route) },
                            onSubjectClick = { subjectId -> navController.navigate(Screen.SubjectDetail.createRoute(subjectId)) },
                            onTabSelected = navToTab
                        )
                    }
                    composable(Screen.AddSubject.route) {
                        StudyFlowAddSubjectScreen(onBack = { navController.popBackStack() })
                    }
                    composable(
                        Screen.SubjectDetail.route,
                        arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                        StudyFlowSubjectDetailScreen(
                            subjectId = subjectId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Settings.route) {
                        StudyFlowSettingsScreen(
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true } // Clear entire back stack
                                }
                            }
                        )
                    }
                    composable(
                        route = Screen.CourseDetail.route,
                        arguments = listOf(navArgument("courseName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courseName = backStackEntry.arguments?.getString("courseName") ?: ""
                        StudyFlowCourseDetailScreen(
                            courseName = courseName,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddTask.route) {
                        StudyFlowAddTaskScreen(
                            onBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddSchedule.route) {
                        StudyFlowAddScheduleScreen(
                            scheduleId = null,
                            onBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.EditSchedule.route,
                        arguments = listOf(navArgument("scheduleId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val scheduleId = backStackEntry.arguments?.getLong("scheduleId")
                        StudyFlowAddScheduleScreen(
                            scheduleId = scheduleId,
                            onBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AddNote.route) {
                        StudyFlowAddNoteScreen(
                            onBack = { navController.popBackStack() },
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.EditNote.route,
                        arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
                        StudyFlowEditNoteScreen(
                            noteId = noteId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.TaskDetail.route,
                        arguments = listOf(navArgument("taskId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
                        StudyFlowTaskDetailScreen(
                            taskId = taskId,
                            onBack = { navController.popBackStack() },
                            onDelete = { navController.popBackStack() }
                        )
                    }
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
