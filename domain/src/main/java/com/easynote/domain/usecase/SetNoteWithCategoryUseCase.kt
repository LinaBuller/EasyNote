package com.easynote.domain.usecase

import com.easynote.domain.models.Category
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategoriesCrossRefModel
import com.easynote.domain.repository.NoteRepository

class SetNoteWithCategoryUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {

    fun execute(
        note: com.easynote.domain.models.Note,
        categories: List<com.easynote.domain.models.Category>
    ) {
        val list = arrayListOf<com.easynote.domain.models.NoteWithCategoriesCrossRefModel>()
        val listIdCategory = arrayListOf<Long>()
        categories.forEach {
            list.add(
                com.easynote.domain.models.NoteWithCategoriesCrossRefModel(
                    note.id,
                    it.idCategory
                )
            )
            listIdCategory.add(it.idCategory)
        }
        noteRepository.setNoteWithCategory(note, list)
    }
}