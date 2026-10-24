package com.example.apphotrohoctap.worker

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apphotrohoctap.MainActivity
import com.example.apphotrohoctap.R
import com.example.apphotrohoctap.StudyFlowApplication
import com.example.apphotrohoctap.ui.pomodoro.PomodoroMode

class PomodoroFinishWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Kiểm tra xem ứng dụng có đang ở Foreground không (hiển thị cho người dùng)
        val app = context.applicationContext as? StudyFlowApplication
        if (app != null && app.isAppInForeground) {
            // App đang mở -> Không bắn notification, để ViewModel / Compose tự mở Dialog
            return Result.success()
        }

        // Nếu app đang ẩn ở background, ta bắn Notification.
        // Cần kiểm tra quyền POST_NOTIFICATIONS (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure()
        }

        val modeName = inputData.getString("MODE_NAME") ?: PomodoroMode.FOCUS.name
        
        val title: String
        val text: String
        
        if (modeName == PomodoroMode.FOCUS.name) {
            title = "Hết giờ tập trung!"
            text = "Bạn làm tốt lắm. Nghỉ 5 phút nhé?"
        } else {
            title = "Hết giờ nghỉ!"
            text = "Nghỉ ngơi đủ rồi, quay lại học nào!"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            9999, 
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "pomodoro_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2002, builder.build())

        return Result.success()
    }
}
