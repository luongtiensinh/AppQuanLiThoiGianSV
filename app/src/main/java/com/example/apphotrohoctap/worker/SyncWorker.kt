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
            
            if (result is SyncResult.Success) {
                Result.success()
            } else {
                val errorMsg = (result as SyncResult.Error).message
                Log.e("SyncWorker", "Lỗi đồng bộ ngầm: $errorMsg")
                
                // Nếu lỗi là do chưa đăng nhập (người dùng mới tải app, hoặc đã log out)
                // thì KHÔNG NÊN báo retry (thử lại) vì nó sẽ treo hệ thống WorkManager vô ích.
                if (errorMsg.contains("Chưa đăng nhập")) {
                    Result.failure()
                } else {
                    // Return retry so WorkManager will automatically retry on network/server failure
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Ngoại lệ khi đồng bộ ngầm", e)
            Result.retry()
        }
    }
}
