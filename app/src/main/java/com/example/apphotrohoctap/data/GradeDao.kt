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
    suspend fun setCurrentSemester(id: Long)

    @Query("SELECT * FROM semesters WHERE isDeleted = 0 AND isCurrent = 1 LIMIT 1")
    suspend fun getCurrentSemester(): SemesterEntity?

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

    @Query("UPDATE grade_components SET isDeleted = 1, updatedAt = :timestamp WHERE componentId = :componentId")
    suspend fun softDeleteComponent(componentId: Long, timestamp: Long)

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
