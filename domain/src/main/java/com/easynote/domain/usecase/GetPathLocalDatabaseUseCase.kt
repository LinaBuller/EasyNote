package com.easynote.domain.usecase

import com.easynote.domain.repository.NoteRepository

class GetPathLocalDatabaseUseCase(private val noteRepository: NoteRepository) {
    fun execute():String?{
        return noteRepository.getPath()
    }
}