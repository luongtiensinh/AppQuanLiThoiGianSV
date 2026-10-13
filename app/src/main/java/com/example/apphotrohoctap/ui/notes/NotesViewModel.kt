package com.example.apphotrohoctap.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.NoteEntity
import com.example.apphotrohoctap.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.addNote(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
    
    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }
}
