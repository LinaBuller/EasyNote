package com.buller.mysqlite.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.sqlite.db.SupportSQLiteQuery
import com.buller.mysqlite.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id")
    fun getNote(id: Long): Note

    @RawQuery(observedEntities = [Note::class])
    fun getNotes(query: SupportSQLiteQuery): LiveData<List<Note>>

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_title LIKE :searchQuery OR note_content LIKE:searchQuery")
    fun getSearchText(searchQuery: String): LiveData<List<Note>>

    @Insert()
    fun insertNote(note: Note): Long

    @Insert
    fun insertImage(image: Image): Long

    @Update
    fun updateNote(note: Note)

    @Delete()
    fun deleteNote(note: Note)

    @Query("DELETE FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:idNote")
    fun deleteNote(idNote: Long)

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id")
    suspend fun getNoteWithImages(id: Long): NoteWithImages

    @Update
    fun updateImage(image: Image)

    @Delete
    fun deleteImage(image: Image)

    @Transaction
    fun saveImagesOfNote(idNote: Long, listOfImages: List<Image>) {
        val idOfImages = arrayListOf<Long>()
        listOfImages.forEach {
            if (it.id != 0L) {
                idOfImages.add(it.id)
            }
        }
        deleteNotExistImages(idOfImages, idNote)

        if (listOfImages.isNotEmpty()) {
            listOfImages.forEach { image ->
                if (image.id == 0L) {
                    image.foreignId = idNote
                    insertImage(image)
                }
            }
        }
    }

    @Insert
    fun insertFavoritesColor(list: List<FavoriteColor>)

    @Query("SELECT * FROM ${ConstantsDbName.FAV_COLOR_TABLE_NAME}")
    fun getFavoritesColor(): Flow<List<FavoriteColor>>

    @Delete
    fun deleteFavoritesColor(color: FavoriteColor)

    @Insert
    fun insertCategory(category: Category): Long

    @Query("SELECT * FROM ${ConstantsDbName.CATEGORY_TABLE_NAME}")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT * FROM ${ConstantsDbName.CATEGORY_TABLE_NAME} WHERE category_id=:idCategory")
    fun getCategory(idCategory: Long): Category

    @Delete
    fun deleteCategory(category: Category)

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:idNote")
    suspend fun getNoteWithCategory(idNote: Long): NoteWithCategories

    @Insert(onConflict = IGNORE)
    fun insert(noteWithCategoriesCrossRef: NoteWithCategoriesCrossRef): Long

    @Insert(onConflict = IGNORE)
    fun insertManyNoteWithCategoriesCrossRef(list: List<NoteWithCategoriesCrossRef>): LongArray

    @Delete
    fun delete(noteWithCategoriesCrossRef: NoteWithCategoriesCrossRef): Int

    @Update
    fun update(noteWithCategoriesCrossRef: NoteWithCategoriesCrossRef)

    @Update
    fun update(category: Category)

    @Query("DELETE  FROM notewithcategoriescrossref WHERE note_id=:idNote")
    fun deleteCrossFromDeleteNote(idNote: Long)

    @Query("DELETE FROM notewithcategoriescrossref WHERE note_id=:idNote AND category_id NOT IN (:list)")
    fun deleteNotExistCategory(list: List<Long>, idNote: Long)

    @Query("DELETE FROM ${ConstantsDbName.IMAGES_TABLE_NAME} WHERE foreign_id=:idNote AND image_id NOT IN (:list)")
    fun deleteNotExistImages(list: List<Long>, idNote: Long)

}