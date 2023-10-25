package com.example.data.storage.room.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import com.example.data.storage.ConstantsDbName

@Entity(
    primaryKeys = [ConstantsDbName.NOTE_ID, ConstantsDbName.CATEGORY_ID],
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            entity = StorageNote::class,
            parentColumns = arrayOf("note_id"),
            childColumns = arrayOf(ConstantsDbName.NOTE_ID)
        ),


        ForeignKey(
            onDelete = CASCADE,
            entity = StorageCategory::class,
            parentColumns = arrayOf("category_id"),
            childColumns = arrayOf(ConstantsDbName.CATEGORY_ID)
        )
    ],
    indices = [Index(value = [ConstantsDbName.NOTE_ID, ConstantsDbName.CATEGORY_ID], unique = true)]
)
class StorageNoteWithCategoriesCrossRef(
    val note_id: Long = 0,
    val category_id: Long = 0
)