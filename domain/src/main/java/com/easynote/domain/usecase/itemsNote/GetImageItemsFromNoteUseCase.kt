package com.easynote.domain.usecase.itemsNote

import androidx.lifecycle.LiveData
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class GetImageItemsFromNoteUseCase(private val noteRepository: NoteRepository) {
    fun execute(id:Long): List<ImageItem>{
       return noteRepository.getImageItemsFromNote(id)
    }
}