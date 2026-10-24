package com.example.apphotrohoctap.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.apphotrohoctap.notification.NotificationHelper
import com.example.apphotrohoctap.worker.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isDeadlineReminderEnabled = MutableStateFlow(true)
    val isDeadlineReminderEnabled: StateFlow<Boolean> = _isDeadlineReminderEnabled.asStateFlow()

    private val _useAlarmSound = MutableStateFlow(true)
    val useAlarmSound: StateFlow<Boolean> = _useAlarmSound.asStateFlow()

    init {
        _isDeadlineReminderEnabled.value = prefs.getBoolean("deadlineReminder", true)
        _useAlarmSound.value = prefs.getBoolean("useAlarmSound", true)
    }

    fun toggleDeadlineReminder(enabled: Boolean) {
        _isDeadlineReminderEnabled.value = enabled
        prefs.edit().putBoolean("deadlineReminder", enabled).apply()
        val app = getApplication<Application>()
        if (enabled) {
            ReminderManager.startPeriodicReminder(app)
        } else {
            ReminderManager.stopPeriodicReminder(app)
        }
    }

    fun toggleUseAlarmSound(enabled: Boolean) {
        _useAlarmSound.value = enabled
        prefs.edit().putBoolean("useAlarmSound", enabled).apply()
    }

    fun triggerTestReminder(context: Context): Boolean {
        if (!_isDeadlineReminderEnabled.value) {
            toggleDeadlineReminder(true)
        }
        val useAlarm = _useAlarmSound.value
        val posted = NotificationHelper.sendNotification(
            context = context,
            title = "StudyFlow - Nhắc nhở Thử nghiệm!",
            message = "Đây là thông báo thử nghiệm! Kiểm tra âm thanh báo thức và chuông rung.",
            useAlarmSound = useAlarm,
            playDirectSound = true
        )
        ReminderManager.testOneTimeReminder(getApplication())
        return posted
    }
}
