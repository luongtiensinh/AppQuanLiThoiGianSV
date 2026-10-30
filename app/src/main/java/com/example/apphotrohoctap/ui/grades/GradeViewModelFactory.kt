package com.example.apphotrohoctap.ui.grades

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.GradeRepository

class GradeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GradeViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = GradeRepository(database.gradeDao())
            @Suppress("UNCHECKED_CAST")
            return GradeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
