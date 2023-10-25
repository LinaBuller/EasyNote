package com.example.data.storage.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.easynote.domain.models.Category
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategories
import com.easynote.domain.models.NoteWithCategoriesCrossRef
import com.easynote.domain.models.TextItem
import com.easynote.domain.repository.NoteRepository
import com.example.data.storage.room.RoomDataSource
import com.example.data.storage.sharedprefs.SharedPrefNoteManager
import org.koin.core.component.KoinComponent


class NotesRepositoryImpl(
    val roomDataSource: RoomDataSource,
    var sharedPrefNoteManager: SharedPrefNoteManager
) : NoteRepository, KoinComponent {

    override suspend fun getNotes(query: SimpleSQLiteQuery): LiveData<List<Note>> {
//supaDataSource.getNotes(query)
        val note = roomDataSource.getNotes(query)
        val mediatorNotes = MediatorLiveData<List<Note>>()
        mediatorNotes.addSource(note) {
            mediatorNotes.value = note.value
        }
        return mediatorNotes
    }

    override suspend fun setNote(note: Note): Long {
        return roomDataSource.setNote(note)
    }

    override fun updateNote(note: Note) {
//        val noteSt = noteMapper.mapToStorage(note)
//        notesDao.updateNote(noteSt)
        roomDataSource.updateNote(note)
    }

    override fun deleteNote(note: Note) {
//        val noteSt = noteMapper.mapToStorage(note)
//        notesDao.deleteNote(noteSt)
        roomDataSource.deleteNote(note)
    }

    override fun getCategories(): LiveData<List<Category>> {
//        val listStorageCategory = notesDao.getCategories()
//        val mediatorNotes = MediatorLiveData<List<Category>>()
//        mediatorNotes.addSource(listStorageCategory) {
//            val arrayList = arrayListOf<Category>()
//            arrayList.addAll(categoryListMapper.mapToDomain(it))
//            mediatorNotes.value = arrayList
//        }
//        return mediatorNotes
        return roomDataSource.getCategories()
    }

    override fun setCategory(category: Category): Long {
//        val categoryStr = categoryMapper.mapToStorage(category)
//        return notesDao.insertCategory(categoryStr)
        return roomDataSource.setCategory(category)
    }

    override fun deleteCategory(category: Category) {
//        val categoryStr = categoryMapper.mapToStorage(category)
//        notesDao.deleteCategory(categoryStr)
        roomDataSource.deleteCategory(category)
    }

    override fun updateCategory(category: Category) {
//        val categoryStr = categoryMapper.mapToStorage(category)
//        notesDao.update(categoryStr)
        roomDataSource.updateCategory(category)
    }

    override suspend fun getNoteWithCategories(idNote: Long): NoteWithCategories {
        return getNoteWithCategoriesObj(idNote)
    }

    override suspend fun getNoteWithCategories(note: Note): NoteWithCategories {
        return getNoteWithCategoriesObj(note.id)
    }

    private suspend fun getNoteWithCategoriesObj(noteId: Long): NoteWithCategories {
//        val noteWithCategories = notesDao.getNoteWithCategory(noteId)
//        val note = noteMapper.mapToDomain(noteWithCategories.note)
//        val categories = arrayListOf<Category>()
//        noteWithCategories.listOfCategories?.forEach {
//            val category = categoryMapper.mapToDomain(it)
//            categories.add(category)
//        }
//        return NoteWithCategories(
//            note = note,
//            listOfCategories = categories
//        )

        return roomDataSource.getNoteWithCategoriesObj(noteId)
    }

    override fun setNoteWithCategory(note: Note, category: List<NoteWithCategoriesCrossRef>) {
//        val list = arrayListOf<StorageNoteWithCategoriesCrossRef>()
//        val listIdCategory = arrayListOf<Long>()
//        category.forEach {
//            list.add(StorageNoteWithCategoriesCrossRef(note.id, it.category_id))
//            listIdCategory.add(it.category_id)
//        }
//        notesDao.deleteNotExistCategory(listIdCategory, note.id)
//        notesDao.insertManyNoteWithCategoriesCrossRef(list)

        roomDataSource.setNoteWithCategory(note, category)
    }

    override fun updateNoteWithCategories(note: Note, categories: List<Category>) {
//        categories.forEach {
//            notesDao.update(StorageNoteWithCategoriesCrossRef(note.id, it.idCategory))
//        }
        roomDataSource.updateNoteWithCategories(note, categories)
    }

    override fun getFavoriteColor(): LiveData<List<FavoriteColor>> {
//        val storageFavoriteColor = notesDao.getFavoritesColor()
//        val mediatorFavColors = MediatorLiveData<List<FavoriteColor>>()
//        mediatorFavColors.addSource(storageFavoriteColor) {
//            val arrayList = arrayListOf<FavoriteColor>()
//            arrayList.addAll(favColorListMapper.mapToDomain(storageFavoriteColor.value!!))
//            mediatorFavColors.value = arrayList
//        }
//        return mediatorFavColors
        return roomDataSource.getFavoriteColor()
    }

    override fun setFavoritesColor(listColor: List<FavoriteColor>) {
//        val listStorageCategory = arrayListOf<StorageFavoriteColor>()
//        listColor.forEach {
//            val storageFavoriteColor = favColorMapper.mapToStorage(it)
//            listStorageCategory.add(storageFavoriteColor)
//        }
//        notesDao.insertFavoritesColor(listStorageCategory)
        roomDataSource.setFavoritesColor(listColor)
    }

    override fun deleteFavoriteColor(favoriteColor: FavoriteColor) {
//        val storageFavoriteColor = favColorMapper.mapToStorage(favoriteColor)
//        notesDao.deleteFavoritesColor(storageFavoriteColor)

        roomDataSource.deleteFavoriteColor(favoriteColor)
    }

    override fun getItemsTextFromNote(idNote: Long): List<MultiItem> {
//        val listStorageTextItem = notesDao.getItemsText(idNote)
//        val listTextItem = arrayListOf<MultiItem>()
//        listStorageTextItem.forEach {
//            val textItem = textItemMapper.mapToDomain(it)
//            listTextItem.add(textItem)
//        }
//        return listTextItem
        return roomDataSource.getItemsTextFromNote(idNote)
    }

    override fun setTextItemFromNote(textItem: TextItem): Long {
//        val storageTextItem = textItemMapper.mapToStorage(textItem)
//        return notesDao.insertTextItemFromNote(storageTextItem)
        return roomDataSource.setTextItemFromNote(textItem)
    }

    override fun updateTextItemFromNote(textItem: TextItem) {
//        val storageTextItem = textItemMapper.mapToStorage(textItem)
//        notesDao.updateTextItem(storageTextItem)
        roomDataSource.updateTextItemFromNote(textItem)
    }

    override fun deleteTextItemFromNote(textItem: TextItem) {
//        val storageTextItem = textItemMapper.mapToStorage(textItem)
//        notesDao.deleteTextItem(storageTextItem)
        roomDataSource.deleteTextItemFromNote(textItem)
    }

    override fun deleteImage(image: Image) {
//        val storageImage = imageMapper.mapToStorage(image)
//        notesDao.deleteImage(storageImage)
        roomDataSource.deleteImage(image)
    }

    override fun getImageItemsFromNote(idNote: Long): List<ImageItem> {
//        val storageImageItem = notesDao.getImageItems(idNote)
//
//        val listImageItem = arrayListOf<ImageItem>()
//        storageImageItem.forEach {
//            val imageItem = imageItemMapper.mapToDomain(it)
//            imageItem.listImageItems = getImageFromImageItem(imageItem.imageItemId)
//            listImageItem.add(imageItem)
//        }
//        return listImageItem
        return roomDataSource.getImageItemsFromNote(idNote)
    }

    override fun getImageFromImageItem(idImageItem: Long): List<Image> {
//        val storageImages = notesDao.getImages(idImageItem)
//        return imageListMapper.mapToDomain(storageImages)
        return roomDataSource.getImageFromImageItem(idImageItem)
    }


    @Transaction
    override fun setImageItemWithImages(item: ImageItem, imageList: List<Image>?): Long {
//        val storageImageItem = imageItemMapper.mapToStorage(item)
//        val id = notesDao.insertImageItem(storageImageItem)
//        if (imageList != null) {
//            val listStorageImage = imageListMapper.mapToStorage(imageList)
//            listStorageImage.forEach {
//                it.foreignId = id
//                notesDao.insertImage(it)
//            }
//        }
//        return id
        return roomDataSource.setImageItemWithImages(item, imageList)
    }

    override fun updateImageItem(item: ImageItem) {
//        val storageImageItem = imageItemMapper.mapToStorage(item)
//        val listImages = item.listImageItems
//        val storageImagesList = imageListMapper.mapToStorage(listImages)
//
//        storageImagesList.forEach { storageImage ->
//            if (storageImage.foreignId == 0L) {
//                storageImage.foreignId = item.imageItemId
//                notesDao.insertImage(storageImage)
//            }
//            if (!storageImage.isNew) {
//                notesDao.updateImage(storageImage)
//            }
//        }
//
//        notesDao.updateImageItem(storageImageItem)

        roomDataSource.updateImageItem(item)
    }

    override fun deleteImageItem(item: ImageItem) {
//        val storageImageItem = imageItemMapper.mapToStorage(item)
//        notesDao.deleteImageItem(storageImageItem)
        roomDataSource.deleteImageItem(item)
    }

    override fun getIsFirstUsagesSharPref(): Boolean {
        return sharedPrefNoteManager.getFirstUsages()
    }

    override fun setIsFirstUsagesSharPref(isFirst: Boolean) {
        sharedPrefNoteManager.setIsFirstUsages(isFirst)
    }

    override fun getPreferredThemeSharPref(): Boolean {
        return sharedPrefNoteManager.getPreferredTheme()
    }

    override fun setPreferredThemeSharPref(preferredTheme: Boolean) {
        sharedPrefNoteManager.setPreferredTheme(preferredTheme)
    }

    override fun getTypeListSharPref(): Boolean {
        return sharedPrefNoteManager.getTypeList()
    }

    override fun setTypeListSharPref(typeList: Boolean) {
        sharedPrefNoteManager.setTypeList(typeList)
    }

    override fun getIsBioAuthSharedPref(): Boolean {
        return sharedPrefNoteManager.getIsBioAuth()
    }

    override fun setIsBioAuthSharedPref(isBioAuth: Boolean) {
        sharedPrefNoteManager.setIsBioAuth(isBioAuth)
    }

     override fun getPath(): String? {
        return roomDataSource.getPath()
    }

    override fun checkpoint() {
        roomDataSource.checkpointDatabase()
    }

    override fun getAllImages():List<Image> {
       return roomDataSource.getAllImages()
    }
}