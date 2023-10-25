package com.easynote.domain.usecase.favoriteColors

import com.easynote.domain.repository.NoteRepository

class DeleteFavoriteColorsUseCase(private val noteRepository: NoteRepository) {
    fun execute(favoriteColor: com.easynote.domain.models.FavoriteColor) {
        noteRepository.deleteFavoriteColor(favoriteColor)
    }
}