package com.example.data.storage.room.mappers

import com.easynote.domain.models.FavoriteColor
import com.example.data.storage.EntityMapper
import com.example.data.storage.room.models.StorageFavoriteColor

class FavColorMapper : EntityMapper<FavoriteColor, StorageFavoriteColor> {
    override fun mapToDomain(entity: StorageFavoriteColor) = FavoriteColor(
        id = entity.id,
        number = entity.number,
        h = entity.h,
        s = entity.s,
        l = entity.l
    )

    override fun mapToStorage(model: FavoriteColor) = StorageFavoriteColor(
        id = model.id,
        number = model.number,
        h = model.h,
        s = model.s,
        l = model.l
    )
}