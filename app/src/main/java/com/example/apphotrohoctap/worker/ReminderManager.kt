package com.example.apphotrohoctap.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderManager {
    private const val WORK_NAME = "DEADLINE_REMINDER_WORK"
    private const val SYNC_WORK_NAME = "BACKGROUND_SYNC_WORK"

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
        val request = OneTimeWorkRequestBuilder<DeadlineReminderWorker>()
            .addTag("TEST_WORK")
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "TEST_REMINDER",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // --- CẤU HÌNH ĐỒNG BỘ BACKGROUND ---
    fun startBackgroundSync(context: Context) {
        // Chỉ chạy Sync khi có mạng
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Sync mỗi 15 phút
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Không ghi đè nếu đã chạy
            request
        )
    }

    fun stopBackgroundSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }
}
