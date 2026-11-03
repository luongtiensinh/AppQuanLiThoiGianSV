package com.example.apphotrohoctap.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.apphotrohoctap.notification.NotificationHelper

class PomodoroReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val modeName = intent.getStringExtra("MODE_NAME") ?: "FOCUS"
        Log.d("PomodoroReceiver", "Pomodoro finished! Mode: $modeName")
        
        val title = if (modeName == "FOCUS") "Hết giờ tập trung!" else "Hết giờ nghỉ!"
        val message = if (modeName == "FOCUS") {
            "Bạn đã hoàn thành phiên tập trung. Hãy nghỉ ngơi một chút nhé!"
        } else {
            "Đã hết thời gian nghỉ. Sẵn sàng cho phiên tập trung tiếp theo nào!"
        }

        NotificationHelper.sendNotification(
            context = context,
            title = title,
            message = message,
            useAlarmSound = true
        )
    }
}