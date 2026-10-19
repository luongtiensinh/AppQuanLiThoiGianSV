package com.example.apphotrohoctap.ui.courses

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.NoteRepository
import com.example.apphotrohoctap.data.ScheduleRepository
import com.example.apphotrohoctap.data.TaskRepository

internal class CourseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val taskRepo = TaskRepository(database.taskDao())
            val scheduleRepo = ScheduleRepository(database.scheduleDao())
            val noteRepo = NoteRepository(database.noteDao())
            @Suppress("UNCHECKED_CAST")
            return CourseViewModel(taskRepo, scheduleRepo, noteRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
