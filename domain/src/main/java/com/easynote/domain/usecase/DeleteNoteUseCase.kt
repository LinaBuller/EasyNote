package com.easynote.domain.usecase

import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class DeleteNoteUseCase(private val noteRepository: NoteRepository) {

    fun execute(note:Note) {
        noteRepository.deleteNote(note)
    }
}