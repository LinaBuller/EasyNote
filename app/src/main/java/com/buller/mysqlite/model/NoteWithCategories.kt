package com.buller.mysqlite.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.buller.mysqlite.data.ConstantsDbName

data class NoteWithCategories(
    @Embedded
    var note: Note,
    @Relation(
        parentColumn = ConstantsDbName.NOTE_ID,
        entityColumn = ConstantsDbName.CATEGORY_ID,

        associateBy = Junction(
            value = NoteWithCategoriesCrossRef::class,
            parentColumn = ConstantsDbName.NOTE_ID,
            entityColumn = ConstantsDbName.CATEGORY_ID
        )
    )
    var listOfCategories: List<Category>? = null
)