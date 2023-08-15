package com.easynote.domain.usecase.favoriteColors


import androidx.lifecycle.LiveData
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.repository.NoteRepository

class GetFavoriteColorsUseCase(private val noteRepository: NoteRepository) {
    fun execute():LiveData<List<FavoriteColor>> {
        return noteRepository.getFavoriteColor()
    }
}