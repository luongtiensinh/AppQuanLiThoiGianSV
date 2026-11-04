package com.example.apphotrohoctap.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.apphotrohoctap.ui.pomodoro.PomodoroMode

object PomodoroReminderManager {

    fun schedule(context: Context, delayMillis: Long, mode: PomodoroMode) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PomodoroReceiver::class.java).apply {
            putExtra("MODE_NAME", mode.name)
        }
        
        val requestCode = mode.name.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + delayMillis
        
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Android 14+ require SCHEDULE_EXACT_ALARM permission
            // If denied, fallback to setWindow or WorkManager, but for now we catch it
        }
    }

    fun cancel(context: Context, mode: PomodoroMode? = null) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Hủy cụ thể một mode nếu được truyền vào, hoặc hủy tất cả nếu không truyền
        val modesToCancel = mode?.let { listOf(it) } ?: PomodoroMode.entries.toList()
        
        for (m in modesToCancel) {
            val intent = Intent(context, PomodoroReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                m.name.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
