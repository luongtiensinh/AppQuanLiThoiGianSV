package com.example.apphotrohoctap.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Tasks : Screen("tasks")
    object Notes : Screen("notes")
    object Settings : Screen("settings")
    object Pomodoro : Screen("pomodoro")
    object AddTask : Screen("addTask")
    object AddSchedule : Screen("addSchedule")
    object EditSchedule : Screen("editSchedule/{scheduleId}") {
        fun createRoute(scheduleId: Long) = "editSchedule/$scheduleId"
    }
    object AddNote : Screen("addNote")
    object CourseDetail : Screen("courseDetail/{courseName}") {
        fun createRoute(courseName: String) = "courseDetail/${android.net.Uri.encode(courseName)}"
    }
    
    object TaskDetail : Screen("taskDetail/{taskId}") {
        fun createRoute(taskId: Long) = "taskDetail/$taskId"
    }
    
    object EditNote : Screen("editNote/{noteId}") {
        fun createRoute(noteId: String) = "editNote/$noteId"
    }
}
