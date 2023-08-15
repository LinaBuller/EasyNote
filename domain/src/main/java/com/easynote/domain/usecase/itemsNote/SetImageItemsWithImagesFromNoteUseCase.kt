package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.repository.NoteRepository

class SetImageItemsWithImagesFromNoteUseCase(private val noteRepository: NoteRepository) {
    fun execute(imageItem: ImageItem, listImage:List<Image>):Long{
       return noteRepository.setImageItemWithImages(imageItem,listImage)
    }
}