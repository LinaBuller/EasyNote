package com.easynote.domain.usecase

import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository


class SetNoteUseCase(private val noteRepository:NoteRepository) {

    suspend fun execute(note: Note): Long {
        return noteRepository.setNote(note)
    }
}