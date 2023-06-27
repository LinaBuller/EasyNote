package com.buller.mysqlite.fragments.add.multiadapter

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.storage.ConstantsDbName
import com.easynote.domain.models.Image
import com.example.data.storage.models.StorageImageItem


data class ImageItemWithImage(
    @Embedded
    var itemImage: StorageImageItem,

    @Relation(
        parentColumn = ConstantsDbName.ITEMS_IMAGE_ID,
        entityColumn = ConstantsDbName.IMAGES_FOREIGN_ID,
        entity = com.easynote.domain.models.Image::class
    )
    var listOfImages: List<com.easynote.domain.models.Image>? = null
)