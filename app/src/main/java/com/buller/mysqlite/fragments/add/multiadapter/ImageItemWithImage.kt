package com.buller.mysqlite.fragments.add.multiadapter

import androidx.room.Embedded
import androidx.room.Relation
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.model.Image


data class ImageItemWithImage(
    @Embedded
    var itemImage: ImageItem,

    @Relation(
        parentColumn = ConstantsDbName.ITEMS_IMAGE_ID,
        entityColumn = ConstantsDbName.IMAGES_FOREIGN_ID,
        entity = Image::class
    )
    var listOfImages: List<Image>? = null
)