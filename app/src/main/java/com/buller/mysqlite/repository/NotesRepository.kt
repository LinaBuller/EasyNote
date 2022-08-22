package com.buller.mysqlite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.buller.mysqlite.data.NotesDao
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.model.NoteWithImagesWrapper
import kotlinx.coroutines.flow.Flow


class NotesRepository (private val notesDao: NotesDao) {
    val readAllNotes: LiveData<List<Note>> = notesDao.getNotes().asLiveData()

    fun insertNote(note: Note): Long {
        return notesDao.insertNote(note)
    }

    fun insertImage(image: Image):Long{
        return notesDao.insertImage(image)
    }

    suspend fun getNoteWithImages(idNote: Long): NoteWithImagesWrapper {
        return notesDao.getNoteWithImages(idNote)
    }

    fun insertNoteWithImage(noteWithImagesWrapper: NoteWithImagesWrapper){
        notesDao.insertNoteWithImage(noteWithImagesWrapper)
    }








    fun updateNote(note: Note) {
        notesDao.updateNote(note)
    }

    fun updateImage(image: Image) {
        notesDao.updateImage(image)
    }



    fun deleteNote(note: Note) {
        notesDao.deleteNote(note)
    }
    fun deleteNote(id:Long){
        notesDao.deleteNote(id)
    }



    companion object {
        @Volatile
        private var instance: NotesRepository? = null
        fun getInstance(notesDao: NotesDao) = instance ?: synchronized(this) {
            instance ?: NotesRepository(notesDao).also { instance = it }
        }
    }
}