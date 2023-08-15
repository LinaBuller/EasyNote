package com.easynote.domain.viewmodels

import androidx.appcompat.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.TextItem
import com.easynote.domain.usecase.DeleteNoteUseCase
import com.easynote.domain.usecase.GetListNotesUseCase
import com.easynote.domain.usecase.UpdateNoteUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.DeleteTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetImageItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetImagesFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.GetTextItemsFromNoteUseCase
import com.easynote.domain.utils.BuilderQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class RecycleBinFragmentViewModel(
    val getListNotesUseCase: GetListNotesUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val getTextItemsFromNoteUseCase: GetTextItemsFromNoteUseCase,
    val getImageItemsFromNoteUseCase: GetImageItemsFromNoteUseCase,
    val getImagesFromImageItemUseCase: GetImagesFromImageItemUseCase,
    val deleteTextItemFromNoteUseCase: DeleteTextItemFromNoteUseCase,
    val deleteImageFromImageItemUseCase: DeleteImageFromImageItemUseCase,
    val deleteImageItemFromNoteUseCase: DeleteImageItemFromNoteUseCase
) : ViewModel() {

    private val mediatorNotes = MediatorLiveData<List<Note>>()
    val readAllNotes: LiveData<List<Note>> get() = mediatorNotes
    private var lastNotes: LiveData<List<Note>>? = null

    fun loadNotes() {
        //default filters
        val query = BuilderQuery.buildQueryRecycleBin()

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

    private fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNoteUseCase.execute(note)
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

    fun updateStatusNote(isDelete: Boolean?) {
        var selectedNote = selectedNote.value
        if (isDelete != null && selectedNote != null) {
            selectedNote = selectedNote.copy(isDeleted = isDelete)
            updateNote(selectedNote)
            clearSelectedNote()
        }
    }

    fun deleteSelectedNote() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedNote.value?.let {
                deleteTextItem(it.id)
                deleteImageItem(it.id)
                deleteNote(it)
                clearSelectedNote()
            }
        }
    }

    private fun clearSelectedNote() {
        selectedNote.postValue(null)
    }


    //getting note items (TextItem, ImageItem) for deleting in database and external image files
    private fun deleteTextItem(noteId: Long) {
        val deletingTextItem = getTextItemsFromNote(noteId)
        deletingTextItem.forEach {
            deleteTextItemFromNoteUseCase.execute(it as TextItem)
        }
    }

    private fun getTextItemsFromNote(id: Long): List<MultiItem> {
        return getTextItemsFromNoteUseCase.execute(id)
    }

    private fun deleteImageItem(noteId: Long) {
        val deletingImageItem = selectImageInImageItem(noteId)
        deletingImageItem.forEach {
            deleteImageFiles(it)
        }
    }

    private fun deleteImageFiles(imageItem: ImageItem) {

        val listSavedImages = getImageFromImageItem(imageItem.imageItemId)

        listSavedImages.forEach { savedImage ->

            val deletingImage = File(savedImage.uri)
            if (deletingImage.exists()) {
                deletingImage.delete()
            }

            deleteImageFromImageItem(savedImage)

        }
        deleteImageItemFromNoteUseCase.execute(imageItem)
    }

    private fun deleteImageFromImageItem(image: Image) {
        deleteImageFromImageItemUseCase.execute(image)
    }

    private fun selectImageInImageItem(id: Long): List<ImageItem> {
        val listImageItems = getImageItemsFromNote(id)

        listImageItems.forEach { imageItem ->
            val listImage = getImageFromImageItem(imageItem.imageItemId)
            imageItem.listImageItems = listImage as ArrayList<Image>
        }

        return listImageItems
    }

    private fun getImageItemsFromNote(id: Long): List<ImageItem> {
        return getImageItemsFromNoteUseCase.execute(id)
    }

    private fun getImageFromImageItem(idImageItem: Long): List<Image> {
        return getImagesFromImageItemUseCase.execute(idImageItem)
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
            deleteNote(it)
        }
        clearSelectedNotesFromActionMode()
    }


    fun restoreSelectedNotesFromActionMode() {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.forEach {
            it.isDeleted = false
            updateNote(it)
        }
        clearSelectedNotesFromActionMode()
    }

    fun clearSelectedNotesFromActionMode() {
        _selectedNotesFromActionMode.value = mutableSetOf()
    }
}