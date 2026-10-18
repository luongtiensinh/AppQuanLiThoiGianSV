package com.example.apphotrohoctap.ui.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apphotrohoctap.data.AppDatabase
import com.example.apphotrohoctap.data.NoteRepository

internal class NotesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = NoteRepository(database.noteDao())
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
