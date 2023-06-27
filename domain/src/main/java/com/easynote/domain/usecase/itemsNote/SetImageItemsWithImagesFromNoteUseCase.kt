package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.repository.NoteRepository

class SetImageItemsWithImagesFromNoteUseCase(private val noteRepository: com.easynote.domain.repository.NoteRepository) {
    fun execute(imageItem: com.easynote.domain.models.ImageItem, listImage:List<com.easynote.domain.models.Image>):Long{
       return noteRepository.setImageItemWithImages(imageItem,listImage)
    }
}