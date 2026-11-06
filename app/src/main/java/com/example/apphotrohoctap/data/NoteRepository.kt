package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    fun getNotesByCourse(courseName: String): Flow<List<NoteEntity>> = noteDao.getNotesByCourse(courseName)
    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)
    suspend fun addNote(note: NoteEntity) = noteDao.insertNote(note.copy(updatedAtMillis = System.currentTimeMillis()))
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note.copy(updatedAtMillis = System.currentTimeMillis()))
    suspend fun deleteNote(noteId: Long) = noteDao.softDeleteNote(noteId, System.currentTimeMillis())
}
