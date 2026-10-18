package com.example.apphotrohoctap.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.ScheduleRepository
import com.example.apphotrohoctap.data.TaskRepository

internal class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val taskRepository = TaskRepository(database.taskDao())
            val scheduleRepository = ScheduleRepository(database.scheduleDao())
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(taskRepository, scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
