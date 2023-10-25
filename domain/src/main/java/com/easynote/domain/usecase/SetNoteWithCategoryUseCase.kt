package com.easynote.domain.usecase

import com.easynote.domain.models.NoteWithCategoriesCrossRef
import com.easynote.domain.repository.NoteRepository

class SetNoteWithCategoryUseCase(private val noteRepository: NoteRepository) {

    fun execute(
        note: com.easynote.domain.models.Note,
        categories: List<com.easynote.domain.models.Category>
    ) {
        val list = arrayListOf<NoteWithCategoriesCrossRef>()
        val listIdCategory = arrayListOf<Long>()
        categories.forEach {
            list.add(
               NoteWithCategoriesCrossRef(
                    note.id,
                    it.idCategory
                )
            )
            listIdCategory.add(it.idCategory)
        }
        noteRepository.setNoteWithCategory(note, list)
    }
}