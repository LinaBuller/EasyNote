package com.example.data.storage.room.mappers

import com.easynote.domain.models.Category
import com.example.data.storage.EntityMapper
import com.example.data.storage.room.models.StorageCategory

class CategoryMapper : EntityMapper<Category, StorageCategory> {
    override fun mapToDomain(entity: StorageCategory) = Category(
        idCategory = entity.idCategory,
        titleCategory = entity.titleCategory,
        position = entity.position
    )

    override fun mapToStorage(model: Category) = StorageCategory(
        idCategory = model.idCategory,
        titleCategory = model.titleCategory,
        position = model.position
    )
}