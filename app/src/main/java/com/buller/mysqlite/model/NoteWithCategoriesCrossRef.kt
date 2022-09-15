package com.buller.mysqlite.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import com.buller.mysqlite.data.ConstantsDbName

@Entity(
    primaryKeys = [ConstantsDbName.NOTE_ID, ConstantsDbName.CATEGORY_ID],
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            entity = Note::class,
            parentColumns = arrayOf("note_id"),
            childColumns = arrayOf(ConstantsDbName.NOTE_ID)
        ),


        ForeignKey(
            onDelete = CASCADE,
            entity = Category::class,
            parentColumns = arrayOf("category_id"),
            childColumns = arrayOf(ConstantsDbName.CATEGORY_ID)
        )
    ],
    indices = [Index(value = [ConstantsDbName.NOTE_ID, ConstantsDbName.CATEGORY_ID], unique = true)]
)
class NoteWithCategoriesCrossRef(
    val note_id: Long = 0,
    val category_id: Long = 0
)