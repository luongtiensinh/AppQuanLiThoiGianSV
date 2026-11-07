package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class GradeRepository(private val gradeDao: GradeDao) {
    // --- Semester ---
    fun getAllSemesters(): Flow<List<SemesterEntity>> = gradeDao.getAllSemesters()
    
    suspend fun addSemester(semester: SemesterEntity): Long {
        if (semester.isCurrent) {
            gradeDao.clearCurrentSemester()
        }
        return gradeDao.insertSemester(semester.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateSemester(semester: SemesterEntity) {
        if (semester.isCurrent) {
            gradeDao.clearCurrentSemester()
        }
        gradeDao.updateSemester(semester.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteSemester(semesterId: Long) = gradeDao.softDeleteSemester(semesterId, System.currentTimeMillis())
    
    suspend fun setCurrentSemester(id: Long) {
        gradeDao.clearCurrentSemester()
        gradeDao.setCurrentSemester(id)
    }
    
    suspend fun getCurrentSemester(): SemesterEntity? = gradeDao.getCurrentSemester()

    // --- Subject & Components ---
    fun getSubjectsWithGradesBySemester(semesterId: Long): Flow<List<SubjectWithGrades>> = 
        gradeDao.getSubjectsWithGradesBySemester(semesterId)
        
    fun getAllSubjectsWithGrades(): Flow<List<SubjectWithGrades>> = gradeDao.getAllSubjectsWithGrades()
    
    suspend fun getSubjectWithGradesById(id: Long): SubjectWithGrades? = gradeDao.getSubjectWithGradesById(id)
    
    fun getSubjectWithGradesFlowById(id: Long): Flow<SubjectWithGrades?> = gradeDao.getSubjectWithGradesFlowById(id)
    
    suspend fun addSubjectWithComponents(subject: SubjectGradeEntity, components: List<GradeComponentEntity>) {
        val subjectId = gradeDao.insertSubject(subject.copy(updatedAt = System.currentTimeMillis()))
        if (components.isNotEmpty()) {
            val componentEntities = components.map { it.copy(subjectId = subjectId, updatedAt = System.currentTimeMillis()) }
            gradeDao.insertComponents(componentEntities)
        }
    }
    
    suspend fun updateSubject(subject: SubjectGradeEntity) = gradeDao.updateSubject(subject.copy(updatedAt = System.currentTimeMillis()))
    
    suspend fun addComponent(component: GradeComponentEntity) = gradeDao.insertComponent(component.copy(updatedAt = System.currentTimeMillis()))
    
    suspend fun updateComponent(component: GradeComponentEntity) = gradeDao.updateComponent(component.copy(updatedAt = System.currentTimeMillis()))
    
    suspend fun deleteSubject(subjectId: Long) = gradeDao.softDeleteSubject(subjectId, System.currentTimeMillis())
    
    suspend fun deleteComponent(componentId: Long) = gradeDao.softDeleteComponent(componentId, System.currentTimeMillis())
}
