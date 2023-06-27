package com.easynote.domain.usecase

import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class DeleteNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {

    fun execute(note: com.easynote.domain.models.Note) {
        noteRepository.deleteNote(note)
    }
}