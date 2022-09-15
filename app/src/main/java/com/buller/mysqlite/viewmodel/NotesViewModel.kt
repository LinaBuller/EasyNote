package com.buller.mysqlite.viewmodel


import android.app.Application
import androidx.lifecycle.*
import com.buller.mysqlite.data.NotesDatabase
import com.buller.mysqlite.model.*
import com.buller.mysqlite.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    var readAllNotes: LiveData<List<Note>>
    val readAllCategories: LiveData<List<Category>>

    var editedNote = MutableLiveData<Note>()
    val editedImages = MutableLiveData<List<Image>?>()
    val editedColorsFields = MutableLiveData<List<Int>>()
    val editedNewCategory = MutableLiveData<List<Category>>()
    val editedSelectCategoryFromAddFragment = MutableLiveData<List<Category>>()


    private val repository: NotesRepository
    var id = 0

    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val categoryEventChannel = Channel<CategoryEvent>()
    val categoryEvent = categoryEventChannel.receiveAsFlow()


    init {
        val noteDao = NotesDatabase.getDatabase(application).noteDao()
        repository = NotesRepository(noteDao)
        readAllNotes = repository.readAllNotes
        readAllCategories = repository.readAllCategories
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

    fun addOrUpdateNoteWithImages(
        note: Note,
        images: List<Image>?,
        categories: List<Category>?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                //new note
                val id = addNote(note)
                note.id = id
            } else {
                updateNote(note)
            }

            if (images != null) {
                saveImages(note, images)
            }
            if (categories != null) {
                saveCategories(note, categories)
            }
        }
    }

    private fun saveCategories(note: Note, categories: List<Category>) {
        repository.saveNoteWithCategory(note, categories)
    }

    private fun saveImages(note: Note, images: List<Image>) {
        repository.insertNoteWithImage(note.id, images)
    }


    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
        }
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

    fun onNoteSwipe(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.isDeleted) {
                noteEventChannel.send(NoteEvent.ShowUndoDeleteNoteMessage(note))
            } else {
                noteEventChannel.send(NoteEvent.ShowUndoRestoreNoteMessage(note))
            }
        }
    }

    fun onUndoClickNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.isDeleted) {
                note.isDeleted = false
                updateNote(note)
            } else {
                note.isDeleted = true
                updateNote(note)
            }

        }
    }

    fun onUndoDeleteClickCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(category)
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

    fun onCategorySwipe(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
            categoryEventChannel.send(CategoryEvent.ShowUndoDeleteCategoryMessage(category))
        }
    }

    fun addCategory(category: Category): Long {
        val categoryId = repository.insertCategory(category)
        category.idCategory = categoryId
        return categoryId
    }

    fun selectEditedCategory(listSelectedCategory: List<Category>) {
        editedSelectCategoryFromAddFragment.value = listSelectedCategory
    }

    fun selectEditedCategoryPost(listSelectedCategory: List<Category>) {
        editedSelectCategoryFromAddFragment.postValue(listSelectedCategory)
    }

    fun cleanSelectedCategories() {
        editedSelectCategoryFromAddFragment.value = listOf()
    }

    suspend fun noteWithImages(idNote: Long): NoteWithImages {
        return repository.getNoteWithImages(idNote)
    }

    suspend fun noteWithCategories(id: Long): NoteWithCategories {
        return repository.getNoteWithCategories(id)
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
        }
    }

    sealed class NoteEvent {
        data class ShowUndoDeleteNoteMessage(val note: Note) : NoteEvent()
        data class ShowUndoRestoreNoteMessage(val note: Note) : NoteEvent()
    }

    sealed class CategoryEvent {
        data class ShowUndoDeleteCategoryMessage(val category: Category) : CategoryEvent()

    }
}

