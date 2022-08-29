package com.buller.mysqlite.viewmodel


import android.app.Application
import androidx.lifecycle.*
import com.buller.mysqlite.data.NotesDatabase
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.repository.NotesRepository
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.model.NoteWithImagesWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    var editedNote = MutableLiveData<Note>()
    val editedImages = MutableLiveData<List<Image>?>()
    val editedColorsFields = MutableLiveData<List<Int>>()

    val readAllNotes: LiveData<List<Note>>
    private val repository: NotesRepository
    var id = 0

    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()


    init {
        val noteDao = NotesDatabase.getDatabase(application).noteDao()
        repository = NotesRepository(noteDao)
        readAllNotes = repository.readAllNotes
    }

    fun addNote(note: Note): Long {
        return repository.insertNote(note)
    }

    fun selectEditedNote(note: Note) {
        editedNote.value = note
    }

    fun selectEditedImagesPost(images: List<Image>?) {
        editedImages.postValue(images)
    }

    fun selectEditedImages(images: List<Image>?) {
        editedImages.value = images
    }

    fun addOrUpdateNoteWithImages(note: Note, images: List<Image>?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                //new note
                if (images == null) {
                    addNote(note)
                } else {
                    addNoteWithImage(note, images)
                }

            } else {
                // update
                deleteNote(note.id)
                if (images == null) {
                    addNote(note)
                } else {
                    addNoteWithImage(note, images)
                }
            }

        }
    }

    fun addNoteWithImage(note: Note, listImage: List<Image>?) {
        repository.insertNoteWithImage(NoteWithImagesWrapper(note, listImage))
    }

    suspend fun noteWithImages(idNote: Long): NoteWithImagesWrapper {
        return repository.getNoteWithImages(idNote)
    }

    fun clearEditImages() {
        editedImages.value = listOf()
    }

    fun selectColorFieldsNote(colors: List<Int>) {
        editedColorsFields.value = colors
    }

    fun cleanSelectedColors() {
        editedColorsFields.value = listOf(0, 0)
    }


    private fun deleteNote(id: Long) {
        repository.deleteNote(id)
    }

    fun onNoteSwipe(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
            noteEventChannel.send(NoteEvent.ShowUndoDeleteNoteMessage(note))
        }
    }

    fun onUndoDeleteClick(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
        }
    }

    fun deleteImage(image: Image) {
        val list: ArrayList<Image> = ArrayList()
        editedImages.value?.let { list.addAll(it) }
        list.removeIf { imageSelect ->
            imageSelect.uri.contains(image.uri)
        }
        selectEditedImages(list)
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteImage(image)
        }
    }

    sealed class NoteEvent {
        data class ShowUndoDeleteNoteMessage(val note: Note) : NoteEvent()
    }
}
