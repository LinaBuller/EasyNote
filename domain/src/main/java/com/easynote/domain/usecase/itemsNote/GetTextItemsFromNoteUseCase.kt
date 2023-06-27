package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class GetTextItemsFromNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {

    fun execute(note: com.easynote.domain.models.Note):List<com.easynote.domain.models.MultiItem> {
        return noteRepository.getItemsTextFromNote(note.id)
    }
}