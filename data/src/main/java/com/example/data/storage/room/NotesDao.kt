package com.example.data.storage.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.data.storage.ConstantsDbName

import com.example.data.storage.room.models.StorageImageItem
import com.example.data.storage.room.models.StorageNoteWithCategories
import com.example.data.storage.room.models.StorageNoteWithCategoriesCrossRef
import com.example.data.storage.room.models.StorageCategory
import com.example.data.storage.room.models.StorageFavoriteColor
import com.example.data.storage.room.models.StorageImage
import com.example.data.storage.room.models.StorageNote
import com.example.data.storage.room.models.StorageTextItem

@Dao
interface NotesDao {

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id")
    fun getNote(id: Long): StorageNote

    @RawQuery(observedEntities = [StorageNote::class])
    fun getNotes(query: SupportSQLiteQuery): LiveData<List<StorageNote>>

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_title LIKE :searchQuery OR note_content LIKE:searchQuery")
    fun getSearchText(searchQuery: String): LiveData<List<StorageNote>>

    @Insert()
    fun insertNote(note: StorageNote): Long

    @Insert()
    fun insertImage(image: StorageImage): Long

    @Update
    fun updateNote(note: StorageNote)

    @Delete()
    fun deleteNote(note: StorageNote)

    @Query("DELETE FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:idNote")
    fun deleteNote(idNote: Long)


    @Update
    fun updateImage(image: StorageImage)

    @Delete
    fun deleteImage(image: StorageImage)

    @Insert
    fun insertFavoritesColor(list: List<StorageFavoriteColor>)

    @Query("SELECT * FROM ${ConstantsDbName.FAV_COLOR_TABLE_NAME}")
    fun getFavoritesColor(): LiveData<List<StorageFavoriteColor>>

    @Delete
    fun deleteFavoritesColor(color: StorageFavoriteColor)

    @Insert
    fun insertCategory(category: StorageCategory): Long

    @Query("SELECT * FROM ${ConstantsDbName.CATEGORY_TABLE_NAME}")
    fun getCategories(): LiveData<List<StorageCategory>>

    @Query("SELECT * FROM ${ConstantsDbName.CATEGORY_TABLE_NAME} WHERE category_id=:idCategory")
    fun getCategory(idCategory: Long): StorageCategory

    @Delete
    fun deleteCategory(category: StorageCategory)

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:idNote")
    suspend fun getNoteWithCategory(idNote: Long): StorageNoteWithCategories

    @Insert(onConflict = IGNORE)
    fun insert(storageNoteWithCategoriesCrossRef: StorageNoteWithCategoriesCrossRef): Long

    @Insert(onConflict = IGNORE)
    fun insertManyNoteWithCategoriesCrossRef(list: List<StorageNoteWithCategoriesCrossRef>): LongArray

    @Delete
    fun delete(storageNoteWithCategoriesCrossRef: StorageNoteWithCategoriesCrossRef): Int

    @Update
    fun update(storageNoteWithCategoriesCrossRef: StorageNoteWithCategoriesCrossRef)

    @Update
    fun update(category: StorageCategory)

    @Query("DELETE FROM storagenotewithcategoriescrossref WHERE note_id=:idNote AND category_id NOT IN (:list)")
    fun deleteNotExistCategory(list: List<Long>, idNote: Long)

    @Query("DELETE FROM ${ConstantsDbName.IMAGES_TABLE_NAME} WHERE foreign_id=:idNote AND image_id NOT IN (:list)")
    fun deleteNotExistImages(list: List<Long>, idNote: Long)


    //получить итемы с текстом
    @Query("SELECT * FROM ${ConstantsDbName.ITEMS_TEXT_TABLE_NAME} WHERE text_item_foreign_id=:idNote")
    fun getItemsText(idNote: Long):List<StorageTextItem>

    @Insert
    fun insertTextItemFromNote(item: StorageTextItem): Long

    @Update
    fun updateTextItem(item: StorageTextItem)

    @Delete
    fun deleteTextItem(item: StorageTextItem)

    //получить итемы с картинкам
    @Query("SELECT * FROM ${ConstantsDbName.ITEMS_IMAGE_TABLE_NAME} WHERE image_item_foreign_id=:idNote")
    fun getImageItems(idNote: Long):List<StorageImageItem>

    @Query("SELECT * FROM ${ConstantsDbName.IMAGES_TABLE_NAME} WHERE foreign_id=:foreignId")
    fun getImages(foreignId: Long): List<StorageImage>

    @Insert
    fun insertImageItem(item: StorageImageItem):Long

    @Update
    fun updateImageItem(item: StorageImageItem)

    @Insert
    fun insertImages(item: List<StorageImage>)

    @Delete
    fun deleteImageItem(imageItem: StorageImageItem)

    @Query("SELECT * FROM ${ConstantsDbName.IMAGES_TABLE_NAME}")
    fun getAllImages(): List<StorageImage>
}