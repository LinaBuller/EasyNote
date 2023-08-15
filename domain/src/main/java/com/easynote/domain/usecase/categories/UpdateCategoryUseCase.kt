package com.easynote.domain.usecase.categories

import com.easynote.domain.models.Category
import com.easynote.domain.repository.NoteRepository

class UpdateCategoryUseCase(private val noteRepository: NoteRepository) {

    fun execute(category:Category) {
        noteRepository.updateCategory(category)
    }
}