package com.example.apphotrohoctap.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.apphotrohoctap.ui.pomodoro.PomodoroMode
import java.util.concurrent.TimeUnit

object PomodoroReminderManager {
    private const val WORK_NAME = "POMODORO_FINISH"

    fun schedule(context: Context, delayMillis: Long, mode: PomodoroMode) {
        val inputData = Data.Builder()
            .putString("MODE_NAME", mode.name)
            .build()

        val request = OneTimeWorkRequestBuilder<PomodoroFinishWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE, // Ghi đè nếu có schedule trước đó
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
