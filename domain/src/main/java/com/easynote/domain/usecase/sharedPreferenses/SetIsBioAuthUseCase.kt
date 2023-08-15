package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class SetIsBioAuthUseCase(private val noteRepository: NoteRepository) {

    fun execute(isBioAuth:Boolean) {
        noteRepository.setIsBioAuthSharedPref(isBioAuth)
    }

}