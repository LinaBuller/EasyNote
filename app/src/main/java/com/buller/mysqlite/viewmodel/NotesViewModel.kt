package com.buller.mysqlite.viewmodel


import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.buller.mysqlite.data.NotesDatabase
import com.buller.mysqlite.model.*
import com.buller.mysqlite.repository.NotesRepository
import com.buller.mysqlite.utils.BuilderQuery
import com.buller.mysqlite.utils.CurrentTimeInFormat
import com.buller.mysqlite.utils.edittextnote.OnUndoRedo
import com.buller.mysqlite.utils.theme.CurrentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class NotesViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DEFAULT_SORT_COLUMN = "n.is_pin"
        const val DEFAULT_SORT_ORDER = 1
        const val DEFAULT_FILTER_CATEGORY_ID = -1L
    }

    private val _application = application
    private val repository: NotesRepository
    private val noteDao = NotesDatabase.getDatabase(_application)?.noteDao()

    private val mediatorNotes = MediatorLiveData<List<Note>>()

    //var readAllNotes: LiveData<List<Note>> = mediatorNotes
    val readAllNotes: LiveData<List<Note>> get() = mediatorNotes

    private var lastNotes: LiveData<List<Note>>? = null

    val readAllCategories: LiveData<List<Category>>
    var favColor: LiveData<List<FavoriteColor>>

    val editedImages = MutableLiveData<List<Image>?>()

    private val _editedSelectCategoryFromDialogMoveCategory = MutableLiveData<List<Category>?>()
    val editedSelectCategoryFromDialogMoveCategory: LiveData<List<Category>?> get() =_editedSelectCategoryFromDialogMoveCategory

    val currentColorsFields = MutableLiveData<List<Int>>()
    val editedColorsFields = MutableLiveData<List<Int>>()

    var id = 0

    private val _filterCategoryId: MutableLiveData<Long>
    val filterCategoryId: LiveData<Long>

    private val _sortColumn: MutableLiveData<String>
    private val sortColumn: LiveData<String>

    private val _sortOrder: MutableLiveData<Int>
    private val sortOrder: LiveData<Int>

    private val _searchText: MutableLiveData<String>
    private val searchText: LiveData<String>

    private val _currentTheme: MutableLiveData<CurrentTheme>
    val currentTheme: LiveData<CurrentTheme>

    private var _selectedNote = MutableLiveData<Note?>()
    val selectedNote: LiveData<Note?> get() = _selectedNote

    private var _selectedCategory = MutableLiveData<Category?>()
    val selectedCategory: LiveData<Category?> get() = _selectedCategory

    private val _currentKindOfList = MutableLiveData<Boolean>()
    val currentKindOfList: LiveData<Boolean> get() = _currentKindOfList


    private val _selectedItemsFromActionMode = MutableLiveData<MutableSet<Note>>()
    val selectedItemsFromActionMode: LiveData<MutableSet<Note>> get() = _selectedItemsFromActionMode

    init {
        repository = noteDao?.let { NotesRepository(it) }!!
        _currentTheme = MutableLiveData(CurrentTheme(0))
        currentTheme = _currentTheme
        _filterCategoryId = MutableLiveData(DEFAULT_FILTER_CATEGORY_ID)
        filterCategoryId = _filterCategoryId
        _sortColumn = MutableLiveData(DEFAULT_SORT_COLUMN)
        sortColumn = _sortColumn
        _sortOrder = MutableLiveData(DEFAULT_SORT_ORDER)
        sortOrder = _sortOrder
        _searchText = MutableLiveData("")
        searchText = _searchText
        readAllCategories = repository.readAllCategories
        favColor = repository.favoriteColor

        loadNotes()

    }

    fun changeSelectedItem(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedItemsFromActionMode.value != null) {
            list.addAll(_selectedItemsFromActionMode.value!!)
        }
        if (list.contains(selectedNote)) {
            removeSelectedItem(selectedNote)
        } else {
            addSelectedItem(selectedNote)
        }
    }

    private fun removeSelectedItem(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedItemsFromActionMode.value != null) {
            list.addAll(_selectedItemsFromActionMode.value!!)
        }
        list.remove(selectedNote)
        _selectedItemsFromActionMode.value = list
    }

    private fun addSelectedItem(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedItemsFromActionMode.value != null) {
            list.addAll(_selectedItemsFromActionMode.value!!)
        }
        list.add(selectedNote)
        _selectedItemsFromActionMode.value = list
    }

    fun deleteOrUpdateSelectionItems() {
        val list = mutableSetOf<Note>()
        if (_selectedItemsFromActionMode.value != null) {
            list.addAll(_selectedItemsFromActionMode.value!!)
        }
        list.forEach {
            if (it.isArchive){
                it.isArchive = false
                it.isDeleted = true
                updateNote(it)
            }else{
                deleteNote(it)
            }
        }
        clearSelectedItems()
    }
     fun unarchiveSelectedItems(){
         val list = mutableSetOf<Note>()
         if (_selectedItemsFromActionMode.value != null) {
             list.addAll(_selectedItemsFromActionMode.value!!)
         }
         list.forEach {
             it.isArchive = false
             updateNote(it)
         }
         clearSelectedItems()
     }

    fun restoreSelectedItems(){
        val list = mutableSetOf<Note>()
        if (_selectedItemsFromActionMode.value != null) {
            list.addAll(_selectedItemsFromActionMode.value!!)
        }
        list.forEach {
            it.isDeleted = false
            updateNote(it)
        }
        clearSelectedItems()
    }

    fun clearSelectedItems() {
        _selectedItemsFromActionMode.value = mutableSetOf()
    }

    fun setCurrentKindOfList(kindOfList: Boolean) {
        _currentKindOfList.value = kindOfList
    }

    val undo: ArrayDeque<OnUndoRedo> = ArrayDeque()
    val redo: ArrayDeque<OnUndoRedo> = ArrayDeque()

    fun clearUndoRedo() {
        undo.clear()
        redo.clear()
    }

    fun changeTheme(id: Int) {
        val currentThemeNow: CurrentTheme? = _currentTheme.value
        if (currentThemeNow != null) {
            _currentTheme.value = currentThemeNow.copy(themeId = id)
        }
    }

    fun saveData(title: String, content: String, text: String) {
        val listImage: List<Image>? = editedImages.value
        val listColors: List<Int>? = currentColorsFields.value
        val listCategories: List<Category>? = editedSelectCategoryFromDialogMoveCategory.value
        var currentNote = selectedNote.value
        currentNote = currentNote!!.copy(
            title = title,
            content = content,
            text = text,
            time = CurrentTimeInFormat.getCurrentTime(),
            colorFrameTitle = listColors?.get(0) ?: 0,
            colorFrameContent = listColors?.get(1) ?: 0
        )
        addOrUpdateNoteWithImages(
            currentNote,
            listImage,
            listCategories
        )
    }

    fun setSelectedNote(selectedNote: Note) {
        _selectedNote.value = selectedNote
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

    private fun addNote(note: Note): Long {
        return repository.insertNote(note)
    }


    fun selectEditedImagesPost(images: List<Image>?) {
        editedImages.postValue(images)
    }

    fun selectEditedImages(images: List<Image>?) {
        editedImages.value = images
    }

    fun addSelectedImagesToViewModel(uris: List<Uri>) {
        val newImageList = arrayListOf<Image>()
        if (editedImages.value != null) {
            newImageList.addAll(editedImages.value!!)
        }
        uris.forEach { newUri ->
            newImageList.add(Image(0, 0, newUri.toString()))
        }
        selectEditedImages(newImageList)
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

    fun saveCategories(note: Note, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNoteWithCategory(note, categories)
        }
    }

    private fun saveImages(note: Note, images: List<Image>) {
        repository.saveNoteWithImage(note.id, images)
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

    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(category)
        }
    }

    fun setSelectedCategoryFromItemList() {
        val selectedNote = selectedNote.value
        viewModelScope.launch {
        if (selectedNote != null) {
            val selectedCategory = getNoteWithCategories(selectedNote.id)
            if (selectedCategory.listOfCategories != null) {
                selectEditedCategoryPost(selectedCategory.listOfCategories!!)
            } else {
                cleanSelectedCategories()
            }
        }
        }
    }
    fun clearSelectedNote(){
        _selectedNote.value = null
        _editedSelectCategoryFromDialogMoveCategory.value = null
    }

    fun changeCheckboxCategory(category: Category, isChecked: Boolean) {
        val newList = arrayListOf<Category>()
        val list = _editedSelectCategoryFromDialogMoveCategory.value
        if (list != null) {
            newList.addAll(list)
        }
        if (isChecked) {
            newList.add(category)
        } else {
            newList.remove(category)
        }
        selectEditedCategory(newList)
    }

    fun updateCategoryFromItemList() {
        if (selectedNote.value != null && _editedSelectCategoryFromDialogMoveCategory.value != null) {
            saveCategories(selectedNote.value!!, _editedSelectCategoryFromDialogMoveCategory.value!!)
        }
    }

    fun selectEditedCategory(listSelectedCategory: List<Category>) {
        _editedSelectCategoryFromDialogMoveCategory.value = listSelectedCategory
    }

    fun selectEditedCategoryPost(listSelectedCategory: List<Category>) {
        _editedSelectCategoryFromDialogMoveCategory.postValue(listSelectedCategory)
    }

    fun cleanSelectedCategories() {
        _editedSelectCategoryFromDialogMoveCategory.value = listOf()
    }

    suspend fun noteWithImages(idNote: Long): NoteWithImages {
        return repository.getNoteWithImages(idNote)
    }

    suspend fun getNoteWithCategories(id: Long): NoteWithCategories {
        return repository.getNoteWithCategories(id)
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }

    fun deleteNotes(notes: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            notes.forEach { note ->
                repository.deleteNote(note)
            }
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

    fun updateStatusNote(isDelete: Boolean?, isArchive: Boolean?) {
        var selectedNote = selectedNote.value
        if (isDelete!=null && selectedNote != null){
            selectedNote = selectedNote.copy(isDeleted = isDelete)
        }
        if (isArchive!=null && selectedNote != null){
            selectedNote = selectedNote.copy(isArchive = isArchive)
        }
        if (selectedNote != null){
            updateNote(selectedNote)
        }
        _selectedNote = MutableLiveData()
    }

    fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }
    fun clearSelectedCategory(){
        _selectedCategory.value = null
    }

}


