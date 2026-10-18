package com.example.apphotrohoctap

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class StudyFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Tạo Notification Channel cho Android 8.0 (API 26) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "deadline_channel",
                "Nhắc nhở Deadline",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Kênh thông báo nhắc nhở bài tập sắp tới hạn"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
