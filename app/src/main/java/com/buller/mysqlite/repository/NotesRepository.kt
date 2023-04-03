package com.buller.mysqlite.repository

import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.buller.mysqlite.data.NotesDao
import com.buller.mysqlite.model.*


class NotesRepository(private val notesDao: NotesDao) {

    companion object {
        @Volatile
        private var instance: NotesRepository? = null
        fun getInstance(notesDao: NotesDao) = instance ?: synchronized(this) {
            instance ?: NotesRepository(notesDao).also { instance = it }
        }
    }

    val readAllCategories: LiveData<List<Category>> = notesDao.getCategories().asLiveData()
    val favoriteColor: LiveData<List<FavoriteColor>> = notesDao.getFavoritesColor().asLiveData()

    fun getNotes(query: SimpleSQLiteQuery): LiveData<List<Note>> {
        return notesDao.getNotes(query)
    }

    fun insertNote(note: Note): Long {
        return notesDao.insertNote(note)
    }

    suspend fun getNoteWithImages(idNote: Long): NoteWithImages {
        return notesDao.getNoteWithImages(idNote)
    }

    suspend fun getNoteWithCategories(idNote: Long): NoteWithCategories {
        return notesDao.getNoteWithCategory(idNote)
    }

    fun saveNoteWithImage(idNote: Long, listOfImages: List<Image>) {
        notesDao.saveImagesOfNote(idNote, listOfImages)
    }

    fun deleteNote(note: Note) {
        notesDao.deleteNote(note)
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

    fun saveNoteWithCategory(note: Note, category: List<Category>) {
        val list = arrayListOf<NoteWithCategoriesCrossRef>()
        val listIdCategory = arrayListOf<Long>()
        category.forEach {
            list.add(NoteWithCategoriesCrossRef(note.id, it.idCategory))
            listIdCategory.add(it.idCategory)
        }
        notesDao.deleteNotExistCategory(listIdCategory, note.id)
        notesDao.insertManyNoteWithCategoriesCrossRef(list)
    }


    fun update(note: Note) {
        notesDao.updateNote(note)
    }

    fun update(category: Category) {
        notesDao.update(category)
    }

    fun update(note: Note, categories: List<Category>) {
        categories.forEach {
            notesDao.update(NoteWithCategoriesCrossRef(note.id, it.idCategory))
        }
    }

    fun deleteCrossFromDeleteNote(id: Long) {
        notesDao.deleteCrossFromDeleteNote(id)
    }

    fun addFavoritesColor(listColor: List<FavoriteColor>) {
        notesDao.insertFavoritesColor(listColor)
    }

    fun deleteFavColor(idFavColor: FavoriteColor) {
        notesDao.deleteFavoritesColor(idFavColor)
    }

}