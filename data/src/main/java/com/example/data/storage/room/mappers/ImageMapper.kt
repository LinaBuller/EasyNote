package com.example.data.storage.room.mappers

import com.easynote.domain.models.Image
import com.example.data.storage.EntityMapper
import com.example.data.storage.room.models.StorageImage

class ImageMapper : EntityMapper<Image, StorageImage> {
    override fun mapToDomain(entity: StorageImage) = Image(
        id = entity.id,
        foreignId = entity.foreignId,
        uri = entity.uri,
        isNew = false,
        position = entity.position
    )

    override fun mapToStorage(model: Image) = StorageImage(
        id = model.id,
        foreignId = model.foreignId,
        uri = model.uri,
        isNew = model.isNew,
        position = model.position
    )
}