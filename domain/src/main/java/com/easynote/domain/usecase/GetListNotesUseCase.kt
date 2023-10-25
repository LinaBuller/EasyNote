package com.easynote.domain.usecase

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.easynote.domain.models.Note
import com.easynote.domain.repository.NoteRepository


class GetListNotesUseCase(private val noteRepository: NoteRepository) {


    suspend fun execute(query: SimpleSQLiteQuery): LiveData<List<Note>> {
        return  noteRepository.getNotes(query)
    }

}