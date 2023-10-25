package com.easynote.domain.usecase.categories

import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategories
import com.easynote.domain.repository.NoteRepository

class GetNoteWithCategoriesUseCase(private val noteRepository: NoteRepository) {

    suspend fun execute(noteId: Long): NoteWithCategories {
        return noteRepository.getNoteWithCategories(noteId)
    }

    suspend fun execute(note: Note): NoteWithCategories {
        return noteRepository.getNoteWithCategories(note)
    }
}