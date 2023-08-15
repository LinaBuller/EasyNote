package com.easynote.domain.usecase.sharedPreferenses

import com.easynote.domain.repository.NoteRepository

class SetIsFirstUsagesUseCase(private val noteRepository: NoteRepository)  {
    fun execute(isFirst:Boolean){
        return noteRepository.setIsFirstUsagesSharPref(isFirst)
    }
}