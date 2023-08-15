package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.TextItem
import com.easynote.domain.repository.NoteRepository

class DeleteTextItemFromNoteUseCase (private val noteRepository: NoteRepository){

    fun execute(textItem:TextItem) {
        noteRepository.deleteTextItemFromNote(textItem)
    }
}