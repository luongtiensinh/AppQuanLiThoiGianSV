package com.example.apphotrohoctap.data.sync

import com.example.apphotrohoctap.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class SyncResult {
    object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
}

class SyncRepository(private val database: AppDatabase) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun syncWithCloud(): SyncResult = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext SyncResult.Error("Chưa đăng nhập tài khoản Google.")
            val userId = user.uid

            syncCollection(userId, "tasks", database.taskDao()::getAllTasksForSync, database.taskDao()::insertTask, TaskEntity::class.java)
            syncCollection(userId, "subtasks", database.taskDao()::getAllSubtasksForSync, database.taskDao()::insertSubtask, SubtaskEntity::class.java)
            syncCollection(userId, "schedules", database.scheduleDao()::getAllSchedulesForSync, database.scheduleDao()::insertSchedule, ScheduleEntity::class.java)
            syncCollection(userId, "notes", database.noteDao()::getAllNotesForSync, database.noteDao()::insertNote, NoteEntity::class.java)
            syncCollection(userId, "semesters", database.gradeDao()::getAllSemestersForSync, database.gradeDao()::insertSemester, SemesterEntity::class.java)
            syncCollection(userId, "subjects", database.gradeDao()::getAllSubjectsForSync, database.gradeDao()::insertSubject, SubjectGradeEntity::class.java)
            syncCollection(userId, "grade_components", database.gradeDao()::getAllComponentsForSync, database.gradeDao()::insertComponent, GradeComponentEntity::class.java)

            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Lỗi đồng bộ hóa dữ liệu.")
        }
    }

    private suspend inline fun <reified T : Any> syncCollection(
        userId: String,
        collectionName: String,
        crossinline getLocalData: suspend () -> List<T>,
        crossinline saveLocalData: suspend (T) -> Unit,
        clazz: Class<T>
    ) {
        val collectionRef = firestore.collection("users").document(userId).collection(collectionName)

        val snapshot = collectionRef.get().await()
        val remoteList = snapshot.documents.mapNotNull { it.toObject(clazz) }

        val localList = getLocalData()

        val remoteMap = remoteList.associateBy { getSyncId(it) }
        val localMap = localList.associateBy { getSyncId(it) }

        val allSyncIds = remoteMap.keys + localMap.keys

        allSyncIds.forEach { syncId ->
            val remoteItem = remoteMap[syncId]
            val localItem = localMap[syncId]

            if (remoteItem != null && localItem != null) {
                val remoteTime = getUpdatedAt(remoteItem)
                val localTime = getUpdatedAt(localItem)
                
                if (remoteTime > localTime) {
                    saveLocalData(remoteItem) 
                } else if (localTime > remoteTime) {
                    collectionRef.document(syncId).set(localItem).await() 
                }
            } else if (remoteItem != null) {
                saveLocalData(remoteItem)
            } else if (localItem != null) {
                collectionRef.document(syncId).set(localItem).await()
            }
        }
    }

    private fun getSyncId(item: Any): String {
        return item.javaClass.getMethod("getSyncId").invoke(item) as String
    }

    private fun getUpdatedAt(item: Any): Long {
        return item.javaClass.getMethod("getUpdatedAt").invoke(item) as Long
    }
}
