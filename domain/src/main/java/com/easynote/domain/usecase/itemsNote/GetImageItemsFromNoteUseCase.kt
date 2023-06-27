package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class GetImageItemsFromNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute(note: com.easynote.domain.models.Note): List<com.easynote.domain.models.ImageItem>{
       return noteRepository.getImageItemsFromNote(note.id)
    }
}