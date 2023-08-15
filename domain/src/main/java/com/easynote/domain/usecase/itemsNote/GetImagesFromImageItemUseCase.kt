package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.repository.NoteRepository

class GetImagesFromImageItemUseCase(private val noteRepository: NoteRepository) {
    fun execute(imageItemId:Long): List<Image>{
        return noteRepository.getImageFromImageItem(imageItemId)
    }
}