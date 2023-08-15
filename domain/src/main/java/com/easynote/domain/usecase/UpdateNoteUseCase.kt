package com.easynote.domain.usecase

import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class UpdateNoteUseCase(private val noteRepository: NoteRepository) {
    fun execute(note:Note) {
        noteRepository.updateNote(note)
    }
}