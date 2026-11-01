package com.example.apphotrohoctap.ui.settings

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import com.example.apphotrohoctap.worker.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isDeadlineReminderEnabled = MutableStateFlow(false)
    val isDeadlineReminderEnabled: StateFlow<Boolean> = _isDeadlineReminderEnabled.asStateFlow()

    init {
        checkAndSyncNotificationState()
    }

    fun checkAndSyncNotificationState() {
        val app = getApplication<Application>()
        val savedState = prefs.getBoolean("deadlineReminder", false)
        val systemNotificationsEnabled = NotificationManagerCompat.from(app).areNotificationsEnabled()

        // Đồng bộ trạng thái thực tế giữa cài đặt app và quyền hệ thống
        val finalState = savedState && systemNotificationsEnabled
        _isDeadlineReminderEnabled.value = finalState
        if (finalState != savedState) {
            prefs.edit().putBoolean("deadlineReminder", finalState).apply()
        }
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

    fun triggerTestReminder() {
        ReminderManager.testOneTimeReminder(getApplication())
    }
}
