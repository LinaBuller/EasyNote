package com.easynote.domain.usecase.categories

import com.easynote.domain.models.Category
import com.easynote.domain.repository.NoteRepository

class SetCategoriesUseCase(private val noteRepository: NoteRepository) {

    fun execute(category: Category): Long {
        return noteRepository.setCategory(category)
    }
}