package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class GradeRepository(private val gradeDao: GradeDao) {
    // --- Semester ---
    fun getAllSemesters(): Flow<List<SemesterEntity>> = gradeDao.getAllSemesters()
    
    suspend fun addSemester(semester: SemesterEntity): Long {
        if (semester.isCurrent) {
            gradeDao.clearCurrentSemester()
        }
        return gradeDao.insertSemester(semester)
    }

    suspend fun updateSemester(semester: SemesterEntity) {
        if (semester.isCurrent) {
            gradeDao.clearCurrentSemester()
        }
        gradeDao.updateSemester(semester)
    }

    suspend fun deleteSemester(semester: SemesterEntity) = gradeDao.deleteSemester(semester)
    
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
