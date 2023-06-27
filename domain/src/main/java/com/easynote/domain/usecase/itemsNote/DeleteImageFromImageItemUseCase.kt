package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class DeleteImageFromImageItemUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute(image: com.easynote.domain.models.Image){
        noteRepository.deleteImageFromImageItem(image)
    }
}