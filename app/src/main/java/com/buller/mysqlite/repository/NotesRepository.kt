package com.buller.mysqlite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.buller.mysqlite.data.NotesDao
import com.buller.mysqlite.model.*
import kotlinx.coroutines.flow.forEach
import java.util.stream.Collector


class NotesRepository(private val notesDao: NotesDao) {
    val readAllNotes = notesDao.getNotes(0)
    val readAllCategories: LiveData<List<Category>> = notesDao.getCategories().asLiveData()


    fun insertNote(note: Note): Long {
        return notesDao.insertNote(note)
    }

    fun refreshAllNotes(idNote:Long){
        notesDao.getNotes(idNote)
    }
    fun insertImage(image: Image): Long {
        return notesDao.insertImage(image)
    }

    fun getNote(noteId: Long): Note {
        return notesDao.getNote(noteId)
    }

    suspend fun getNoteWithImages(idNote: Long): NoteWithImages {
        return notesDao.getNoteWithImages(idNote)
    }

    suspend fun getNoteWithCategories(idNote: Long): NoteWithCategories {
        return notesDao.getNoteWithCategory(idNote)
    }

    fun insertNoteWithImage(idNote:Long, listOfImages: List<Image>) {
        notesDao.saveImagesOfNote(idNote, listOfImages)
    }

    fun deleteNote(note: Note) {
        notesDao.deleteNote(note)
    }

    fun deleteNote(id: Long) {
        notesDao.deleteNote(id)
    }

    fun deleteImage(image: Image) {
        notesDao.deleteImage(image)
    }

    fun insertCategory(category: Category): Long {
        return notesDao.insertCategory(category)
    }

    fun deleteCategory(category: Category) {
        notesDao.deleteCategory(category)
    }

    fun getCategory(idCategory: Long): Category {
        return notesDao.getCategory(idCategory)
    }

    fun saveNoteWithCategory(note: Note, category: List<Category>) {
        val list = arrayListOf<NoteWithCategoriesCrossRef>()
        val  listIdCategory = arrayListOf<Long>()
            category.forEach {
            list.add(NoteWithCategoriesCrossRef(note.id, it.idCategory))
            listIdCategory.add(it.idCategory)
        }
        notesDao.deleteNotExistCategory(listIdCategory,note.id)
        notesDao.insertManyNoteWithCategoriesCrossRef(list)
    }


    fun update(note: Note) {
        notesDao.updateNote(note)
    }

    fun updateIma(note: Note, images: List<Image>) {
        notesDao.updateNote(note)
        images.forEach {
            notesDao.updateImage(it)
        }
    }

    fun update(note: Note, categories: List<Category>) {
        categories.forEach {
            notesDao.update(NoteWithCategoriesCrossRef(note.id, it.idCategory))
        }
    }

    fun deleteCrossFromDeleteNote(id: Long) {
        notesDao.deleteCrossFromDeleteNote(id)
    }

    companion object {
        @Volatile
        private var instance: NotesRepository? = null
        fun getInstance(notesDao: NotesDao) = instance ?: synchronized(this) {
            instance ?: NotesRepository(notesDao).also { instance = it }
        }
    }
}