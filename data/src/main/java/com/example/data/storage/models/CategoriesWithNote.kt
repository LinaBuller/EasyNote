package com.example.data.storage.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.data.storage.ConstantsDbName

data class CategoriesWithNote(
    @Embedded
    var category: StorageCategory,
    @Relation(
        parentColumn = ConstantsDbName.CATEGORY_ID,
        entityColumn = ConstantsDbName.NOTE_ID,

        associateBy = Junction(
            value = NoteWithCategoriesCrossRef::class,
            parentColumn = ConstantsDbName.CATEGORY_ID,
            entityColumn = ConstantsDbName.NOTE_ID
        )
    )
    var listOfNotes: List<StorageNote>? = null
)