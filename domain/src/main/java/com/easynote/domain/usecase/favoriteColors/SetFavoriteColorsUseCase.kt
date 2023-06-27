package com.easynote.domain.usecase.favoriteColors

import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.repository.NoteRepository

class SetFavoriteColorsUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute(favoriteColors: List<com.easynote.domain.models.FavoriteColor>) {
        noteRepository.setFavoritesColor(favoriteColors)
    }
}