package com.example.apphotrohoctap

import android.app.Application
import android.os.Bundle
import android.app.Activity
import com.example.apphotrohoctap.notification.NotificationHelper
import com.example.apphotrohoctap.worker.ReminderManager

class StudyFlowApplication : Application(), Application.ActivityLifecycleCallbacks {
    
    var startedActivityCount = 0
        private set
        
    val isAppInForeground: Boolean
        get() = startedActivityCount > 0

    override fun onCreate() {
        super.onCreate()
        
        // Đăng ký theo dõi vòng đời Activity để biết app đang ở Foreground hay Background
        registerActivityLifecycleCallbacks(this)
        
        // Khởi tạo kênh thông báo với độ ưu tiên cao & âm thanh báo thức
        NotificationHelper.initChannels(this)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isReminderEnabled = prefs.getBoolean("deadlineReminder", true)
        if (!prefs.contains("deadlineReminder")) {
            prefs.edit().putBoolean("deadlineReminder", true).apply()
        }
        if (isReminderEnabled) {
            ReminderManager.startPeriodicReminder(this)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) { startedActivityCount++ }
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) { startedActivityCount-- }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
