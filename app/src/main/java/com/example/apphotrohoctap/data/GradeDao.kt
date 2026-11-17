package com.example.apphotrohoctap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    // --- Semester ---
    @Query("SELECT * FROM semesters WHERE isDeleted = 0 ORDER BY semesterId DESC")
    fun getAllSemesters(): Flow<List<SemesterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity): Long

    @Update
    suspend fun updateSemester(semester: SemesterEntity)

    @Query("UPDATE semesters SET isDeleted = 1, updatedAt = :timestamp WHERE semesterId = :semesterId")
    suspend fun softDeleteSemester(semesterId: Long, timestamp: Long)

    @Query("SELECT * FROM semesters WHERE isDeleted = 0 AND semesterId = :id")
    suspend fun getSemesterById(id: Long): SemesterEntity?

    @Query("UPDATE semesters SET isCurrent = 0")
    suspend fun clearCurrentSemester()

    @Query("UPDATE semesters SET isCurrent = 1 WHERE semesterId = :id")
    suspend fun markSemesterAsCurrent(id: Long)

    @Transaction
    suspend fun setCurrentSemesterTransactional(id: Long) {
        clearCurrentSemester()
        markSemesterAsCurrent(id)
    }

    @Query("SELECT * FROM semesters WHERE isDeleted = 0 AND isCurrent = 1 LIMIT 1")
    suspend fun getCurrentSemester(): SemesterEntity?
    
    @Query("SELECT * FROM semesters WHERE isDeleted = 0 ORDER BY semesterId DESC LIMIT 1")
    suspend fun getLatestSemester(): SemesterEntity?

    // --- Subject & Components ---
    @Transaction
    @Query("SELECT * FROM subject_grades WHERE isDeleted = 0 AND semesterId = :semesterId ORDER BY subjectName ASC")
    fun getSubjectsWithGradesBySemester(semesterId: Long): Flow<List<SubjectWithGrades>>
    
    @Transaction
    @Query("SELECT * FROM subject_grades WHERE isDeleted = 0 ORDER BY subjectName ASC")
    fun getAllSubjectsWithGrades(): Flow<List<SubjectWithGrades>>

    @Transaction
    @Query("SELECT * FROM subject_grades WHERE isDeleted = 0 AND subjectId = :id")
    suspend fun getSubjectWithGradesById(id: Long): SubjectWithGrades?

    @Transaction
    @Query("SELECT * FROM subject_grades WHERE isDeleted = 0 AND subjectId = :id")
    fun getSubjectWithGradesFlowById(id: Long): Flow<SubjectWithGrades?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectGradeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponents(components: List<GradeComponentEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: GradeComponentEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectGradeEntity)

    @Update
    suspend fun updateComponent(component: GradeComponentEntity)

    @Query("UPDATE subject_grades SET isDeleted = 1, updatedAt = :timestamp WHERE subjectId = :subjectId")
    suspend fun softDeleteSubject(subjectId: Long, timestamp: Long)

    @Query("UPDATE subject_grades SET isDeleted = 1, updatedAt = :timestamp WHERE semesterId = :semesterId")
    suspend fun softDeleteSubjectsBySemester(semesterId: Long, timestamp: Long)

    @Query("UPDATE grade_components SET isDeleted = 1, updatedAt = :timestamp WHERE componentId = :componentId")
    suspend fun softDeleteComponent(componentId: Long, timestamp: Long)

    @Query("UPDATE grade_components SET isDeleted = 1, updatedAt = :timestamp WHERE subjectId = :subjectId")
    suspend fun softDeleteComponentsBySubject(subjectId: Long, timestamp: Long)

    @Query("UPDATE grade_components SET isDeleted = 1, updatedAt = :timestamp WHERE subjectId IN (SELECT subjectId FROM subject_grades WHERE semesterId = :semesterId)")
    suspend fun softDeleteComponentsBySemester(semesterId: Long, timestamp: Long)

    @Transaction
    suspend fun updateSemesterSafe(semester: SemesterEntity) {
        if (semester.isCurrent) {
            clearCurrentSemester()
        }
        updateSemester(semester)
        
        // Chỉ auto-fallback nếu học kỳ hiện tại bị mất do người dùng tắt cờ.
        // Cần đảm bảo nếu không có học kỳ nào, hoặc user chủ động bỏ chọn,
        // thì hệ thống vẫn gán lại một cái nếu có. (Yêu cầu business logic: luôn có 1 current).
        if (getCurrentSemester() == null) {
            getLatestSemester()?.let { markSemesterAsCurrent(it.semesterId) }
        }
    }
    
    @Transaction
    suspend fun insertSemesterSafe(semester: SemesterEntity): Long {
        if (semester.isCurrent) {
            clearCurrentSemester()
        }
        val id = insertSemester(semester)
        
        // Nếu user tạo semester mới nhưng isCurrent = false,
        // và hệ thống đang KHÔNG CÓ current semester nào,
        // thì KHÔNG TỰ ĐỘNG fallback. Tôn trọng quyết định tạo học kỳ không-current của user.
        // Do đó, xóa auto-fallback ở hàm insert.
        return id
    }

    @Transaction
    suspend fun softDeleteSemesterCascade(semesterId: Long, timestamp: Long) {
        val semesterToDelete = getSemesterById(semesterId)
        if (semesterToDelete?.isCurrent == true) {
            clearCurrentSemester()
        }
        
        softDeleteComponentsBySemester(semesterId, timestamp)
        softDeleteSubjectsBySemester(semesterId, timestamp)
        softDeleteSemester(semesterId, timestamp)
        
        // Auto-fallback khi XÓA học kỳ hiện tại là cần thiết
        if (getCurrentSemester() == null) {
            getLatestSemester()?.let { markSemesterAsCurrent(it.semesterId) }
        }
    }

    @Transaction
    suspend fun softDeleteSubjectCascade(subjectId: Long, timestamp: Long) {
        softDeleteComponentsBySubject(subjectId, timestamp)
        softDeleteSubject(subjectId, timestamp)
    }

    @Query("SELECT * FROM semesters WHERE updatedAt > :lastSyncTime")
    suspend fun getModifiedSemesters(lastSyncTime: Long): List<SemesterEntity>

    @Query("SELECT * FROM subject_grades WHERE updatedAt > :lastSyncTime")
    suspend fun getModifiedSubjects(lastSyncTime: Long): List<SubjectGradeEntity>

    @Query("SELECT * FROM grade_components WHERE updatedAt > :lastSyncTime")
    suspend fun getModifiedComponents(lastSyncTime: Long): List<GradeComponentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesterMany(semesters: List<SemesterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjectMany(subjects: List<SubjectGradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponentMany(components: List<GradeComponentEntity>)
}
