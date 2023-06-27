package com.easynote.domain.usecase.categories

import com.easynote.domain.models.Category
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategoriesCrossRefModel
import com.easynote.domain.models.NoteWithCategoriesModel
import com.easynote.domain.repository.NoteRepository

class GetNoteWithCategoriesUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {

    suspend fun execute(note: com.easynote.domain.models.Note): com.easynote.domain.models.NoteWithCategoriesModel {
        return noteRepository.getNoteWithCategories(note.id)
    }
}