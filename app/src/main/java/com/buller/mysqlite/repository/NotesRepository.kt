package com.buller.mysqlite.repository

import androidx.lifecycle.*
import com.buller.mysqlite.data.NotesDao
import com.buller.mysqlite.fragments.constans.SortedConstants
import com.buller.mysqlite.model.*


class NotesRepository(private val notesDao: NotesDao) {
    val readAllCategories: LiveData<List<Category>> = notesDao.getCategories().asLiveData()
    val favoriteColor:LiveData<List<FavoriteColor>> = notesDao.getFavoritesColor().asLiveData()

    private val _readAllNotes: MutableLiveData<Int> by lazy { MutableLiveData(0) }
    private var searchQuery: String? = null
    private var findIdCategory = -1L
    val readAllNotes =
        _readAllNotes.switchMap { _note ->
            if (findIdCategory == -1L) {
                if (searchQuery == null) {
                    when (_note) {
                        SortedConstants.SORT_AZ -> notesDao.getNoteSortedByTitle(SortedConstants.SORT_AZ)
                        SortedConstants.SORT_ZA -> notesDao.getNoteSortedByTitle(SortedConstants.SORT_ZA)
                        SortedConstants.SORT_NEWOLD -> notesDao.getNoteSortedByTitle(SortedConstants.SORT_NEWOLD)
                        SortedConstants.SORT_OLDNEW -> notesDao.getNoteSortedByTitle(SortedConstants.SORT_OLDNEW)
                        else -> {
                            notesDao.getNoteSortedByTitle(SortedConstants.NO_SORT)
                        }
                    }
                } else {
                    notesDao.getSearchText(searchQuery!!)
                }

            } else {
                when (_note) {
                    SortedConstants.SORT_AZ -> notesDao.getNotesSelectedCategory(
                        findIdCategory,
                        SortedConstants.SORT_AZ
                    )
                    SortedConstants.SORT_ZA -> notesDao.getNotesSelectedCategory(
                        findIdCategory,
                        SortedConstants.SORT_ZA
                    )
                    SortedConstants.SORT_NEWOLD -> notesDao.getNotesSelectedCategory(
                        findIdCategory,
                        SortedConstants.SORT_NEWOLD
                    )
                    SortedConstants.SORT_OLDNEW -> notesDao.getNotesSelectedCategory(
                        findIdCategory,
                        SortedConstants.SORT_OLDNEW
                    )
                    else -> {
                        notesDao.getNotesSelectedCategory(findIdCategory, SortedConstants.NO_SORT)
                    }
                }
            }
        }

    fun sortBY(filter: Int, idCategory: Long = -1L, searchText: String? = null) {
        findIdCategory = idCategory
        searchQuery = searchText
        _readAllNotes.value = filter
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

    fun insertNoteWithImage(idNote: Long, listOfImages: List<Image>) {
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

    fun addFavoritesColor(listColor:List<FavoriteColor>){
        notesDao.insertFavoritesColor(listColor)
    }

    fun updateFavoritesColor(favoriteColor: FavoriteColor){
        notesDao.deleteFavoritesColor(favoriteColor)
        notesDao.insertFavoritesColor(listOf(favoriteColor))
    }

    fun deleteFavColor(idFavColor: FavoriteColor) {
        notesDao.deleteFavoritesColor(idFavColor)
    }

    companion object {
        @Volatile
        private var instance: NotesRepository? = null
        fun getInstance(notesDao: NotesDao) = instance ?: synchronized(this) {
            instance ?: NotesRepository(notesDao).also { instance = it }
        }
    }
}