package com.example.apphotrohoctap.data

import androidx.room.Embedded
import androidx.room.Relation

data class SubjectWithGrades(
    @Embedded val subject: SubjectGradeEntity,
    @Relation(
        parentColumn = "subjectId",
        entityColumn = "subjectId"
    )
    val components: List<GradeComponentEntity>
)
