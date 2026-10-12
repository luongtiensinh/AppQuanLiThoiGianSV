package com.example.apphotrohoctap.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Tasks : Screen("tasks")
    object Notes : Screen("notes")
    object Settings : Screen("settings")
    object AddTask : Screen("addTask")
    object AddSchedule : Screen("addSchedule")
    object AddNote : Screen("addNote")
    object CourseDetail : Screen("courseDetail")
    
    object TaskDetail : Screen("taskDetail/{taskId}") {
        fun createRoute(taskId: Long) = "taskDetail/$taskId"
    }
    
    object EditNote : Screen("editNote/{noteId}") {
        fun createRoute(noteId: String) = "editNote/$noteId"
    }
}
