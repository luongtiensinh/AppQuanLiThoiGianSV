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
    @Transaction
    @Query("SELECT * FROM subject_grades ORDER BY subjectName ASC")
    fun getAllSubjectsWithGrades(): Flow<List<SubjectWithGrades>>

    @Transaction
    @Query("SELECT * FROM subject_grades WHERE subjectId = :id")
    suspend fun getSubjectWithGradesById(id: Long): SubjectWithGrades?

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
