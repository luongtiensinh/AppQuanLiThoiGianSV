package com.example.apphotrohoctap.ui.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.NoteRepository
import com.example.apphotrohoctap.data.ScheduleRepository

internal class NotesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = NoteRepository(database.noteDao())
            val scheduleRepository = ScheduleRepository(database.scheduleDao())
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository, scheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
