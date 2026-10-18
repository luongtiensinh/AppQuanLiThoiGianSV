package com.example.apphotrohoctap.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderManager {
    private const val WORK_NAME = "DEADLINE_REMINDER_WORK"

    // Hàm gọi Worker chạy định kỳ (mỗi 15 phút, mức tối thiểu của WorkManager)
    fun startPeriodicReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<DeadlineReminderWorker>(15, TimeUnit.MINUTES)
            .build()
            
        // enqueueUniquePeriodicWork đảm bảo chỉ có 1 job chạy ngầm dù gọi nhiều lần
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    // Tắt chạy ngầm
    fun stopPeriodicReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    // Dùng để Test nhanh xem Notification có hiện lên không
    fun testOneTimeReminder(context: Context) {
        val request = OneTimeWorkRequestBuilder<DeadlineReminderWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "TEST_REMINDER",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
