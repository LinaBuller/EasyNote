package com.easynote.domain.usecase

import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository


class SetNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {

    fun execute(note: com.easynote.domain.models.Note): Long {
        return noteRepository.setNote(note)
    }
}