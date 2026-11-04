package com.example.apphotrohoctap.ui.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.TaskRepository

import com.example.apphotrohoctap.data.ScheduleRepository

internal class TasksViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = TaskRepository(database.taskDao())
            val scheduleRepository = ScheduleRepository(database.scheduleDao())
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(repository, scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
