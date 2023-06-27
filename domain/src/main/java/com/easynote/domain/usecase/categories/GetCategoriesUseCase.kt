package com.easynote.domain.usecase.categories

import androidx.lifecycle.LiveData
import com.easynote.domain.models.Category
import com.easynote.domain.repository.NoteRepository

class GetCategoriesUseCase(private val noteRepository: NoteRepository) {

    fun execute(): LiveData<List<Category>> {
        return noteRepository.getCategories()
    }

}