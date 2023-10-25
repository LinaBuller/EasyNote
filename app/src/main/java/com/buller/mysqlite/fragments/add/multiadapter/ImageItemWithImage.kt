package com.buller.mysqlite.fragments.add.multiadapter

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.storage.ConstantsDbName
import com.easynote.domain.models.Image
import com.example.data.storage.room.models.StorageImageItem


data class ImageItemWithImage(
    @Embedded
    var itemImage: StorageImageItem,

    @Relation(
        parentColumn = ConstantsDbName.ITEMS_IMAGE_ID,
        entityColumn = ConstantsDbName.IMAGES_FOREIGN_ID,
        entity =Image::class
    )
    var listOfImages: List<Image>? = null
)