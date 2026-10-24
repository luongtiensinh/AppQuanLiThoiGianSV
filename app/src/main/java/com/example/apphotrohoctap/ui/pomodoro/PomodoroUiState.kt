package com.example.apphotrohoctap.ui.pomodoro

enum class PomodoroStatus {
    IDLE,
    RUNNING,
    PAUSED
}

data class PomodoroUiState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val status: PomodoroStatus = PomodoroStatus.IDLE,
    val remainingMillis: Long = PomodoroMode.FOCUS.defaultDurationMillis,
    val totalMillis: Long = PomodoroMode.FOCUS.defaultDurationMillis,
    val completedFocusSessions: Int = 0,
    val linkedTaskTitle: String? = null
)
