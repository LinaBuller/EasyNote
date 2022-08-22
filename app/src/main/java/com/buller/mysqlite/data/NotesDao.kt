package com.buller.mysqlite.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.model.NoteWithImagesWrapper
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    //get all notes
    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME}")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id")
    fun getNote(id: Long): Flow<Note>

    @Insert
    fun insertNote(note: Note): Long

    @Insert
    fun insertImage(image: Image): Long

    @Update
    fun updateNote(note: Note)

    @Update
    fun updateImage(image: Image)

    @Delete
    fun deleteNote(note: Note)

    @Query("DELETE FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:idNote")
    fun deleteNote(idNote:Long)

    //    @Transaction
//    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id IN(" +
//            "SELECT DISTINCT(${ConstantsDbName.IMAGES_FOREIGN_ID}) FROM ${ConstantsDbName.IMAGES_TABLE_NAME})")
//    fun getNoteWithImages(id:Long): Flow<NoteWithImagesWrapper>

    @Transaction
    fun insertNoteWithImage(noteWithImagesWrapper: NoteWithImagesWrapper) {
        val idNote = insertNote(noteWithImagesWrapper.note)
        val listImage = noteWithImagesWrapper.listOfImages
        if (listImage != null) {
            if (listImage.isNotEmpty()){
                listImage.forEach { image->
                    image.foreignId = idNote
                    insertImage(image)
                }
            }
        }
    }

    @Query("SELECT * FROM ${ConstantsDbName.NOTE_TABLE_NAME} WHERE note_id=:id")
    suspend fun getNoteWithImages(id: Long):NoteWithImagesWrapper


}