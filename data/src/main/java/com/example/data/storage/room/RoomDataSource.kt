package com.example.data.storage.room

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
import com.example.data.storage.room.mappers.CategoryListMapper
import com.example.data.storage.room.mappers.CategoryMapper
import com.example.data.storage.room.mappers.FavColorListMapper
import com.example.data.storage.room.mappers.FavColorMapper
import com.example.data.storage.room.mappers.ImageItemMapper
import com.example.data.storage.room.mappers.ImageListMapper
import com.example.data.storage.room.mappers.ImageMapper
import com.example.data.storage.room.mappers.NoteListMapper
import com.example.data.storage.room.mappers.NoteMapper
import com.example.data.storage.room.mappers.TextItemMapper
import com.example.data.storage.room.models.StorageFavoriteColor
import com.example.data.storage.room.models.StorageNoteWithCategoriesCrossRef
import com.example.data.storage.DataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RoomDataSource(private var notesDao: NotesDao, val localDatabase: LocalDatabase) : DataSource,
    KoinComponent {

    private val noteMapper: NoteMapper by inject<NoteMapper>()
    private val noteListMapper: NoteListMapper by inject<NoteListMapper>()
    private val favColorMapper: FavColorMapper by inject<FavColorMapper>()
    private val favColorListMapper: FavColorListMapper by inject<FavColorListMapper>()
    private val textItemMapper: TextItemMapper by inject<TextItemMapper>()
    private val imageMapper: ImageMapper by inject<ImageMapper>()
    private val imageListMapper: ImageListMapper by inject<ImageListMapper>()
    private val imageItemMapper: ImageItemMapper by inject<ImageItemMapper>()
    private val categoryMapper: CategoryMapper by inject<CategoryMapper>()
    private val categoryListMapper: CategoryListMapper by inject<CategoryListMapper>()


    fun getNotes(query: SimpleSQLiteQuery): LiveData<List<Note>> {

        val storageNotes = notesDao.getNotes(query)
        val mediatorNotes = MediatorLiveData<List<Note>>()
        mediatorNotes.addSource(storageNotes) {
            val noteList = arrayListOf<Note>()
            noteList.addAll(noteListMapper.mapToDomain(storageNotes.value!!))
            mediatorNotes.value = noteList
        }
        return mediatorNotes
    }

     fun setNote(note: Note): Long {
        val noteSt = noteMapper.mapToStorage(note)
        return notesDao.insertNote(noteSt)
    }

    fun updateNote(note: Note) {
        val noteSt = noteMapper.mapToStorage(note)
        notesDao.updateNote(noteSt)
    }

    fun deleteNote(note: Note) {
        val noteSt = noteMapper.mapToStorage(note)
        notesDao.deleteNote(noteSt)
    }

    fun getCategories(): LiveData<List<Category>> {
        val listStorageCategory = notesDao.getCategories()
        val mediatorNotes = MediatorLiveData<List<Category>>()
        mediatorNotes.addSource(listStorageCategory) {
            val arrayList = arrayListOf<Category>()
            arrayList.addAll(categoryListMapper.mapToDomain(it))
            mediatorNotes.value = arrayList
        }
        return mediatorNotes
    }

    fun setCategory(category: Category): Long {
        val categoryStr = categoryMapper.mapToStorage(category)
        return notesDao.insertCategory(categoryStr)
    }

    fun deleteCategory(category: Category) {
        val categoryStr = categoryMapper.mapToStorage(category)
        notesDao.deleteCategory(categoryStr)
    }

    fun updateCategory(category: Category) {
        val categoryStr = categoryMapper.mapToStorage(category)
        notesDao.update(categoryStr)
    }

    suspend fun getNoteWithCategoriesObj(noteId: Long): NoteWithCategories {
        val noteWithCategories = notesDao.getNoteWithCategory(noteId)
        val note = noteMapper.mapToDomain(noteWithCategories.note)
        val categories = arrayListOf<Category>()
        noteWithCategories.listOfCategories?.forEach {
            val category = categoryMapper.mapToDomain(it)
            categories.add(category)
        }
        return NoteWithCategories(
            note = note,
            listOfCategories = categories
        )
    }

    fun setNoteWithCategory(note: Note, category: List<NoteWithCategoriesCrossRef>) {
        val list = arrayListOf<StorageNoteWithCategoriesCrossRef>()
        val listIdCategory = arrayListOf<Long>()
        category.forEach {
            list.add(StorageNoteWithCategoriesCrossRef(note.id, it.category_id))
            listIdCategory.add(it.category_id)
        }
        notesDao.deleteNotExistCategory(listIdCategory, note.id)
        notesDao.insertManyNoteWithCategoriesCrossRef(list)
    }

    fun updateNoteWithCategories(note: Note, categories: List<Category>) {
        categories.forEach {
            notesDao.update(StorageNoteWithCategoriesCrossRef(note.id, it.idCategory))
        }
    }

    fun getFavoriteColor(): LiveData<List<FavoriteColor>> {
        val storageFavoriteColor = notesDao.getFavoritesColor()
        val mediatorFavColors = MediatorLiveData<List<FavoriteColor>>()
        mediatorFavColors.addSource(storageFavoriteColor) {
            val arrayList = arrayListOf<FavoriteColor>()
            arrayList.addAll(favColorListMapper.mapToDomain(storageFavoriteColor.value!!))
            mediatorFavColors.value = arrayList
        }
        return mediatorFavColors
    }

    fun setFavoritesColor(listColor: List<FavoriteColor>) {
        val listStorageCategory = arrayListOf<StorageFavoriteColor>()
        listColor.forEach {
            val storageFavoriteColor = favColorMapper.mapToStorage(it)
            listStorageCategory.add(storageFavoriteColor)
        }
        notesDao.insertFavoritesColor(listStorageCategory)
    }

    fun deleteFavoriteColor(favoriteColor: FavoriteColor) {
        val storageFavoriteColor = favColorMapper.mapToStorage(favoriteColor)
        notesDao.deleteFavoritesColor(storageFavoriteColor)
    }

    fun getItemsTextFromNote(idNote: Long): List<MultiItem> {
        val listStorageTextItem = notesDao.getItemsText(idNote)
        val listTextItem = arrayListOf<MultiItem>()
        listStorageTextItem.forEach {
            val textItem = textItemMapper.mapToDomain(it)
            listTextItem.add(textItem)
        }
        return listTextItem
    }

    fun setTextItemFromNote(textItem: TextItem): Long {
        val storageTextItem = textItemMapper.mapToStorage(textItem)
        return notesDao.insertTextItemFromNote(storageTextItem)
    }

    fun updateTextItemFromNote(textItem: TextItem) {
        val storageTextItem = textItemMapper.mapToStorage(textItem)
        notesDao.updateTextItem(storageTextItem)
    }

    fun deleteTextItemFromNote(textItem: TextItem) {
        val storageTextItem = textItemMapper.mapToStorage(textItem)
        notesDao.deleteTextItem(storageTextItem)
    }

    fun deleteImage(image: Image) {
        val storageImage = imageMapper.mapToStorage(image)
        notesDao.deleteImage(storageImage)
    }

    fun getImageItemsFromNote(idNote: Long): List<ImageItem> {
        val storageImageItem = notesDao.getImageItems(idNote)

        val listImageItem = arrayListOf<ImageItem>()
        storageImageItem.forEach {
            val imageItem = imageItemMapper.mapToDomain(it)
            imageItem.listImageItems = getImageFromImageItem(imageItem.imageItemId)
            listImageItem.add(imageItem)
        }
        return listImageItem
    }

    fun getImageFromImageItem(idImageItem: Long): List<Image> {
        val storageImages = notesDao.getImages(idImageItem)
        return imageListMapper.mapToDomain(storageImages)
    }


    @Transaction
    fun setImageItemWithImages(item: ImageItem, imageList: List<Image>?): Long {
        val storageImageItem = imageItemMapper.mapToStorage(item)
        val id = notesDao.insertImageItem(storageImageItem)
        if (imageList != null) {
            val listStorageImage = imageListMapper.mapToStorage(imageList)
            listStorageImage.forEach {
                it.foreignId = id
                it.isNew = false
                notesDao.insertImage(it)
            }
        }
        imageList?.forEach {
            it.isNew = false
        }
        return id
    }

    fun updateImageItem(item: ImageItem) {
        val storageImageItem = imageItemMapper.mapToStorage(item)
        val listImages = item.listImageItems
        val storageImagesList = imageListMapper.mapToStorage(listImages)

        storageImagesList.forEach { storageImage ->
            if (storageImage.foreignId == 0L) {
                storageImage.foreignId = item.imageItemId
                notesDao.insertImage(storageImage)
            } else {
                if (storageImage.isNew){
                    notesDao.updateImage(storageImage)
                    storageImage.isNew = false
                }
            }
        }

        listImages.forEach {
            it.isNew = false
        }
        notesDao.updateImageItem(storageImageItem)
    }

    fun deleteImageItem(item: ImageItem) {
        val storageImageItem = imageItemMapper.mapToStorage(item)
        notesDao.deleteImageItem(storageImageItem)
    }

    fun getPath(): String? {
        return localDatabase.openHelper.readableDatabase.path
    }

    fun checkpointDatabase() {
        val db = localDatabase.openHelper.writableDatabase
        db.query("PRAGMA wal_checkpoint(FULL);", emptyArray())
        db.query("PRAGMA wal_checkpoint(TRUNCATE);", emptyArray())
    }

    fun getAllImages():List<Image> {
        return imageListMapper.mapToDomain(notesDao.getAllImages())
    }
}