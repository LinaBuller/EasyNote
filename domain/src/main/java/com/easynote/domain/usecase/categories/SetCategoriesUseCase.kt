package com.easynote.domain.usecase.categories

import com.easynote.domain.models.Category
import com.easynote.domain.repository.NoteRepository

class SetCategoriesUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {

    fun execute(category: com.easynote.domain.models.Category): Long {
        return noteRepository.setCategory(category)
    }
}