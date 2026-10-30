package com.example.apphotrohoctap.ui.grades

import com.example.apphotrohoctap.data.GradeComponentEntity
import com.example.apphotrohoctap.data.SubjectWithGrades

data class GradeComponentUiModel(
    val componentId: Long,
    val componentName: String,
    val weightPercent: Double,
    val achievedScore: Double?
)

data class SubjectGradeUiModel(
    val subjectId: Long,
    val subjectName: String,
    val credits: Int,
    val targetGrade: String,
    val components: List<GradeComponentUiModel>,
    val currentAccumulatedScore: Double, // Điểm hệ 10 hiện tại tích lũy được
    val requiredScoreToTarget: Double?,  // Điểm cần đạt của phần còn lại
    val isCompleted: Boolean,            // true nếu tất cả component đã có điểm
    val finalLetterGrade: String?        // Điểm chữ cuối cùng (nếu isCompleted = true)
)

internal fun SubjectWithGrades.toUiModel(): SubjectGradeUiModel {
    val uiComponents = components.map { 
        GradeComponentUiModel(it.componentId, it.componentName, it.weightPercent, it.achievedScore) 
    }

    var accumulated = 0.0
    var hasNull = false
    
    val pairList = uiComponents.map { 
        if (it.achievedScore == null) hasNull = true
        else accumulated += (it.weightPercent / 100.0) * it.achievedScore
        
        Pair(it.weightPercent, it.achievedScore)
    }

    val requiredScore = GradeCalculator.calculateRequiredScore(subject.targetGrade, pairList)
    val finalLetterGrade = if (!hasNull) GradeCalculator.getLetterGradeFromScore(accumulated) else null

    return SubjectGradeUiModel(
        subjectId = subject.subjectId,
        subjectName = subject.subjectName,
        credits = subject.credits,
        targetGrade = subject.targetGrade,
        components = uiComponents,
        currentAccumulatedScore = accumulated,
        requiredScoreToTarget = requiredScore,
        isCompleted = !hasNull,
        finalLetterGrade = finalLetterGrade
    )
}
