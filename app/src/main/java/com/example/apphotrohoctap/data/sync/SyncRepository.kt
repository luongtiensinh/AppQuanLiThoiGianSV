package com.example.apphotrohoctap.data.sync

import android.content.Context
import com.example.apphotrohoctap.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class SyncResult {
    object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
}

class SyncRepository(private val database: AppDatabase, private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val batchMutex = Mutex()

    suspend fun syncWithCloud(): SyncResult = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext SyncResult.Error("Chưa đăng nhập tài khoản Google.")
            val userId = user.uid

            val prefs = context.getSharedPreferences("studyflow_sync_prefs", Context.MODE_PRIVATE)
            val lastSyncTime = prefs.getLong("last_sync_time", 0L)
            val currentSyncTime = System.currentTimeMillis()

            val writeBatch = firestore.batch()

            // Run concurrently with async/awaitAll
            awaitAll(
                async { processSync(userId, "tasks", database.taskDao()::getModifiedTasks, database.taskDao()::insertTaskMany, TaskEntity::class.java, lastSyncTime, writeBatch) },
                async { processSync(userId, "subtasks", database.taskDao()::getModifiedSubtasks, database.taskDao()::insertSubtaskMany, SubtaskEntity::class.java, lastSyncTime, writeBatch) },
                async { processSync(userId, "schedules", database.scheduleDao()::getModifiedSchedules, database.scheduleDao()::insertScheduleMany, ScheduleEntity::class.java, lastSyncTime, writeBatch) },
                async { processSync(userId, "notes", database.noteDao()::getModifiedNotes, database.noteDao()::insertNoteMany, NoteEntity::class.java, lastSyncTime, writeBatch) },
                async { processSync(userId, "semesters", database.gradeDao()::getModifiedSemesters, database.gradeDao()::insertSemesterMany, SemesterEntity::class.java, lastSyncTime, writeBatch) },
                async { processSync(userId, "subjects", database.gradeDao()::getModifiedSubjects, database.gradeDao()::insertSubjectMany, SubjectGradeEntity::class.java, lastSyncTime, writeBatch) },
                async { processSync(userId, "grade_components", database.gradeDao()::getModifiedComponents, database.gradeDao()::insertComponentMany, GradeComponentEntity::class.java, lastSyncTime, writeBatch) }
            )

            // Commit all network writes in a single batch
            writeBatch.commit().await()
            
            // Save successful sync timestamp
            prefs.edit().putLong("last_sync_time", currentSyncTime).apply()

            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Lỗi đồng bộ hóa dữ liệu.")
        }
    }

    private suspend inline fun <reified T : Any> processSync(
        userId: String,
        collectionName: String,
        crossinline getLocalData: suspend (Long) -> List<T>,
        crossinline saveLocalData: suspend (List<T>) -> Unit,
        clazz: Class<T>,
        lastSyncTime: Long,
        writeBatch: WriteBatch
    ) {
        val collectionRef = firestore.collection("users").document(userId).collection(collectionName)

        // Delta Fetch from Firebase
        val snapshot = collectionRef.whereGreaterThan("updatedAt", lastSyncTime).get().await()
        val remoteList = snapshot.documents.mapNotNull { it.toObject(clazz) }

        // Delta Fetch from Local
        val localList = getLocalData(lastSyncTime)

        val remoteMap = remoteList.associateBy { getSyncId(it) }
        val localMap = localList.associateBy { getSyncId(it) }

        val allSyncIds = remoteMap.keys + localMap.keys
        val localUpdates = mutableListOf<T>()

        allSyncIds.forEach { syncId ->
            val remoteItem = remoteMap[syncId]
            val localItem = localMap[syncId]

            if (remoteItem != null && localItem != null) {
                val remoteTime = getUpdatedAt(remoteItem)
                val localTime = getUpdatedAt(localItem)
                
                if (remoteTime > localTime) {
                    localUpdates.add(remoteItem) 
                } else if (localTime > remoteTime) {
                    batchMutex.withLock {
                        writeBatch.set(collectionRef.document(syncId), localItem) 
                    }
                }
            } else if (remoteItem != null) {
                localUpdates.add(remoteItem)
            } else if (localItem != null) {
                batchMutex.withLock {
                    writeBatch.set(collectionRef.document(syncId), localItem)
                }
            }
        }
        
        if (localUpdates.isNotEmpty()) {
            saveLocalData(localUpdates)
        }
    }

    private fun getSyncId(item: Any): String {
        return item.javaClass.getMethod("getSyncId").invoke(item) as String
    }

    private fun getUpdatedAt(item: Any): Long {
        return item.javaClass.getMethod("getUpdatedAt").invoke(item) as Long
    }
}
