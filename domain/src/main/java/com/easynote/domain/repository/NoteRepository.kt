package com.easynote.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.easynote.domain.models.Category
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategoriesCrossRefModel
import com.easynote.domain.models.NoteWithCategoriesModel
import com.easynote.domain.models.TextItem

interface NoteRepository {

    fun getNotes(query: SimpleSQLiteQuery): LiveData<List<com.easynote.domain.models.Note>>
    fun setNote(note: com.easynote.domain.models.Note): Long
    fun updateNote(note: com.easynote.domain.models.Note)
    fun deleteNote(note: com.easynote.domain.models.Note)
    fun getCategories():LiveData<List<com.easynote.domain.models.Category>>
    fun setCategory(category: com.easynote.domain.models.Category): Long
    fun updateCategory(category: com.easynote.domain.models.Category)
    fun deleteCategory(category: com.easynote.domain.models.Category)
    suspend fun getNoteWithCategories(idNote: Long): com.easynote.domain.models.NoteWithCategoriesModel
    fun setNoteWithCategory(note: com.easynote.domain.models.Note, category: List<com.easynote.domain.models.NoteWithCategoriesCrossRefModel>)
    fun updateNoteWithCategories(note: com.easynote.domain.models.Note, categories: List<com.easynote.domain.models.Category>)
    fun getFavoriteColor():List<com.easynote.domain.models.FavoriteColor>
    fun setFavoritesColor(listColor: List<com.easynote.domain.models.FavoriteColor>)
    fun deleteFavoriteColor(favoriteColor: com.easynote.domain.models.FavoriteColor)
    fun getItemsTextFromNote(idNote: Long): List<com.easynote.domain.models.MultiItem>
    fun setTextItemFromNote(textItem: com.easynote.domain.models.TextItem): Long
    fun updateTextItemFromNote(textItem: com.easynote.domain.models.TextItem)
    fun deleteTextItemFromNote(textItem: com.easynote.domain.models.TextItem)
    fun getImageFromImageItem(idImageItem: Long): List<com.easynote.domain.models.Image>
    fun deleteImageFromImageItem(image: com.easynote.domain.models.Image)
    fun getImageItemsFromNote(idNote: Long): List<com.easynote.domain.models.ImageItem>
    fun setImageItemWithImages(item: com.easynote.domain.models.ImageItem, imageList: List<com.easynote.domain.models.Image>?): Long
    fun updateImageItem(item: com.easynote.domain.models.ImageItem, imageList: List<com.easynote.domain.models.Image>)
}