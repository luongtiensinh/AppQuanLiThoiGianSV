package com.example.apphotrohoctap.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    // --- Semester ---
    @Query("SELECT * FROM semesters ORDER BY semesterId DESC")
    fun getAllSemesters(): Flow<List<SemesterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity): Long

    @Update
    suspend fun updateSemester(semester: SemesterEntity)

    @Delete
    suspend fun deleteSemester(semester: SemesterEntity)

    @Query("SELECT * FROM semesters WHERE semesterId = :id")
    suspend fun getSemesterById(id: Long): SemesterEntity?

    @Query("UPDATE semesters SET isCurrent = 0")
    suspend fun clearCurrentSemester()

    @Query("UPDATE semesters SET isCurrent = 1 WHERE semesterId = :id")
    suspend fun setCurrentSemester(id: Long)

    @Query("SELECT * FROM semesters WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentSemester(): SemesterEntity?

    // --- Subject & Components ---
    @Transaction
    @Query("SELECT * FROM subject_grades WHERE semesterId = :semesterId ORDER BY subjectName ASC")
    fun getSubjectsWithGradesBySemester(semesterId: Long): Flow<List<SubjectWithGrades>>
    
    @Transaction
    @Query("SELECT * FROM subject_grades ORDER BY subjectName ASC")
    fun getAllSubjectsWithGrades(): Flow<List<SubjectWithGrades>>

    @Transaction
    @Query("SELECT * FROM subject_grades WHERE subjectId = :id")
    suspend fun getSubjectWithGradesById(id: Long): SubjectWithGrades?

    @Transaction
    @Query("SELECT * FROM subject_grades WHERE subjectId = :id")
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

    @Delete
    suspend fun deleteSubject(subject: SubjectGradeEntity)

    @Query("DELETE FROM grade_components WHERE componentId = :componentId")
    suspend fun deleteComponent(componentId: Long)
}
