package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class GetPreferredThemeUseCase(private val noteRepository: NoteRepository) {

    fun execute(): Boolean {
        return noteRepository.getPreferredThemeSharPref()
    }

}