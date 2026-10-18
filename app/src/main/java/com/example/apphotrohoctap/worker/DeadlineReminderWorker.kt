package com.example.apphotrohoctap.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apphotrohoctap.MainActivity
import com.example.apphotrohoctap.R
import com.example.apphotrohoctap.data.AppDatabase

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

            if (upcomingTasks.isNotEmpty()) {
                val count = upcomingTasks.size
                val firstTaskTitle = upcomingTasks.first().task.title
                
                val message = if (count == 1) {
                    "Bạn có 1 bài tập sắp đến hạn: $firstTaskTitle"
                } else {
                    "Bạn có $count bài tập cần hoàn thành trong 24 giờ tới (Gần nhất: $firstTaskTitle)."
                }
                showNotification(message)
                
                // Cập nhật mốc thời gian đã gửi thông báo
                prefs.edit().putLong("last_notified_at", now).apply()
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun showNotification(message: String) {
        // Tạo Intent mở MainActivity khi bấm vào Notification
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Xây dựng Notification
        val notification = NotificationCompat.Builder(context, "deadline_channel")
            .setSmallIcon(R.mipmap.ic_launcher) // Thay bằng logo nhỏ nhắn nếu có icon riêng
            .setContentTitle("StudyFlow - Nhắc nhở Deadline!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Hỗ trợ text dài
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Hiển thị Notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, notification)
    }
}
