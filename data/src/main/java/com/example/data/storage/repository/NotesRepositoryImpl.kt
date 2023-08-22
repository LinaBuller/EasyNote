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
import com.example.data.storage.models.StorageNote
import com.example.data.storage.NotesDao
import com.example.data.storage.models.NoteWithCategoriesCrossRef
import com.example.data.storage.models.StorageCategory
import com.example.data.storage.models.StorageFavoriteColor
import com.example.data.storage.models.StorageImage
import com.example.data.storage.models.StorageImageItem
import com.example.data.storage.models.StorageTextItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategoriesCrossRefModel
import com.easynote.domain.models.NoteWithCategoriesModel
import com.easynote.domain.models.TextItem
import com.easynote.domain.repository.NoteRepository
import com.example.data.storage.sharedprefs.SharedPrefNoteManager


class NotesRepositoryImpl(
    var notesDao: NotesDao,
    var sharedPrefNoteManager: SharedPrefNoteManager
) : NoteRepository {

    override fun getNotes(query: SimpleSQLiteQuery): LiveData<List<Note>> {

        val storageNotes = notesDao.getNotes(query)
        val mediatorNotes = MediatorLiveData<List<Note>>()
        mediatorNotes.addSource(storageNotes) {
            val arrayList = arrayListOf<Note>()
            arrayList.addAll(listStorageNoteMapToDomain(storageNotes.value!!))
            mediatorNotes.value = arrayList
        }
        return mediatorNotes
    }

    override fun setNote(note: Note): Long {
        val noteSt = noteMapToStorage(note)
        return notesDao.insertNote(noteSt)
    }

    override fun updateNote(note: Note) {
        val noteSt = noteMapToStorage(note)
        notesDao.updateNote(noteSt)
    }

    override fun deleteNote(note: Note) {
        val noteSt = noteMapToStorage(note)
        notesDao.deleteNote(noteSt)
    }

    private fun noteMapToStorage(note: Note): StorageNote {
        return StorageNote(
            title = note.title,
            content = note.content,
            id = note.id,
            text = note.text,
            gradientColorFirst = note.gradientColorFirst,
            gradientColorFirstH = note.gradientColorFirstH,
            gradientColorFirstS = note.gradientColorFirstS,
            gradientColorFirstL = note.gradientColorFirstL,
            gradientColorSecond = note.gradientColorSecond,
            gradientColorSecondH = note.gradientColorSecondH,
            gradientColorSecondS = note.gradientColorSecondS,
            gradientColorSecondL = note.gradientColorSecondL,
            time = note.time,
            isDeleted = note.isDeleted,
            isFavorite = note.isFavorite,
            isPin = note.isPin,
            isArchive = note.isArchive
        )
    }

    private fun storageNoteMapToDomain(storageNote: StorageNote): Note {
        return Note(
            title = storageNote.title,
            content = storageNote.content,
            id = storageNote.id,
            text = storageNote.text,
            gradientColorFirst = storageNote.gradientColorFirst,
            gradientColorFirstH = storageNote.gradientColorFirstH,
            gradientColorFirstS = storageNote.gradientColorFirstS,
            gradientColorFirstL = storageNote.gradientColorFirstL,
            gradientColorSecond = storageNote.gradientColorSecond,
            gradientColorSecondH = storageNote.gradientColorSecondH,
            gradientColorSecondS = storageNote.gradientColorSecondS,
            gradientColorSecondL = storageNote.gradientColorSecondL,
            time = storageNote.time,
            isDeleted = storageNote.isDeleted,
            isArchive = storageNote.isArchive,
            isPin = storageNote.isPin,
            isFavorite = storageNote.isFavorite
        )
    }

    private fun listStorageNoteMapToDomain(listStorageNote: List<StorageNote>): List<Note> {
        val list = arrayListOf<Note>()
        listStorageNote.forEach { storageNote ->
            val note = storageNoteMapToDomain(storageNote)
            list.add(note)
        }
        return list
    }

    override fun getCategories(): LiveData<List<Category>> {
        val listStorageCategory = notesDao.getCategories()
        val mediatorNotes = MediatorLiveData<List<Category>>()
        mediatorNotes.addSource(listStorageCategory) {
            val arrayList = arrayListOf<Category>()
            arrayList.addAll(listStorageCategoryMapToDomain(it))
            mediatorNotes.value = arrayList
        }
        return mediatorNotes
    }

    override fun setCategory(category: Category): Long {
        val categoryStr = categoryMapToStorage(category)
        return notesDao.insertCategory(categoryStr)
    }

    override fun deleteCategory(category: Category) {
        val categoryStr = categoryMapToStorage(category)
        notesDao.deleteCategory(categoryStr)
    }

    override fun updateCategory(category: Category) {
        val categoryStr = categoryMapToStorage(category)
        notesDao.update(categoryStr)
    }

    private fun storageCategoryMapToDomain(storageCategory: StorageCategory): Category {
        return Category(
            idCategory = storageCategory.idCategory,
            titleCategory = storageCategory.titleCategory
        )
    }

    private fun categoryMapToStorage(category: Category): StorageCategory {
        return StorageCategory(
            idCategory = category.idCategory,
            titleCategory = category.titleCategory
        )
    }

    private fun listStorageCategoryMapToDomain(listStorageCategory: List<StorageCategory>): List<Category> {
        val listCategory = arrayListOf<Category>()
        listStorageCategory.forEach {
            val category = storageCategoryMapToDomain(it)
            listCategory.add(category)
        }
        return listCategory
    }

    override suspend fun getNoteWithCategories(idNote: Long): NoteWithCategoriesModel {
        val noteWithCategories = notesDao.getNoteWithCategory(idNote)
        val note = storageNoteMapToDomain(noteWithCategories.note)
        val categories = arrayListOf<Category>()
        noteWithCategories.listOfCategories?.forEach {
            val category = storageCategoryMapToDomain(it)
            categories.add(category)
        }

        return NoteWithCategoriesModel(
            note = note,
            listOfCategories = categories
        )
    }

    override suspend fun getNoteWithCategories(note: Note): NoteWithCategoriesModel {
        val noteWithCategories = notesDao.getNoteWithCategory(note.id)
        val noteS = storageNoteMapToDomain(noteWithCategories.note)
        val categories = arrayListOf<Category>()
        noteWithCategories.listOfCategories?.forEach {
            val category = storageCategoryMapToDomain(it)
            categories.add(category)
        }

        return NoteWithCategoriesModel(
            note = noteS,
            listOfCategories = categories
        )
    }

    override fun setNoteWithCategory(note: Note, category: List<NoteWithCategoriesCrossRefModel>) {
        val list = arrayListOf<NoteWithCategoriesCrossRef>()
        val listIdCategory = arrayListOf<Long>()
        category.forEach {
            list.add(NoteWithCategoriesCrossRef(note.id, it.category_id))
            listIdCategory.add(it.category_id)
        }
        notesDao.deleteNotExistCategory(listIdCategory, note.id)
        notesDao.insertManyNoteWithCategoriesCrossRef(list)
    }

    override fun updateNoteWithCategories(note: Note, categories: List<Category>) {
        categories.forEach {
            notesDao.update(NoteWithCategoriesCrossRef(note.id, it.idCategory))
        }
    }

    override fun getFavoriteColor(): LiveData<List<FavoriteColor>> {
        val storageFavoriteColor = notesDao.getFavoritesColor()
        val mediatorFavColors = MediatorLiveData<List<FavoriteColor>>()
        mediatorFavColors.addSource(storageFavoriteColor) {
            val arrayList = arrayListOf<FavoriteColor>()
            arrayList.addAll(storageFavoriteColorMapToDomain(storageFavoriteColor.value!!))
            mediatorFavColors.value = arrayList
        }
        return mediatorFavColors
    }

    override fun setFavoritesColor(listColor: List<FavoriteColor>) {
        val listStorageCategory = arrayListOf<StorageFavoriteColor>()
        listColor.forEach {
            val storageFavoriteColor = favColorMapToStorage(it)
            listStorageCategory.add(storageFavoriteColor)
        }
        notesDao.insertFavoritesColor(listStorageCategory)
    }

    override fun deleteFavoriteColor(favoriteColor: FavoriteColor) {
        val storageCategory = favColorMapToStorage(favoriteColor)
        notesDao.deleteFavoritesColor(storageCategory)
    }

    private fun favColorMapToStorage(favColor: FavoriteColor): StorageFavoriteColor {
        return StorageFavoriteColor(
            id = favColor.id,
            number = favColor.number,
            h = favColor.h,
            s = favColor.s,
            l = favColor.l
        )
    }

    private fun storageFavoriteColorMapToDomain(storageFavoriteColor: List<StorageFavoriteColor>): List<FavoriteColor> {
        val arrayList = arrayListOf<FavoriteColor>()
        storageFavoriteColor.forEach {
            val favColor = FavoriteColor(
                id = it.id,
                number = it.number,
                h = it.h,
                s = it.s,
                l = it.l
            )
            arrayList.add(favColor)
        }
        return arrayList
    }

    override fun getItemsTextFromNote(idNote: Long): List<MultiItem> {
        val listStorageTextItem = notesDao.getItemsText(idNote)
        val listTextItem = arrayListOf<MultiItem>()
        listStorageTextItem.forEach {
            val textItem = storageTextItemMapToDomain(it)
            listTextItem.add(textItem)
        }
        return listTextItem
    }

    override fun setTextItemFromNote(textItem: TextItem): Long {
        val storageTextItem = textItemMapToStorage(textItem)
        return notesDao.insertTextItemFromNote(storageTextItem)
    }

    override fun updateTextItemFromNote(textItem: TextItem) {
        val storageTextItem = textItemMapToStorage(textItem)
        notesDao.updateTextItem(storageTextItem)
    }

    override fun deleteTextItemFromNote(textItem: TextItem) {
        val storageTextItem = textItemMapToStorage(textItem)
        notesDao.deleteTextItem(storageTextItem)
    }

    private fun textItemMapToStorage(textItem: TextItem): StorageTextItem {
        return StorageTextItem(
            itemTextId = textItem.itemTextId,
            foreignId = textItem.foreignId,
            text = textItem.text,
            position = textItem.position
        )
    }

    private fun storageTextItemMapToDomain(storageTextItem: StorageTextItem): TextItem {
        return TextItem(
            itemTextId = storageTextItem.itemTextId,
            foreignId = storageTextItem.foreignId,
            text = storageTextItem.text,
            position = storageTextItem.position
        )
    }



    override fun deleteImage(image: Image) {
        val storageImage = imageMapToStorage(image)
        notesDao.deleteImage(storageImage)
    }

    private fun storageImageMapToDomain(storageImage: StorageImage): Image {
        return Image(
            id = storageImage.id,
            foreignId = storageImage.foreignId,
            uri = storageImage.uri,
            isNew = false,
            position = storageImage.position
        )
    }

    private fun imageMapToStorage(image: Image): StorageImage {
        return StorageImage(id = image.id, foreignId = image.foreignId, uri = image.uri, isNew = image.isNew, position = image.position)
    }

    private fun listImageMapToStorage(listImage: List<Image>): List<StorageImage> {
        val listStorageImage = arrayListOf<StorageImage>()
        listImage.forEach {
            val storageImage = imageMapToStorage(it)
            listStorageImage.add(storageImage)
        }
        return listStorageImage
    }




    override fun getImageItemsFromNote(idNote: Long): List<ImageItem> {
        val storageImageItem = notesDao.getImageItems(idNote)

        val listImageItem = arrayListOf<ImageItem>()
        storageImageItem.forEach {
            val imageItem = storageImageItemMapToDomain(it)
            imageItem.listImageItems = getImageFromImageItem(imageItem.imageItemId)
            listImageItem.add(imageItem)
        }
        return listImageItem
    }

    override fun getImageFromImageItem(idImageItem: Long): List<Image> {
        val storageImages = notesDao.getImages(idImageItem)
        val listImages = arrayListOf<Image>()
        storageImages.forEach {
            val image = storageImageMapToDomain(it)
            listImages.add(image)
        }
        return listImages
    }





    @Transaction
    override fun setImageItemWithImages(item: ImageItem, imageList: List<Image>?): Long {
        val storageImageItem = imageItemMapToStorage(item)
        val id = notesDao.insertImageItem(storageImageItem)
        if (imageList != null) {
            val listStorageImage = listImageMapToStorage(imageList)
            listStorageImage.forEach {
                it.foreignId = id
                notesDao.insertImage(it)
            }
        }
        return id
    }

    override fun updateImageItem(imageItem: ImageItem) {
        val listImages = imageItem.listImageItems
        val storageImageItem = imageItemMapToStorage(imageItem)
        val storageImagesList = listImageMapToStorage(listImages)

        storageImagesList.forEach { storageImage ->
            if (storageImage.foreignId == 0L){
                storageImage.foreignId = imageItem.imageItemId
                notesDao.insertImage(storageImage)
            }
            if (!storageImage.isNew){
                notesDao.updateImage(storageImage)
            }
        }

        notesDao.updateImageItem(storageImageItem)
    }

    override fun deleteImageItem(imageItem: ImageItem) {
        val storageImageItem = imageItemMapToStorage(imageItem)
        notesDao.deleteImageItem(storageImageItem)
    }

    private fun imageItemMapToStorage(imageItem: ImageItem): StorageImageItem {
        return StorageImageItem(
            imageItemId = imageItem.imageItemId,
            foreignId = imageItem.foreignId,
            position = imageItem.position
        )
    }

    private fun storageImageItemMapToDomain(storageImage: StorageImageItem): ImageItem {
        return ImageItem(
            imageItemId = storageImage.imageItemId,
            foreignId = storageImage.foreignId,
            position = storageImage.position,
            listImageItems = getImageFromImageItem(storageImage.imageItemId)
        )
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
}