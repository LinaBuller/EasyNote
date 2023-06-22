package com.buller.mysqlite.viewmodel

import androidx.appcompat.view.ActionMode
import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.buller.mysqlite.data.NotesDatabase
import com.buller.mysqlite.fragments.add.multiadapter.ImageItem
import com.buller.mysqlite.fragments.add.multiadapter.MultiItem
import com.buller.mysqlite.fragments.add.multiadapter.TextItem
import com.buller.mysqlite.model.*
import com.buller.mysqlite.repository.NotesRepository
import com.buller.mysqlite.utils.BuilderQuery
import com.buller.mysqlite.utils.CurrentTimeInFormat
import com.buller.mysqlite.utils.edittextnote.CommandReplaceText
import com.buller.mysqlite.utils.edittextnote.OnUndoRedo
import com.buller.mysqlite.utils.theme.CurrentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
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
    val editedSelectCategoryFromDialogMoveCategory: LiveData<List<Category>?> get() = _editedSelectCategoryFromDialogMoveCategory

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


    var actionMode: ActionMode? = null

    private val _selectedNotesFromActionMode = MutableLiveData<MutableSet<Note>>()
    val selectedNotesFromActionMode: LiveData<MutableSet<Note>> get() = _selectedNotesFromActionMode

    private val _selectedItemsNoteFromActionMode = MutableLiveData<MutableSet<MultiItem>>()
    val selectedItemsNoteFromActionMode: LiveData<MutableSet<MultiItem>> get() = _selectedItemsNoteFromActionMode

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
        selectedNote.value?.let { unionLiveData(it.id) }
    }

    fun changeSelectedNotes(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        if (list.contains(selectedNote)) {
            removeSelectedNotes(selectedNote)
        } else {
            addSelectedNotes(selectedNote)
        }
    }

    private fun removeSelectedNotes(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.remove(selectedNote)
        _selectedNotesFromActionMode.value = list
    }

    private fun addSelectedNotes(selectedNote: Note) {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.add(selectedNote)
        _selectedNotesFromActionMode.value = list
    }

    fun deleteOrUpdateSelectionNotes() {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.forEach {
            if (it.isArchive) {
                it.isArchive = false
                it.isDeleted = true
                updateNote(it)
            } else {
                deleteNote(it)
            }
        }
        clearSelectedNotes()
    }

    fun unarchiveSelectedNotes() {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.forEach {
            it.isArchive = false
            updateNote(it)
        }
        clearSelectedNotes()
    }

    fun restoreSelectedNotes() {
        val list = mutableSetOf<Note>()
        if (_selectedNotesFromActionMode.value != null) {
            list.addAll(_selectedNotesFromActionMode.value!!)
        }
        list.forEach {
            it.isDeleted = false
            updateNote(it)
        }
        clearSelectedNotes()
    }

    fun clearSelectedNotes() {
        _selectedNotesFromActionMode.value = mutableSetOf()
    }


    fun changeSelectedItemsNote(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        if (list.contains(selectedItem)) {
            removeSelectedItemsNote(selectedItem)
        } else {
            addSelectedItemsNote(selectedItem)
        }
    }

    private fun removeSelectedItemsNote(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        list.remove(selectedItem)
        _selectedItemsNoteFromActionMode.value = list
    }

    private fun addSelectedItemsNote(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        list.add(selectedItem)
        _selectedItemsNoteFromActionMode.value = list
    }

    fun deleteOrUpdateSelectionItemsNote() {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        list.forEach {
//            if (it.isArchive) {
//                it.isArchive = false
//                it.isDeleted = true
//                updateNote(it)
//            } else {
//                deleteNote(it)
//            }
        }
        clearSelectedNotes()
    }

    fun clearSelectedItemsNote() {
        _selectedItemsNoteFromActionMode.value = mutableSetOf()
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

    fun saveData(title: String) {
//        val listImage: List<Image>? = editedImages.value
//        val listColors: List<Int>? = currentColorsFields.value
//        val listCategories: List<Category>? = editedSelectCategoryFromDialogMoveCategory.value
//        var currentNote = selectedNote.value
//        currentNote = currentNote!!.copy(
//            title = title,
//            content = content,
//            text = text,
//            time = CurrentTimeInFormat.getCurrentTime(),
//            colorFrameTitle = listColors?.get(0) ?: 0,
//            colorFrameContent = listColors?.get(1) ?: 0
//        )
//        addOrUpdateNoteWithImages(
//            currentNote,
//            listImage,
//            listCategories
//        )

        val listColors: List<Int>? = currentColorsFields.value
        val listCategories: List<Category>? = editedSelectCategoryFromDialogMoveCategory.value
        var currentNote = selectedNote.value
        val currentListNoteItems = currentItemsFromNote.value

        val firstText = StringBuilder("")
        currentListNoteItems?.filterIsInstance<TextItem>()?.forEach { textItem ->
            firstText.append(textItem.text).append("\n")
        }

        var isImage = 0
        currentListNoteItems?.filterIsInstance<ImageItem>()?.forEach {
            isImage++
        }

        currentNote = currentNote!!.copy(
            title = title,
            content = firstText.toString(),
            time = CurrentTimeInFormat.getCurrentTime(),
            colorFrameTitle = listColors?.get(0) ?: 0,
            colorFrameContent = listColors?.get(1) ?: 0
        )
        addOrUpdateNoteWithImages(
            currentNote, currentListNoteItems,
            listCategories
        )

    }

    //    fun addOrUpdateNoteWithImages(
//        note: Note,
//        images: List<Image>?,
//        categories: List<Category>?
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
//            if (note.id == 0L) {
//                //new note
//                val id = addNote(note)
//                note.id = id
//            } else {
//                updateNote(note)
//            }
//
//            if (images != null) {
//                saveImages(note, images)
//            }
//            if (categories != null) {
//                saveCategories(note, categories)
//            }
//        }
//    }
    fun addOrUpdateNoteWithImages(
        note: Note, listItems: List<MultiItem>?,
        categories: List<Category>?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                //new note
                val id = addNote(note)
                note.id = id
                listItems?.forEach { item ->
                    when (item) {
                        is TextItem -> {
                            if (item.text.isNotEmpty()) {
                                item.foreignId = id
                                repository.insertTextItemFromNote(item)
                            }
                        }

                        is ImageItem -> {
                            item.foreignId = id
                            repository.insertImageItemWithImage(item, item.listImageItems)
                        }
                    }
                }

            } else {
                //old note
                updateNote(note)

                listItems?.forEach { item ->
                    when (item) {
                        is TextItem -> {
                            updateTextItemFromNote(note.id, item)
                        }

                        is ImageItem -> {
                            updateImageItemFromNote(note.id, item)
                        }
                    }
                }
            }


            if (categories != null) {
                saveCategories(note, categories)
            }
            clearListItems()
        }
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

    fun clearSelectedNote() {
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
            saveCategories(
                selectedNote.value!!,
                _editedSelectCategoryFromDialogMoveCategory.value!!
            )
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
        if (isDelete != null && selectedNote != null) {
            selectedNote = selectedNote.copy(isDeleted = isDelete)
        }
        if (isArchive != null && selectedNote != null) {
            selectedNote = selectedNote.copy(isArchive = isArchive)
        }
        if (selectedNote != null) {
            updateNote(selectedNote)
        }
        _selectedNote = MutableLiveData()
    }

    fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
    }


    private val _currentItemsFromNote: MutableLiveData<List<MultiItem>> =
        MutableLiveData<List<MultiItem>>()
    val currentItemsFromNote: LiveData<List<MultiItem>> get() = _currentItemsFromNote

    fun getTextItemsFromNote(idNote: Long): List<TextItem> {
        return repository.getItemsText(idNote)
    }

    fun getImageItemsFromNote(idNote: Long): List<ImageItem> {
        return repository.getImageItems(idNote)
    }

    fun getImageFromImageItem(idImageItem: Long): List<Image> {
        return repository.getImageFromImageItem(idImageItem)
    }

    fun selectImageInImageItem(idNote: Long): List<ImageItem> {

        val list = getImageItemsFromNote(idNote)
        list.forEach { imageItem ->
            val listImage = getImageFromImageItem(imageItem.imageItemId)
            imageItem.listImageItems = listImage as ArrayList<Image>
        }
        return list
    }

    fun unionLiveData(idNote: Long) {

        val first = getTextItemsFromNote(idNote)
        val second = selectImageInImageItem(idNote)
        val unionList = arrayListOf<MultiItem>()
        unionList.addAll(first)
        unionList.addAll(second)
        val sortedList = unionList.sortedWith(compareBy { it.position })
        _currentItemsFromNote.postValue(sortedList)
    }

    fun createNewItemTextFromNote() {
        val currentItems = currentItemsFromNote.value
        val position = currentItems?.size ?: 1
        setItemFromCurrentListItemsForNote(TextItem(position = position))
    }

    fun createNewItemImageFromNote() {

    }

    fun setItemFromCurrentListItemsForNote(item: MultiItem) {
        val currentItemViewModel = arrayListOf<MultiItem>()
        if (_currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(_currentItemsFromNote.value!!)
        }
        currentItemViewModel.add(item)
        if (item is ImageItem) {
            currentItemViewModel.add(TextItem(position = item.position + 1))
        }

        _currentItemsFromNote.value = currentItemViewModel
    }

    fun clearListItems() {
        _currentItemsFromNote.postValue(listOf())
    }


    fun updateItemFromCurrentListItemsForNote(item: MultiItem, newText: String, positionItem: Int) {
        val currentItemViewModel = arrayListOf<MultiItem>()
        if (currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(currentItemsFromNote.value!!)
        }

        var newItem: TextItem? = null
        var indexItem = 0
        currentItemViewModel.forEachIndexed { index, currItem ->
            if (currItem is TextItem && item is TextItem && currItem.itemTextId == item.itemTextId) {
                newItem = currItem
                indexItem = index
            }
        }

        currentItemViewModel[indexItem] = newItem?.copy(text = newText, position = positionItem)!!

        _currentItemsFromNote.value = currentItemViewModel
    }

    fun updateImageItemFromNote(noteId: Long, item: ImageItem) {
        if (item.imageItemId == 0L) {
            item.foreignId = noteId
            repository.insertImageItemWithImage(item, item.listImageItems)
        } else {
            repository.updateImageItem(item, item.listImageItems)
        }
    }

    fun updateTextItemFromNote(noteId: Long, item: TextItem) {
        if (item.itemTextId == 0L) {
            if (item.text.isNotEmpty()) {
                item.foreignId = noteId
                repository.insertTextItemFromNote(item)
            }
        } else {
            if (item.text.isEmpty()) {
                repository.deleteTextItem(item)
            } else {
                repository.updateTextItem(item)
            }

        }
    }

    fun undoTextFromItem() {
        val currentCommand = undo.pop()
        redo.push(currentCommand)
        val command = currentCommand as CommandReplaceText
        currentItemsFromNote.value?.forEach { item ->
            if (item is TextItem && item.itemTextId == command.idItems) {
                val text = item.text
                val newText = currentCommand.undo(text)
                updateItemFromCurrentListItemsForNote(item, newText, command.positionItem)
                //item.text = newText
            }
        }
    }

    fun redoTextFromItem() {
        val currentCommand = redo.pop()
        undo.push(currentCommand)
        val command = currentCommand as CommandReplaceText
        currentItemsFromNote.value?.forEach { item ->
            if (item is TextItem && item.itemTextId == command.idItems) {
                val text = item.text
                val newText = currentCommand.redo(text)
                updateItemFromCurrentListItemsForNote(item, newText, command.positionItem)
            }
        }
    }
}



