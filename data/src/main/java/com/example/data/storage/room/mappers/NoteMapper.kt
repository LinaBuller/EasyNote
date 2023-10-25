package com.example.data.storage.room.mappers

import com.easynote.domain.models.Note
import com.example.data.storage.EntityMapper
import com.example.data.storage.room.models.StorageNote

class NoteMapper: EntityMapper<Note, StorageNote> {
    override fun mapToDomain(entity: StorageNote)= Note (
        id = entity.id,
        title = entity.title,
        content = entity.content,
        text = entity.text,
        time = entity.time,
        gradientColorFirst = entity.gradientColorFirst,
        gradientColorFirstH = entity.gradientColorFirstH,
        gradientColorFirstS = entity.gradientColorFirstS,
        gradientColorFirstL = entity.gradientColorFirstL,
        gradientColorSecond = entity.gradientColorSecond,
        gradientColorSecondH = entity.gradientColorSecondH,
        gradientColorSecondS = entity.gradientColorSecondS,
        gradientColorSecondL = entity.gradientColorSecondL,
        isDeleted = entity.isDeleted,
        isPin = entity.isDeleted,
        isArchive = entity.isArchive,
        isFavorite = entity.isFavorite
    )

    override fun mapToStorage(model: Note)= StorageNote (
        id = model.id,
        title = model.title,
        content = model.content,
        text = model.text,
        time = model.time,
        gradientColorFirst = model.gradientColorFirst,
        gradientColorFirstH = model.gradientColorFirstH,
        gradientColorFirstS = model.gradientColorFirstS,
        gradientColorFirstL = model.gradientColorFirstL,
        gradientColorSecond = model.gradientColorSecond,
        gradientColorSecondH = model.gradientColorSecondH,
        gradientColorSecondS = model.gradientColorSecondS,
        gradientColorSecondL = model.gradientColorSecondL,
        isDeleted = model.isDeleted,
        isPin = model.isDeleted,
        isArchive = model.isArchive,
        isFavorite = model.isFavorite
    )
}