package com.easynote.domain.usecase.itemsNote

import androidx.lifecycle.LiveData
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository

class GetTextItemsFromNoteUseCase(private val noteRepository:NoteRepository) {

    fun execute(id:Long):List<MultiItem> {
        return noteRepository.getItemsTextFromNote(id)
    }
}