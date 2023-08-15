package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class SetTypeListUseCase(private val noteRepository: NoteRepository) {

    fun execute(typeList: Boolean) {
        return noteRepository.setTypeListSharPref(typeList)
    }

}