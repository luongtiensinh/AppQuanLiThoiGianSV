package com.example.apphotrohoctap.data.backup

import com.example.apphotrohoctap.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class BackupResult {
    object Success : BackupResult()
    data class Error(val message: String) : BackupResult()
}

class BackupRepository(private val database: AppDatabase) {
    private val auth = FirebaseAuth.getInstance()
    private val rtdb = FirebaseDatabase.getInstance().reference
    private val gson = Gson()

    suspend fun backupDataToCloud(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext BackupResult.Error("Chưa đăng nhập tài khoản Google.")

            val tasksWithSub = database.taskDao().getAllTasks().first()
            val tasks = tasksWithSub.map { it.task }
            val subtasks = tasksWithSub.flatMap { it.subtasks }

            val schedules = database.scheduleDao().getAllSchedules().first()
            val notes = database.noteDao().getAllNotes().first()

            val subjectsWithGrades = database.gradeDao().getAllSubjectsWithGrades().first()
            val subjects = subjectsWithGrades.map { it.subject }
            val components = subjectsWithGrades.flatMap { it.components }

            val model = BackupModel(
                tasks = tasks,
                subtasks = subtasks,
                schedules = schedules,
                notes = notes,
                subjects = subjects,
                gradeComponents = components
            )

            val jsonString = gson.toJson(model)
            val userUid = user.uid

            rtdb.child("user_backups").child(userUid).setValue(jsonString).await()

            BackupResult.Success
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Lỗi sao lưu không xác định.")
        }
    }

    suspend fun restoreDataFromCloud(): BackupResult = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext BackupResult.Error("Chưa đăng nhập tài khoản Google.")
            val userUid = user.uid

            val snapshot = rtdb.child("user_backups").child(userUid).get().await()
            val jsonString = snapshot.getValue(String::class.java)

            if (jsonString.isNullOrBlank()) {
                return@withContext BackupResult.Error("Không tìm thấy bản sao lưu nào trên đám mây.")
            }

            val model = gson.fromJson(jsonString, BackupModel::class.java)

            // Xóa sạch dữ liệu cũ trong Room
            database.clearAllTables()

            // Restore Tasks & Subtasks
            model.tasks.forEach { task ->
                val newId = database.taskDao().insertTask(task)
                val relatedSubtasks = model.subtasks.filter { it.taskId == task.id }
                if (relatedSubtasks.isNotEmpty()) {
                    val remapped = relatedSubtasks.map { it.copy(taskId = newId) }
                    database.taskDao().insertSubtasks(remapped)
                }
            }

            // Restore Schedules
            model.schedules.forEach { schedule ->
                database.scheduleDao().insertSchedule(schedule)
            }

            // Restore Notes
            model.notes.forEach { note ->
                database.noteDao().insertNote(note)
            }

            // Restore Subjects & GradeComponents
            model.subjects.forEach { subject ->
                val newSubjectId = database.gradeDao().insertSubject(subject)
                val relatedComponents = model.gradeComponents.filter { it.subjectId == subject.subjectId }
                if (relatedComponents.isNotEmpty()) {
                    val remapped = relatedComponents.map { it.copy(subjectId = newSubjectId) }
                    database.gradeDao().insertComponents(remapped)
                }
            }

            BackupResult.Success
        } catch (e: Exception) {
            BackupResult.Error(e.message ?: "Lỗi phục hồi không xác định.")
        }
    }
}
