package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.TextItem
import com.easynote.domain.repository.NoteRepository

class SetTextItemFromNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute(textItem: com.easynote.domain.models.TextItem):Long {
        return noteRepository.setTextItemFromNote(textItem)
    }
}