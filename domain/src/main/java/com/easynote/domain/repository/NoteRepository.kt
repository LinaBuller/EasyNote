package com.easynote.domain.repository

import androidx.lifecycle.LiveData
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

    fun getNotes(query: SimpleSQLiteQuery): LiveData<List<Note>>
    fun setNote(note: Note): Long
    fun updateNote(note: Note)
    fun deleteNote(note: Note)
    fun getCategories():LiveData<List<Category>>
    fun setCategory(category: Category): Long
    fun updateCategory(category: Category)
    fun deleteCategory(category: Category)
    suspend fun getNoteWithCategories(idNote: Long): NoteWithCategoriesModel
    suspend fun getNoteWithCategories(note: Note): NoteWithCategoriesModel
    fun setNoteWithCategory(note: Note, category: List<NoteWithCategoriesCrossRefModel>)
    fun updateNoteWithCategories(note:Note, categories: List<Category>)
    fun getFavoriteColor():LiveData<List<FavoriteColor>>
    fun setFavoritesColor(listColor: List<FavoriteColor>)
    fun deleteFavoriteColor(favoriteColor: FavoriteColor)
    fun getItemsTextFromNote(idNote: Long):List<MultiItem>
    fun setTextItemFromNote(textItem:TextItem): Long
    fun updateTextItemFromNote(textItem: TextItem)
    fun deleteTextItemFromNote(textItem: TextItem)
    fun getImageFromImageItem(idImageItem:Long): List<Image>
    fun deleteImage(image: Image)
    fun getImageItemsFromNote(idNote: Long): List<ImageItem>
    fun setImageItemWithImages(item:ImageItem, imageList: List<Image>?): Long
    fun updateImageItem(item: ImageItem)
    fun deleteImageItem(imageItem: ImageItem)


    fun getIsFirstUsagesSharPref():Boolean
    fun setIsFirstUsagesSharPref(isFirst:Boolean)
    fun getPreferredThemeSharPref(): Boolean
    fun setPreferredThemeSharPref(preferredTheme: Boolean)
    fun getTypeListSharPref(): Boolean
    fun setTypeListSharPref(typeList:Boolean)
    fun getIsBioAuthSharedPref(): Boolean
    fun setIsBioAuthSharedPref(isBioAuth:Boolean)

}