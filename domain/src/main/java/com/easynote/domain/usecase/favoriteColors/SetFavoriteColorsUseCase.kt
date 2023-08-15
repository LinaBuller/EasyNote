package com.easynote.domain.usecase.favoriteColors

import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.repository.NoteRepository

class SetFavoriteColorsUseCase(private val noteRepository: NoteRepository) {
    fun execute(favoriteColors: List<FavoriteColor>) {
        noteRepository.setFavoritesColor(favoriteColors)
    }
}