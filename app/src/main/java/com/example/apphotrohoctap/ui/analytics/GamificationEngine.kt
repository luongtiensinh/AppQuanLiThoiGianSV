package com.example.apphotrohoctap.ui.analytics

object GamificationEngine {

    fun evaluateBadges(
        completedTasksCount: Int,
        completedPomodoroSessions: Int,
        hasLateNightTask: Boolean,
        weeklyCompletionRate: Float,
        hasEarlyBirdTask: Boolean = false,
        subjectCount: Int = 0,
        hasFullSchedule: Boolean = false
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
                id = "early_bird",
                title = "Sơn Ca Chăm Chỉ",
                description = "Hoàn thành bài tập trước 8 giờ sáng",
                iconEmoji = "🌅",
                isUnlocked = hasEarlyBirdTask,
                progressText = if (hasEarlyBirdTask) "Đã mở khóa" else "Chưa mở"
            ),
            BadgeItem(
                id = "task_master",
                title = "Bậc Thầy Deadline",
                description = "Hoàn thành 10 nhiệm vụ học tập",
                iconEmoji = "🎯",
                isUnlocked = completedTasksCount >= 10,
                progressText = "$completedTasksCount/10 task"
            ),
            BadgeItem(
                id = "pomo_god",
                title = "Siêu Sao Pomodoro",
                description = "Hoàn thành 10 chu kỳ Pomodoro",
                iconEmoji = "🔥",
                isUnlocked = completedPomodoroSessions >= 10,
                progressText = "$completedPomodoroSessions/10 phiên"
            ),
            BadgeItem(
                id = "gpa_hunter",
                title = "Thợ Săn GPA",
                description = "Theo dõi từ 3 môn học trong Bảng điểm",
                iconEmoji = "📚",
                isUnlocked = subjectCount >= 3,
                progressText = "$subjectCount/3 môn"
            ),
            BadgeItem(
                id = "schedule_master",
                title = "Thánh Quản Lý",
                description = "Lên lịch học đầy đủ cho các ngày trong tuần",
                iconEmoji = "🗓️",
                isUnlocked = hasFullSchedule,
                progressText = if (hasFullSchedule) "Đã mở khóa" else "Chưa đạt"
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

    fun getUnlockedCount(badges: List<BadgeItem>): Int {
        return badges.count { it.isUnlocked }
    }

    fun isLateNightHour(hourOfDay: Int): Boolean {
        return hourOfDay >= 22 || hourOfDay < 4
    }

    fun isEarlyBirdHour(hourOfDay: Int): Boolean {
        return hourOfDay in 4..7
    }
}
