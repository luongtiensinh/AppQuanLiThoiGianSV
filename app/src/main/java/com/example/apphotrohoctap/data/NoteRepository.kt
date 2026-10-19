package com.example.apphotrohoctap.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()
    fun getNotesByCourse(courseName: String): Flow<List<NoteEntity>> = noteDao.getNotesByCourse(courseName)
    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)
    suspend fun addNote(note: NoteEntity) = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
