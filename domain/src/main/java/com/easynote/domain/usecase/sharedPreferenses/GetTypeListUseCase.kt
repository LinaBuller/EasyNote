package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class GetTypeListUseCase(private val noteRepository: NoteRepository) {
    fun execute(): Boolean {
        return noteRepository.getTypeListSharPref()
    }
}