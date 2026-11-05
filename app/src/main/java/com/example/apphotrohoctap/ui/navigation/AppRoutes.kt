package com.example.apphotrohoctap.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Tasks : Screen("tasks")
    object Notes : Screen("notes")
    
    // Grades Flow
    object Semesters : Screen("semesters") // Màn hình quản lý học kỳ
    object Grades : Screen("grades/{semesterId}") { // Màn hình điểm số của 1 học kỳ
        fun createRoute(semesterId: Long) = "grades/$semesterId"
    }
    object AddSubject : Screen("addSubject")
    object SubjectDetail : Screen("subjectDetail/{subjectId}") {
        fun createRoute(subjectId: Long) = "subjectDetail/$subjectId"
    }
    
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
