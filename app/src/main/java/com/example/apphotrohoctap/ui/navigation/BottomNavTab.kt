package com.example.apphotrohoctap.ui.navigation

enum class BottomNavTab(
    val label: String,
    val iconAsset: String,
    val route: String
) {
    HOME("Trang chủ", "home_nav_home.svg", Screen.Home.route),
    SCHEDULE("Lịch biểu", "home_nav_schedule.svg", Screen.Schedule.route),
    TASKS("Nhiệm vụ", "home_nav_tasks.svg", Screen.Tasks.route),
    NOTES("Ghi chú", "home_nav_notes.svg", Screen.Notes.route),
    GRADES("Điểm số", "home_document.svg", Screen.Grades.route);

    companion object {
        fun fromLabel(label: String): BottomNavTab {
            return entries.find { it.label.equals(label, ignoreCase = true) } ?: HOME
        }
    }
}
