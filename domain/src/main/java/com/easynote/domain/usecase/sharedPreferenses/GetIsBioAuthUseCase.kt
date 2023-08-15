package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class GetIsBioAuthUseCase(private val noteRepository: NoteRepository) {

    fun execute(): Boolean {
        return noteRepository.getIsBioAuthSharedPref()
    }
}