package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class GetIsFirstUsagesUseCase(private val noteRepository: NoteRepository) {

    fun execute():Boolean {
        return noteRepository.getIsFirstUsagesSharPref()
    }
}