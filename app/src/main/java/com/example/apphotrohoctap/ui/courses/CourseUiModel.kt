package com.example.apphotrohoctap.ui.courses

import com.example.apphotrohoctap.data.NoteEntity
import com.example.apphotrohoctap.ui.schedule.StudyClass
import com.example.apphotrohoctap.ui.tasks.TaskItem

internal data class CourseUiState(
    val courseName: String = "",
    val courseCode: String = "Chưa có mã",
    val lecturer: String = "Chưa phân công",
    val room: String = "Chưa xếp phòng",
    val tasksTotal: Int = 0,
    val tasksDone: Int = 0,
    val schedules: List<StudyClass> = emptyList(),
    val tasks: List<TaskItem> = emptyList(),
    val notes: List<NoteEntity> = emptyList()
)
