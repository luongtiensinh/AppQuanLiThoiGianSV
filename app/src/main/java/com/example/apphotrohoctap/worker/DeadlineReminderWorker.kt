package com.example.apphotrohoctap.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.notification.NotificationHelper

class DeadlineReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(context)
            val now = System.currentTimeMillis()
            val next24h = now + 24 * 60 * 60 * 1000L

            // Kiểm tra mốc thời gian báo gần nhất để tránh spam thông báo mỗi 15 phút (Cooldown 4 tiếng)
            val prefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            val lastNotifiedAt = prefs.getLong("last_notified_at", 0L)
            val isTestRun = tags.contains("TEST_WORK")
            val cooldownMillis = 4 * 60 * 60 * 1000L // 4 tiếng

            if (!isTestRun && (now - lastNotifiedAt < cooldownMillis)) {
                return Result.success()
            }

            // Tìm các task chưa hoàn thành có hạn nộp trong 24 giờ tới (Đã được ORDER BY dueAtMillis ASC)
            val upcomingTasks = db.taskDao().getUpcomingDeadlines(now, next24h)

            if (upcomingTasks.isNotEmpty() || isTestRun) {
                val message = if (upcomingTasks.isNotEmpty()) {
                    val count = upcomingTasks.size
                    val firstTaskTitle = upcomingTasks.first().task.title
                    if (count == 1) {
                        "Bạn có 1 bài tập sắp đến hạn: $firstTaskTitle"
                    } else {
                        "Bạn có $count bài tập cần hoàn thành trong 24 giờ tới (Gần nhất: $firstTaskTitle)."
                    }
                } else {
                    "Đây là thông báo thử nghiệm nhắc nhở deadline từ StudyFlow!"
                }

                val settingsPrefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                val useAlarmSound = settingsPrefs.getBoolean("useAlarmSound", true)

                NotificationHelper.sendNotification(
                    context = context,
                    title = "StudyFlow - Nhắc nhở Deadline!",
                    message = message,
                    useAlarmSound = useAlarmSound
                )

                if (!isTestRun) {
                    // Cập nhật mốc thời gian đã gửi thông báo thực tế
                    prefs.edit().putLong("last_notified_at", now).apply()
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
