package com.buller.mysqlite.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import com.buller.mysqlite.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    //get all notes
    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id!=:id")
    fun getNotes(id: Long): LiveData<List<Note>>

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id")
    fun getNote(id: Long): Note

    @Insert
    fun insertNote(note: Note): Long

    @Insert
    fun insertImage(image: Image): Long

    @Update
    fun updateNote(note: Note)


    @Delete()
    fun deleteNote(note: Note)

    @Query("DELETE FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:idNote")
    fun deleteNote(idNote: Long)

    //    @Transaction
//    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id IN(" +
//            "SELECT DISTINCT(${ConstantsDbName.IMAGES_FOREIGN_ID}) FROM ${ConstantsDbName.IMAGES_TABLE_NAME})")
//    fun getNoteWithImages(id:Long): Flow<NoteWithImagesWrapper>


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


    @Query("DELETE  FROM notewithcategoriescrossref WHERE note_id=:idNote")
    fun deleteCrossFromDeleteNote(idNote: Long)

    @Query("DELETE FROM notewithcategoriescrossref WHERE note_id=:idNote AND category_id NOT IN (:list)")
    fun deleteNotExistCategory(list: List<Long>, idNote: Long)

    @Query("DELETE FROM ${ConstantsDbName.IMAGES_TABLE_NAME} WHERE foreign_id=:idNote AND image_id NOT IN (:list)")
    fun deleteNotExistImages(list: List<Long>, idNote: Long)


//    @Query("SELECT * FROM ${ConstantsDbName.CATEGORY_TABLE_NAME} WHERE category_id=:idCategory")
//    fun getCategoryWithNote(idCategory: Long)
}