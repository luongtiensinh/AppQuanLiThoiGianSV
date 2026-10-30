package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class GradeRepository(private val gradeDao: GradeDao) {
    fun getAllSubjectsWithGrades(): Flow<List<SubjectWithGrades>> = gradeDao.getAllSubjectsWithGrades()
    
    suspend fun getSubjectWithGradesById(id: Long): SubjectWithGrades? = gradeDao.getSubjectWithGradesById(id)
    
    suspend fun addSubjectWithComponents(subject: SubjectGradeEntity, components: List<GradeComponentEntity>) {
        val subjectId = gradeDao.insertSubject(subject)
        if (components.isNotEmpty()) {
            val componentEntities = components.map { it.copy(subjectId = subjectId) }
            gradeDao.insertComponents(componentEntities)
        }
    }
    
    suspend fun updateSubject(subject: SubjectGradeEntity) = gradeDao.updateSubject(subject)
    
    suspend fun addComponent(component: GradeComponentEntity) = gradeDao.insertComponent(component)
    
    suspend fun updateComponent(component: GradeComponentEntity) = gradeDao.updateComponent(component)
    
    suspend fun deleteSubject(subject: SubjectGradeEntity) = gradeDao.deleteSubject(subject)
    
    suspend fun deleteComponent(componentId: Long) = gradeDao.deleteComponent(componentId)
}
