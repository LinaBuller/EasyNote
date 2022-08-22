package com.buller.mysqlite.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.buller.mysqlite.data.ConstantsDbName

data class NoteWithImagesWrapper(
    @Embedded
    var note: Note,
    @Relation(
        parentColumn = ConstantsDbName.NOTE_ID,
        entityColumn = ConstantsDbName.IMAGES_FOREIGN_ID,
        entity = Image::class
    )
    var listOfImages: List<Image>? = null
)