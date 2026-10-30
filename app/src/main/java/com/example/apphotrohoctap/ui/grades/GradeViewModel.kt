package com.example.apphotrohoctap.ui.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.GradeComponentEntity
import com.example.apphotrohoctap.data.GradeRepository
import com.example.apphotrohoctap.data.SubjectGradeEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class GradeViewModel(private val repository: GradeRepository) : ViewModel() {

    val subjects: StateFlow<List<SubjectGradeUiModel>> = repository.getAllSubjectsWithGrades()
        .map { list -> list.map { it.toUiModel() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSubject(
        subjectName: String,
        credits: Int,
        targetGrade: String,
        components: List<Pair<String, Double>> // Name and Weight
    ) {
        val cleanName = subjectName.trim()
        if (cleanName.isBlank() || credits <= 0) return
        
        viewModelScope.launch {
            val entity = SubjectGradeEntity(
                subjectName = cleanName,
                credits = credits,
                targetGrade = targetGrade
            )
            val componentEntities = components.map { 
                GradeComponentEntity(subjectId = 0, componentName = it.first, weightPercent = it.second, achievedScore = null)
            }
            repository.addSubjectWithComponents(entity, componentEntities)
        }
    }

    fun updateComponentScore(subjectId: Long, componentId: Long, newScore: Double?) {
        if (newScore != null && (newScore < 0.0 || newScore > 10.0)) return
        
        viewModelScope.launch {
            val subjectWithGrades = repository.getSubjectWithGradesById(subjectId) ?: return@launch
            val component = subjectWithGrades.components.find { it.componentId == componentId }
            if (component != null) {
                repository.updateComponent(component.copy(achievedScore = newScore))
            }
        }
    }

    fun deleteSubject(subjectId: Long) {
        viewModelScope.launch {
            val subject = repository.getSubjectWithGradesById(subjectId)
            if (subject != null) {
                repository.deleteSubject(subject.subject)
            }
        }
    }
}
