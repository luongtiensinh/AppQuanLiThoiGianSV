package com.example.apphotrohoctap.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.sync.SyncRepository
import com.example.apphotrohoctap.data.sync.SyncResult

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val syncRepository = SyncRepository(database)
            
            val result = syncRepository.syncWithCloud()
            
            if (result is SyncResult.Success) {
                Result.success()
            } else {
                // Return retry so WorkManager will automatically retry on failure (network issue, etc)
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
