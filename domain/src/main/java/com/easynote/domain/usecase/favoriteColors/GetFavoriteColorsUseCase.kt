package com.easynote.domain.usecase.favoriteColors


import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.repository.NoteRepository

class GetFavoriteColorsUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute():List<com.easynote.domain.models.FavoriteColor> {
        return noteRepository.getFavoriteColor()
    }
}