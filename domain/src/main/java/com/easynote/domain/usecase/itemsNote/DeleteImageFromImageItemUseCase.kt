package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.repository.NoteRepository

class DeleteImageFromImageItemUseCase(private val noteRepository: NoteRepository) {
    fun execute(image: Image){
        noteRepository.deleteImage(image)
    }
}