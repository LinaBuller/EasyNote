package com.buller.mysqlite.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.buller.mysqlite.data.ConstantsDbName

data class CategoriesWithNote(
    @Embedded
    var category: Category,
    @Relation(
        parentColumn = ConstantsDbName.CATEGORY_ID,
        entityColumn = ConstantsDbName.NOTE_ID,

        associateBy = Junction(
            value = NoteWithCategoriesCrossRef::class,
            parentColumn = ConstantsDbName.CATEGORY_ID,
            entityColumn = ConstantsDbName.NOTE_ID
        )
    )
    var listOfNotes: List<Note>? = null
)