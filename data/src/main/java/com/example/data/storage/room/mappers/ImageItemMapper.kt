package com.example.data.storage.room.mappers

import com.easynote.domain.models.ImageItem
import com.example.data.storage.EntityMapper
import com.example.data.storage.room.models.StorageImageItem

class ImageItemMapper : EntityMapper<ImageItem, StorageImageItem> {
    override fun mapToDomain(entity: StorageImageItem) = ImageItem(
        imageItemId = entity.imageItemId,
        foreignId = entity.foreignId,
        position = entity.position
    )

    override fun mapToStorage(model: ImageItem) = StorageImageItem(
        imageItemId = model.imageItemId,
        foreignId = model.foreignId,
        position = model.position
    )
}