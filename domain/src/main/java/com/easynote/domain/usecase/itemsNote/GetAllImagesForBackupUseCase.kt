package com.easynote.domain.usecase.itemsNote

import com.easynote.domain.models.Image
import com.easynote.domain.repository.NoteRepository

class GetAllImagesForBackupUseCase(private val noteRepository: NoteRepository) {
    fun execute():List<Image> {
        return noteRepository.getAllImages()
    }
}