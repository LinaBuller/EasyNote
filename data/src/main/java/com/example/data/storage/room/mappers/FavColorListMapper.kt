package com.example.data.storage.room.mappers

import com.easynote.domain.models.FavoriteColor
import com.example.data.storage.ListEntityMapper
import com.example.data.storage.room.models.StorageFavoriteColor

class FavColorListMapper(private val mapper: FavColorMapper):
    ListEntityMapper<List<FavoriteColor>, List<StorageFavoriteColor>> {
    override fun mapToDomain(entity: List<StorageFavoriteColor>): List<FavoriteColor> {
        val listToDomain = arrayListOf<FavoriteColor>()
        entity.forEach {
            val itemDomain = mapper.mapToDomain(it)
            listToDomain.add(itemDomain)
        }
        return listToDomain
    }

    override fun mapToStorage(model: List<FavoriteColor>): List<StorageFavoriteColor> {
        val listToEntity = arrayListOf<StorageFavoriteColor>()
        model.forEach {
            val itemEntity = mapper.mapToStorage(it)
            listToEntity.add(itemEntity)
        }
        return listToEntity
    }
}