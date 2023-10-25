package com.example.data.storage.room.mappers

import com.easynote.domain.models.TextItem
import com.example.data.storage.EntityMapper
import com.example.data.storage.room.models.StorageTextItem

class TextItemMapper : EntityMapper<TextItem, StorageTextItem> {

    override fun mapToDomain(entity: StorageTextItem) = TextItem(
        itemTextId = entity.itemTextId,
        foreignId = entity.foreignId,
        text = entity.text,
        position = entity.position
    )

    override fun mapToStorage(model: TextItem) = StorageTextItem(
        itemTextId = model.itemTextId,
        foreignId = model.foreignId,
        text = model.text,
        position = model.position
    )
}
