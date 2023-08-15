package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.repository.NoteRepository

class UpdateImageItemFromNoteUseCase(private val noteRepository: NoteRepository) {
    fun execute(imageItem: ImageItem) {
        noteRepository.updateImageItem(imageItem)
    }
}