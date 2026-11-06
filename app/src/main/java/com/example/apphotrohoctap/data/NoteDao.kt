package com.example.apphotrohoctap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY updatedAtMillis DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND courseName COLLATE NOCASE = :courseName ORDER BY updatedAtMillis DESC")
    fun getNotesByCourse(courseName: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, updatedAtMillis = :timestamp WHERE id = :noteId")
    suspend fun softDeleteNote(noteId: Long, timestamp: Long)

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesForSync(): List<NoteEntity>
}
