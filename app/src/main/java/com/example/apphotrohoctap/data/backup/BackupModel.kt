package com.example.apphotrohoctap.data.backup

import com.example.apphotrohoctap.data.GradeComponentEntity
import com.example.apphotrohoctap.data.NoteEntity
import com.example.apphotrohoctap.data.ScheduleEntity
import com.example.apphotrohoctap.data.SubjectGradeEntity
import com.example.apphotrohoctap.data.SubtaskEntity
import com.example.apphotrohoctap.data.TaskEntity

data class BackupModel(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val tasks: List<TaskEntity> = emptyList(),
    val subtasks: List<SubtaskEntity> = emptyList(),
    val schedules: List<ScheduleEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val subjects: List<SubjectGradeEntity> = emptyList(),
    val gradeComponents: List<GradeComponentEntity> = emptyList()
)
