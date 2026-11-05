package com.example.apphotrohoctap.ui.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.GradeComponentEntity
import com.example.apphotrohoctap.data.GradeRepository
import com.example.apphotrohoctap.data.SemesterEntity
import com.example.apphotrohoctap.data.SubjectGradeEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class GradeViewModel(private val repository: GradeRepository) : ViewModel() {

    private val _selectedSemesterId = MutableStateFlow<Long?>(null)
    val selectedSemesterId: StateFlow<Long?> = _selectedSemesterId

    // 1. Quản lý danh sách học kỳ
    val semesters: StateFlow<List<SemesterUiModel>> = combine(
        repository.getAllSemesters(),
        repository.getAllSubjectsWithGrades()
    ) { semesterList, allSubjects ->
        semesterList.map { semester ->
            // Tính toán GPA và tổng tín chỉ cho từng học kỳ
            val subjectsInSemester = allSubjects.filter { it.subject.semesterId == semester.semesterId }
            
            var totalCredits = 0
            var totalGpaPoints = 0.0
            
            subjectsInSemester.forEach { swg ->
                val uiModel = swg.toUiModel()
                if (uiModel.isCompleted && uiModel.finalLetterGrade != null) {
                    val gpa4 = GradeCalculator.gradeToGpa4(uiModel.finalLetterGrade)
                    totalCredits += uiModel.credits
                    totalGpaPoints += gpa4 * uiModel.credits
                }
            }
            
            val finalGpa = if (totalCredits > 0) totalGpaPoints / totalCredits else 0.0
            
            semester.toUiModel(totalCredits = totalCredits, totalGpa4 = finalGpa)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Tự động load học kỳ hiện tại nếu chưa chọn
    init {
        viewModelScope.launch {
            val current = repository.getCurrentSemester()
            if (current != null && _selectedSemesterId.value == null) {
                _selectedSemesterId.value = current.semesterId
            } else if (current == null) {
                // Nếu chưa có học kỳ nào, Room Migration đã tạo 1 cái nhưng isCurrent = 1.
                // Thử lấy cái đầu tiên
                repository.getAllSemesters().collect { list ->
                    if (list.isNotEmpty() && _selectedSemesterId.value == null) {
                        _selectedSemesterId.value = list.first().semesterId
                        repository.setCurrentSemester(list.first().semesterId)
                    }
                }
            }
        }
    }

    fun selectSemester(semesterId: Long) {
        _selectedSemesterId.value = semesterId
        viewModelScope.launch {
            repository.setCurrentSemester(semesterId)
        }
    }
    
    fun addSemester(name: String, isCurrent: Boolean = true) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = repository.addSemester(SemesterEntity(semesterName = name, isCurrent = isCurrent))
            if (isCurrent) {
                _selectedSemesterId.value = id
            }
        }
    }

    // 2. Quản lý môn học theo học kỳ đã chọn
    @OptIn(ExperimentalCoroutinesApi::class)
    val subjects: StateFlow<List<SubjectGradeUiModel>> = _selectedSemesterId
        .flatMapLatest { semId ->
            if (semId == null) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                repository.getSubjectsWithGradesBySemester(semId)
                    .map { list -> list.map { it.toUiModel() } }
            }
        }
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
        val semId = _selectedSemesterId.value ?: return
        val cleanName = subjectName.trim()
        if (cleanName.isBlank() || credits <= 0) return
        
        viewModelScope.launch {
            val entity = SubjectGradeEntity(
                semesterId = semId,
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

    fun updateSubject(
        subjectId: Long,
        subjectName: String,
        credits: Int,
        targetGrade: String
    ) {
        val semId = _selectedSemesterId.value ?: return
        viewModelScope.launch {
            val existing = repository.getSubjectWithGradesById(subjectId) ?: return@launch
            val updated = existing.subject.copy(
                subjectName = subjectName,
                credits = credits,
                targetGrade = targetGrade
            )
            repository.updateSubject(updated)
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

    // 3. Quản lý Component
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

    fun addComponent(subjectId: Long, name: String, weight: Double) {
        viewModelScope.launch {
            val comp = GradeComponentEntity(
                subjectId = subjectId,
                componentName = name,
                weightPercent = weight,
                achievedScore = null
            )
            repository.addComponent(comp)
        }
    }

    fun updateComponentDetails(componentId: Long, name: String, weight: Double) {
        viewModelScope.launch {
            // Cần lấy tất cả subjects vì component thuộc về subject, không thể query component trực tiếp dễ bằng Flow (cần DAO get component, nhưng thôi làm qua list có sẵn trên UI hoặc repo)
            // Cách nhanh nhất là truyền subjectId vào, nhưng ta có thể lấy component từ repo nếu viết thêm hàm trong DAO. 
            // Tuy nhiên, cách đơn giản là chỉ cập nhật properties
            // Để cập nhật an toàn, nên thêm hàm getComponentById trong DAO, nhưng để tránh sửa nhiều ta có thể cập nhật gián tiếp.
            // Sửa đổi: Đã có hàm updateComponent(GradeComponentEntity), cần tạo entity với componentId. 
            // Vấn đề: cần biết subjectId cũ, achievedScore cũ. 
            // Sẽ truyền full GradeComponentUiModel từ UI xuống
        }
    }
    
    fun updateComponentFull(uiModel: GradeComponentUiModel, subjectId: Long) {
        viewModelScope.launch {
            val comp = GradeComponentEntity(
                componentId = uiModel.componentId,
                subjectId = subjectId,
                componentName = uiModel.componentName,
                weightPercent = uiModel.weightPercent,
                achievedScore = uiModel.achievedScore
            )
            repository.updateComponent(comp)
        }
    }

    fun deleteComponent(componentId: Long) {
        viewModelScope.launch {
            repository.deleteComponent(componentId)
        }
    }
}
