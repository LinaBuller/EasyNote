package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class SetPreferredThemeUseCase(private val noteRepository: NoteRepository) {
    fun execute(preferredTheme:Boolean) {
        return noteRepository.setPreferredThemeSharPref(preferredTheme)
    }
}