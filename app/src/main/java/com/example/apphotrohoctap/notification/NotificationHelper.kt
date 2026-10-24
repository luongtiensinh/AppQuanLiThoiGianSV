package com.example.apphotrohoctap.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.apphotrohoctap.MainActivity
import com.example.apphotrohoctap.R

object NotificationHelper {
    const val CHANNEL_ID = "studyflow_deadline_v4"
    const val ALARM_CHANNEL_ID = "studyflow_alarm_v4"
    const val NOTIFICATION_ID = 1001

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val oldChannels = listOf("deadline_channel", "deadline_channel_v2", "deadline_alarm_channel", "studyflow_deadline_v3", "studyflow_alarm_v3")
            oldChannels.forEach { id ->
                try {
                    notificationManager.deleteNotificationChannel(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Kênh 1: Âm thanh thông báo thường
            val normalChannel = NotificationChannel(
                CHANNEL_ID,
                "Nhắc nhở Deadline (Thông báo)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo nhắc nhở bài tập với âm thanh thông báo chuẩn"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(normalChannel)

            // Kênh 2: Âm thanh báo thức (Alarm)
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val alarmAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Nhắc nhở Deadline (Âm thanh Báo thức)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh thông báo nhắc nhở bài tập phát âm thanh báo thức"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 400, 800, 400, 800)
                enableLights(true)
                setSound(alarmSound, alarmAttributes)
            }
            notificationManager.createNotificationChannel(alarmChannel)
            
            // Kênh 3: Pomodoro Timer
            val pomodoroChannel = NotificationChannel(
                "pomodoro_channel",
                "Pomodoro",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo khi kết thúc phiên Pomodoro"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(pomodoroChannel)
        }
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        useAlarmSound: Boolean = true,
        playDirectSound: Boolean = false
    ): Boolean {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = if (useAlarmSound) ALARM_CHANNEL_ID else CHANNEL_ID
        val soundType = if (useAlarmSound) RingtoneManager.TYPE_ALARM else RingtoneManager.TYPE_NOTIFICATION
        val soundUri = RingtoneManager.getDefaultUri(soundType)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val vibratePattern = if (useAlarmSound) {
            longArrayOf(0, 800, 400, 800, 400, 800)
        } else {
            longArrayOf(0, 500, 250, 500)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(if (useAlarmSound) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(soundUri)
            .setVibrate(vibratePattern)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notificationSent = false
        try {
            manager.notify(NOTIFICATION_ID, builder.build())
            notificationSent = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (playDirectSound) {
            playRingtoneAndVibrate(context, soundUri)
        }

        return notificationSent || playDirectSound
    }

    private fun playRingtoneAndVibrate(context: Context, soundUri: Uri?) {
        try {
            val uri = soundUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 800, 400, 800), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 800, 400, 800), -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
