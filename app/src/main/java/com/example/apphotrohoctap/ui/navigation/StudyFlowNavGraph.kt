package com.example.apphotrohoctap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apphotrohoctap.ui.login.StudyFlowLoginScreen
import com.example.apphotrohoctap.ui.home.StudyFlowHomeScreen
import com.example.apphotrohoctap.ui.pomodoro.StudyFlowPomodoroScreen
import com.example.apphotrohoctap.ui.registration.StudyFlowRegisterScreen
import com.example.apphotrohoctap.ui.schedule.StudyFlowScheduleScreen
import com.example.apphotrohoctap.ui.schedule.StudyFlowAddScheduleScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowTasksScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowTaskDetailScreen
import com.example.apphotrohoctap.ui.tasks.StudyFlowAddTaskScreen
import com.example.apphotrohoctap.ui.courses.StudyFlowCourseDetailScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowNotesScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowAddNoteScreen
import com.example.apphotrohoctap.ui.notes.StudyFlowEditNoteScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowSemesterScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowGradesScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowAddSubjectScreen
import com.example.apphotrohoctap.ui.grades.StudyFlowSubjectDetailScreen
import com.example.apphotrohoctap.ui.settings.StudyFlowSettingsScreen
import com.example.apphotrohoctap.ui.pomodoro.PomodoroViewModel

@Composable
fun StudyFlowNavGraph(
    navController: NavHostController,
    startDestination: String,
    pomodoroViewModel: PomodoroViewModel,
    navToTab: (BottomNavTab) -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
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
                onTaskClick = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId.toLongOrNull() ?: 0L)) },
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
                onTabSelected = navToTab,
                onProfileClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Tasks.route) {
            StudyFlowTasksScreen(
                onAddTask = { navController.navigate(Screen.AddTask.route) },
                onTaskClick = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId.toLongOrNull() ?: 0L)) },
                onTabSelected = navToTab,
                onProfileClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Notes.route) {
            StudyFlowNotesScreen(
                onCreateNote = { navController.navigate(Screen.AddNote.route) },
                onEditNote = { noteId -> navController.navigate(Screen.EditNote.createRoute(noteId.toString())) },
                onTabSelected = navToTab
            )
        }
        
        // --- Grades Flow ---
        composable(Screen.Semesters.route) {
            StudyFlowSemesterScreen(
                onNavigateToGrades = { semesterId -> navController.navigate(Screen.Grades.createRoute(semesterId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Grades.route,
            arguments = listOf(navArgument("semesterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 0L
            StudyFlowGradesScreen(
                semesterId = semesterId,
                onBack = { navController.popBackStack() },
                onAddSubject = { navController.navigate(Screen.AddSubject.createRoute(semesterId)) },
                onSubjectClick = { subjectId -> navController.navigate(Screen.SubjectDetail.createRoute(subjectId)) }
            )
        }
        composable(
            route = Screen.AddSubject.route,
            arguments = listOf(navArgument("semesterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val semesterId = backStackEntry.arguments?.getLong("semesterId") ?: 0L
            StudyFlowAddSubjectScreen(
                semesterId = semesterId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.SubjectDetail.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            StudyFlowSubjectDetailScreen(
                subjectId = subjectId,
                onBack = { navController.popBackStack() }
            )
        }
        // -------------------
        
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
            val encodedCourseName = backStackEntry.arguments?.getString("courseName") ?: ""
            val courseName = android.net.Uri.decode(encodedCourseName)
            StudyFlowCourseDetailScreen(
                courseName = courseName,
                onBack = { navController.popBackStack() },
                onAddTask = { navController.navigate(Screen.AddTask.route) },
                onTaskClick = { taskId -> navController.navigate(Screen.TaskDetail.createRoute(taskId)) },
                onAddSchedule = { navController.navigate(Screen.AddSchedule.route) },
                onScheduleClick = { scheduleId -> navController.navigate(Screen.EditSchedule.createRoute(scheduleId)) },
                onAddNote = { navController.navigate(Screen.AddNote.route) },
                onNoteClick = { noteId -> navController.navigate(Screen.EditNote.createRoute(noteId.toString())) }
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
