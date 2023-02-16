package com.buller.mysqlite.viewmodel


import android.app.Application
import androidx.lifecycle.*
import com.buller.mysqlite.data.NotesDatabase
import com.buller.mysqlite.model.*
import com.buller.mysqlite.repository.NotesRepository
import com.buller.mysqlite.utils.BuilderQuery
import com.buller.mysqlite.utils.theme.CurrentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class NotesViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DEFAULT_SORT_COLUMN = "n.note_id"
        const val DEFAULT_SORT_ORDER = 1
        const val DEFAULT_FILTER_CATEGORY_ID = -1L

    }

    private val _application = application
    private val repository: NotesRepository
    private val noteDao = NotesDatabase.getDatabase(_application).noteDao()

    private val mediatorNotes = MediatorLiveData<List<Note>>()
    var readAllNotes: LiveData<List<Note>> = mediatorNotes
    private var lastNotes: LiveData<List<Note>>? = null

    val readAllCategories: LiveData<List<Category>>
    var favColor: LiveData<List<FavoriteColor>>

    val editedImages = MutableLiveData<List<Image>?>()

    val editedSelectCategoryFromAddFragment = MutableLiveData<List<Category>>()

    val currentColorsFields = MutableLiveData<List<Int>>()
    val editedColorsFields = MutableLiveData<List<Int>>()

    var id = 0

    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val categoryEventChannel = Channel<CategoryEvent>()
    val categoryEvent = categoryEventChannel.receiveAsFlow()

    private val _filterCategoryId :MutableLiveData<Long>
    val filterCategoryId: LiveData<Long>

    private val _sortColumn:MutableLiveData<String>
    private val sortColumn: LiveData<String>

    private val _sortOrder: MutableLiveData<Int>
    private val sortOrder: LiveData<Int>

    private val _searchText:MutableLiveData<String>
    private val searchText: LiveData<String>

    private val _currentTheme: MutableLiveData<CurrentTheme>
    val currentTheme: LiveData<CurrentTheme>

    init {
        repository = NotesRepository(noteDao)
        _currentTheme = MutableLiveData(CurrentTheme(0))
        currentTheme = _currentTheme
        _filterCategoryId = MutableLiveData(DEFAULT_FILTER_CATEGORY_ID)
        filterCategoryId = _filterCategoryId
        _sortColumn = MutableLiveData(DEFAULT_SORT_COLUMN)
        sortColumn =  _sortColumn
        _sortOrder = MutableLiveData(DEFAULT_SORT_ORDER)
        sortOrder = _sortOrder
        _searchText = MutableLiveData("")
        searchText = _searchText
        readAllCategories = repository.readAllCategories
        favColor = repository.favoriteColor
        loadNotes()

    }


     fun changeTheme(id:Int){
         val currentThemeNow : CurrentTheme? = _currentTheme.value
         if (currentThemeNow!=null){
             _currentTheme.value = currentThemeNow.copy(themeId = id)
         }
    }
    private fun loadNotes() {
        val query = BuilderQuery.buildQuery(
            sortColumn.value!!,
            sortOrder.value!!,
            filterCategoryId.value!!,
            searchText.value!!
        )

        val listNotes = repository.getNotes(query)
        lastNotes?.let { mediatorNotes.removeSource(it) }
        mediatorNotes.addSource(listNotes) {
            mediatorNotes.value = it
        }
        lastNotes = listNotes

    }

    fun resetSort() {
        _sortColumn.value = DEFAULT_SORT_COLUMN
        _sortOrder.value = DEFAULT_SORT_ORDER
        loadNotes()
    }

    fun resetFilterCategoryId() {
        _filterCategoryId.value = DEFAULT_FILTER_CATEGORY_ID
        loadNotes()
    }

    fun setFilterCategoryId(id: Long) {
        _filterCategoryId.value = id
        loadNotes()
    }

    fun setSort(sortColumn: String, sortOrder: Int = DEFAULT_SORT_ORDER) {
        _sortColumn.value = sortColumn
        _sortOrder.value = sortOrder
        loadNotes()
    }

    fun setSearchText(text: String) {
        _searchText.value = text
        loadNotes()
    }




    fun addNote(note: Note): Long {
        return repository.insertNote(note)
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
        currentColorsFields.value = colors
    }

    fun cleanSelectedColors() {
        currentColorsFields.value = listOf(0, 0)
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

    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(category)
        }
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

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(category)
        }
    }

    fun deleteCategory(item: Category) {
        if (filterCategoryId.value == item.idCategory) {
            resetFilterCategoryId()
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(item)
        }
    }

    fun addFavoritesColors(listFavColors: List<FavoriteColor>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFavoritesColor(listFavColors)
        }
    }

    fun updateFavoritesColors(favoriteColor: FavoriteColor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavoritesColor(favoriteColor)
        }
    }

    fun deleteFavColor(idFavColor: FavoriteColor) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavColor(idFavColor)
        }
    }

    fun updateEditedFieldColor() {
        viewModelScope.launch(Dispatchers.IO) {
            editedColorsFields.postValue(currentColorsFields.value)

        }
    }

    fun updateCurrentFieldColor() {
        viewModelScope.launch(Dispatchers.IO) {
            currentColorsFields.postValue(editedColorsFields.value)
        }
    }

    fun changeColorField(const: Int, color: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentColor = editedColorsFields.value
            val arrayList = arrayListOf<Int>()
            arrayList.addAll(currentColor!!)
            if (const == 0) {
                arrayList[0] = color
                editedColorsFields.postValue(arrayList)
            }
            if (const == 1) {
                arrayList[1] = color
                editedColorsFields.postValue(arrayList)
            }
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


