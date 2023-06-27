package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.repository.NoteRepository

class UpdateImageItemFromNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute(imageItem: com.easynote.domain.models.ImageItem, imageList: List<com.easynote.domain.models.Image>) {
        noteRepository.updateImageItem(imageItem, imageList)
    }
}