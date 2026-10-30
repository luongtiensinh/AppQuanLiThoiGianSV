package com.example.apphotrohoctap.ui.analytics

object GamificationEngine {

    fun evaluateBadges(
        completedTasksCount: Int,
        completedPomodoroSessions: Int,
        hasLateNightTask: Boolean,
        weeklyCompletionRate: Float
    ): List<BadgeItem> {
        return listOf(
            BadgeItem(
                id = "first_step",
                title = "Người Khởi Đầu",
                description = "Hoàn thành nhiệm vụ đầu tiên",
                iconEmoji = "🚀",
                isUnlocked = completedTasksCount >= 1,
                progressText = if (completedTasksCount >= 1) "Đã đạt" else "$completedTasksCount/1"
            ),
            BadgeItem(
                id = "pomo_master",
                title = "Chiến Thần Tập Trung",
                description = "Hoàn thành 5 chu kỳ Pomodoro",
                iconEmoji = "⏱️",
                isUnlocked = completedPomodoroSessions >= 5,
                progressText = "$completedPomodoroSessions/5 phiên"
            ),
            BadgeItem(
                id = "night_owl",
                title = "Cú Đêm Cần Mẫn",
                description = "Hoàn thành bài tập sau 22 giờ đêm",
                iconEmoji = "🌙",
                isUnlocked = hasLateNightTask,
                progressText = if (hasLateNightTask) "Đã mở khóa" else "Chưa mở"
            ),
            BadgeItem(
                id = "golden_week",
                title = "Tuần Lễ Vàng",
                description = "Đạt 100% mục tiêu nhiệm vụ tuần",
                iconEmoji = "🏆",
                isUnlocked = weeklyCompletionRate >= 1.0f && completedTasksCount > 0,
                progressText = "${(weeklyCompletionRate * 100).toInt()}%"
            )
        )
    }
}
