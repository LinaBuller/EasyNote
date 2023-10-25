package com.example.data.storage.room.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.data.storage.ConstantsDbName

data class StorageNoteWithCategories(
    @Embedded
    var note: StorageNote,
    @Relation(
        parentColumn = ConstantsDbName.NOTE_ID,
        entityColumn = ConstantsDbName.CATEGORY_ID,

        associateBy = Junction(
            value = StorageNoteWithCategoriesCrossRef::class,
            parentColumn = ConstantsDbName.NOTE_ID,
            entityColumn = ConstantsDbName.CATEGORY_ID
        )
    )
    var listOfCategories: List<StorageCategory>? = null
)