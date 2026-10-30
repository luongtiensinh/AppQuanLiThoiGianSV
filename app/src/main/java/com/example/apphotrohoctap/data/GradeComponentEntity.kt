package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grade_components",
    foreignKeys = [
        ForeignKey(
            entity = SubjectGradeEntity::class,
            parentColumns = ["subjectId"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class GradeComponentEntity(
    @PrimaryKey(autoGenerate = true)
    val componentId: Long = 0,
    val subjectId: Long,
    val componentName: String, // Ví dụ: "Chuyên cần", "Giữa kỳ", "Cuối kỳ"
    val weightPercent: Double, // Trọng số (%): ví dụ 10.0, 30.0, 60.0
    val achievedScore: Double? // Điểm đạt được (thang 10), null nếu chưa có điểm
)
