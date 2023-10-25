package com.example.data.storage.room.mappers

import com.easynote.domain.models.Category
import com.example.data.storage.ListEntityMapper
import com.example.data.storage.room.models.StorageCategory

class CategoryListMapper(private val mapper: CategoryMapper) :
    ListEntityMapper<List<Category>, List<StorageCategory>> {

    override fun mapToDomain(entity: List<StorageCategory>): List<Category> {
        val listToDomain = arrayListOf<Category>()
        entity.forEach {
            val itemDomain = mapper.mapToDomain(it)
            listToDomain.add(itemDomain)
        }
        return listToDomain
    }

    override fun mapToStorage(model: List<Category>): List<StorageCategory> {
        val listToEntity = arrayListOf<StorageCategory>()
        model.forEach {
            val itemEntity = mapper.mapToStorage(it)
            listToEntity.add(itemEntity)
        }
        return listToEntity
    }
}