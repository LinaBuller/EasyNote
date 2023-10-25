package com.example.data.storage.room.mappers

import com.easynote.domain.models.Note
import com.example.data.storage.ListEntityMapper
import com.example.data.storage.room.models.StorageNote

class NoteListMapper(private val mapper: NoteMapper):
    ListEntityMapper<List<Note>, List<StorageNote>> {
    override fun mapToDomain(entity: List<StorageNote>): List<Note> {
        val listToDomain = arrayListOf<Note>()
        entity.forEach {
            val itemDomain = mapper.mapToDomain(it)
            listToDomain.add(itemDomain)
        }
        return listToDomain
    }

    override fun mapToStorage(model: List<Note>): List<StorageNote> {
        val listToEntity = arrayListOf<StorageNote>()
        model.forEach {
            val itemEntity = mapper.mapToStorage(it)
            listToEntity.add(itemEntity)
        }
        return listToEntity
    }


}