package com.example.apphotrohoctap.worker

import android.content.Context
import android.util.Log
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
            val syncRepository = SyncRepository(database, applicationContext)
            
            val result = syncRepository.syncWithCloud()
            
            when (result) {
                is SyncResult.Success -> {
                    Result.success()
                }
                is SyncResult.NotLoggedIn -> {
                    Log.w("SyncWorker", "Chưa đăng nhập, hủy bỏ quá trình Sync ngầm.")
                    Result.failure() // Hủy, không retry vì người dùng chưa login
                }
                is SyncResult.Error -> {
                    Log.e("SyncWorker", "Lỗi đồng bộ ngầm: ${result.message}")
                    Result.retry() // Lỗi mạng / server -> Có thể retry
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Ngoại lệ khi đồng bộ ngầm", e)
            Result.retry()
        }
    }
}
