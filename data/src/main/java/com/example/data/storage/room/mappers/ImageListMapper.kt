package com.example.data.storage.room.mappers

import com.easynote.domain.models.Image
import com.example.data.storage.ListEntityMapper
import com.example.data.storage.room.models.StorageImage

class ImageListMapper(private val mapper: ImageMapper):
    ListEntityMapper<List<Image>, List<StorageImage>> {
    override fun mapToDomain(entity: List<StorageImage>): List<Image> {
        val listToDomain = arrayListOf<Image>()
        entity.forEach {
            val itemDomain = mapper.mapToDomain(it)
            listToDomain.add(itemDomain)
        }
        return listToDomain
    }

    override fun mapToStorage(model: List<Image>): List<StorageImage> {
        val listToEntity = arrayListOf<StorageImage>()
        model.forEach {
            val itemEntity = mapper.mapToStorage(it)
            listToEntity.add(itemEntity)
        }
        return listToEntity
    }
}