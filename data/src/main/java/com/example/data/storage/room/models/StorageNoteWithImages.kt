package com.example.data.storage.room.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.storage.ConstantsDbName

data class StorageNoteWithImages(
    @Embedded
    var note: StorageNote,
    @Relation(
        parentColumn = ConstantsDbName.NOTE_ID,
        entityColumn = ConstantsDbName.IMAGES_FOREIGN_ID,
        entity = StorageImage::class
    )
    var listOfImages: List<StorageImage>? = null
)