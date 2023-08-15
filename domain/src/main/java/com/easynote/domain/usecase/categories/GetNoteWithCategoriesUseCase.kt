package com.easynote.domain.usecase.categories

import com.easynote.domain.models.Category
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategoriesCrossRefModel
import com.easynote.domain.models.NoteWithCategoriesModel
import com.easynote.domain.repository.NoteRepository

class GetNoteWithCategoriesUseCase(private val noteRepository: NoteRepository) {

    suspend fun execute(noteId: Long): NoteWithCategoriesModel {
        return noteRepository.getNoteWithCategories(noteId)
    }

    suspend fun execute(note: Note): NoteWithCategoriesModel {
        return noteRepository.getNoteWithCategories(note)
    }
}