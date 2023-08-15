package com.easynote.domain.viewmodels

import androidx.appcompat.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.Note

import com.easynote.domain.usecase.GetListNotesUseCase
import com.easynote.domain.usecase.UpdateNoteUseCase
import com.easynote.domain.utils.BuilderQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArchiveFragmentViewModel(
    val getListNotesUseCase: GetListNotesUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
) : ViewModel() {

    private val mediatorNotes = MediatorLiveData<List<Note>>()
    val readAllNotes: LiveData<List<Note>> get() = mediatorNotes
    private var lastNotes: LiveData<List<Note>>? = null

    fun loadNotes() {
        //default filters
        val query = BuilderQuery.buildQueryArchive()

        val listNotes = getListNotesUseCase.execute(query)
        lastNotes?.let { mediatorNotes.removeSource(it) }
        mediatorNotes.addSource(listNotes) {
            mediatorNotes.value = it
        }
        lastNotes = listNotes

    }

    private fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNoteUseCase.execute(note)
        }
    }

    private var selectedNote = MutableLiveData<Note?>()


    fun getSelected(selectedId: Long) {
        readAllNotes.value?.forEach {
            if (it.id == selectedId) {
                selectedNote.value = it
            }
        }
    }

    fun updateStatusNote(isDelete: Boolean? = null, isArchive: Boolean? = null) {
        var selectedNote = selectedNote.value
        if (isDelete != null && selectedNote != null) {
            selectedNote = selectedNote.copy(isDeleted = isDelete)
        }
        if (isArchive != null && selectedNote != null) {
            selectedNote = selectedNote.copy(isArchive = isArchive)
        }
        if (selectedNote != null) {
            updateNote(selectedNote)
        }
        clearSelectedNote()
    }

    private fun clearSelectedNote() {
        selectedNote.postValue(null)
    }


    var actionMode: ActionMode? = null
    private val _selectedNotesFromActionMode = MutableLiveData<MutableSet<Note>>()
    val selectedNotesFromActionMode: LiveData<MutableSet<Note>> get() = _selectedNotesFromActionMode

    fun changeSelectedNotesFromActionMode(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        if (list.contains(selectedNote)) {
            removeSelectedNotesFromActionMode(selectedNote)
        } else {
            addSelectedNotesFromActionMode(selectedNote)
        }
    }

    private fun removeSelectedNotesFromActionMode(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.remove(selectedNote)
        _selectedNotesFromActionMode.value = list
    }

    private fun addSelectedNotesFromActionMode(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.add(selectedNote)
        _selectedNotesFromActionMode.value = list
    }

    fun deleteSelectionNotesFromActionMode() {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.forEach {
            it.isArchive = false
            it.isDeleted = true
            updateNote(it)
        }
        clearSelectedNotesFromActionMode()
    }

    fun unarchiveSelectedNotesFromActionMode() {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.forEach {
            it.isArchive = false
            updateNote(it)
        }
        clearSelectedNotesFromActionMode()
    }

    fun clearSelectedNotesFromActionMode() {
        _selectedNotesFromActionMode.value = mutableSetOf()
    }


}