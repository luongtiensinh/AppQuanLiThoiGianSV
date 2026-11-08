package com.example.apphotrohoctap.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

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
    val subjectId: Long = 0L,
    val componentName: String = "", // Ví dụ: "Chuyên cần", "Giữa kỳ", "Cuối kỳ"
    val weightPercent: Double = 0.0, // Trọng số (%): ví dụ 10.0, 30.0, 60.0
    val achievedScore: Double? = null, // Điểm đạt được (thang 10), null nếu chưa có điểm
    val syncId: String = UUID.randomUUID().toString(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
