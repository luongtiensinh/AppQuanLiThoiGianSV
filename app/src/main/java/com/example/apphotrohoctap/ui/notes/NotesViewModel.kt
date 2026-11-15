package com.example.apphotrohoctap.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apphotrohoctap.data.NoteEntity
import com.example.apphotrohoctap.data.NoteRepository
import com.example.apphotrohoctap.data.ScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class NotesViewModel(
    private val repository: NoteRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableCourses: StateFlow<List<String>> = scheduleRepository.getDistinctSubjectNames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(note: NoteEntity, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.addNote(note)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun updateNote(note: NoteEntity, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.updateNote(note)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onComplete(true) }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onComplete(false) }
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note.id)
        }
    }
    
    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }
}
