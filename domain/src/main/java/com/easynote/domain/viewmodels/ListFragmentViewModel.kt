package com.easynote.domain.viewmodels

import androidx.appcompat.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.Category
import com.easynote.domain.models.Note
import com.easynote.domain.usecase.DeleteNoteUseCase
import com.easynote.domain.usecase.GetListNotesUseCase
import com.easynote.domain.usecase.SetNoteWithCategoryUseCase
import com.easynote.domain.usecase.UpdateNoteUseCase
import com.easynote.domain.usecase.categories.GetCategoriesUseCase
import com.easynote.domain.usecase.categories.GetNoteWithCategoriesUseCase
import com.easynote.domain.usecase.categories.SetCategoriesUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetTypeListUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetTypeListUseCase
import com.easynote.domain.utils.BuilderQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ListFragmentViewModel(
    val getListNotesUseCase: GetListNotesUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val getCategoriesUseCase: GetCategoriesUseCase,
    val setCategoryUseCase: SetCategoriesUseCase,
    val setNoteWithCategoryUseCase: SetNoteWithCategoryUseCase,
    val getNoteWithCategoriesUseCase: GetNoteWithCategoriesUseCase,
    val getTypeListUseCase: GetTypeListUseCase,
    val setTypeListUseCase: SetTypeListUseCase
) :
    BaseViewModel() {

    fun setCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            setCategoryUseCase.execute(category)
        }
    }


    private var _selectedNote = MutableLiveData<Note?>()
    val selectedNote: LiveData<Note?> get() = _selectedNote

    private val _existCategories = getCategoriesUseCase.execute()
    val existCategories: LiveData<List<Category>> get() = _existCategories

    fun setSelectedNote(note: Note) {
        _selectedNote.value = note
        getSelectedNoteWithCurrentCategories(note.id)
    }

    private fun getSelectedNoteWithCurrentCategories(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val noteWithCategories = getNoteWithCategoriesUseCase.execute(noteId)
            if (noteWithCategories.listOfCategories != null) {
                updateCurrentCategories(noteWithCategories.listOfCategories!!)
            }
        }
    }


    private var _currentCategories = MutableLiveData<List<Category>>()
    val currentCategories: LiveData<List<Category>> get() = _currentCategories

    private fun updateCurrentCategories(listSelectedCategory: List<Category>) {
        _currentCategories.postValue(listOf())
        _currentCategories.postValue(listSelectedCategory)
    }

    fun changeCheckboxCategory(category: Category, isChecked: Boolean) {
        val selectedCategory = arrayListOf<Category>()
        val list = _currentCategories.value
        if (list != null) {
            selectedCategory.addAll(list)
        }
        if (isChecked) {
            selectedCategory.add(category)
        } else {
            selectedCategory.remove(category)
        }
        _currentCategories.value = selectedCategory
    }


    fun updateCategoryFromNote() {
        if (selectedNote.value != null && currentCategories.value != null) {
            setCategoriesFromNote(selectedNote.value!!, currentCategories.value!!)
        }
    }

    private fun setCategoriesFromNote(note: Note, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            setNoteWithCategoryUseCase.execute(note, categories)
            clearSelectedNote()
            cleanCurrentCategories()
        }
    }

    private fun cleanCurrentCategories() {
        _currentCategories.postValue(listOf())
    }

    private fun clearSelectedNote() {
        _selectedNote.postValue(null)
    }


    private val _currentKindOfList = MutableLiveData<Boolean>()
    val currentKindOfList: LiveData<Boolean> get() = _currentKindOfList

    fun setCurrentKindOfList(kindOfList: Boolean) {
        _currentKindOfList.value = kindOfList
    }

    fun getTypeList() {
        _currentKindOfList.value = getTypeListUseCase.execute()
    }

    fun setTypeList(kindOfList: Boolean) {
        setTypeListUseCase.execute(kindOfList)
    }

    companion object {
        const val DEFAULT_SORT_COLUMN = "n.is_pin"
        const val DEFAULT_SORT_ORDER = 1
        const val DEFAULT_FILTER_CATEGORY_ID = -1L
    }

    private val mediatorNotes = MediatorLiveData<List<Note>>()
    val readAllNotes: LiveData<List<Note>> get() = mediatorNotes
    private var lastNotes: LiveData<List<Note>>? = null

    private val _filterCategoryId: MutableLiveData<Long> by lazy {
        MutableLiveData(
            DEFAULT_FILTER_CATEGORY_ID
        )
    }

    val filterCategoryId: LiveData<Long> get() = _filterCategoryId

    private val _sortColumn: MutableLiveData<String> by lazy { MutableLiveData(DEFAULT_SORT_COLUMN) }
    private val sortColumn: LiveData<String> get() = _sortColumn

    private val _sortOrder: MutableLiveData<Int> by lazy { MutableLiveData(DEFAULT_SORT_ORDER) }
    private val sortOrder: LiveData<Int> get() = _sortOrder

    private val _searchText: MutableLiveData<String> by lazy { MutableLiveData("") }
    private val searchText: LiveData<String> get() = _searchText

    suspend fun loadNotes() {
        val query = BuilderQuery.buildQuery(
            sortColumn.value!!,
            sortOrder.value!!,
            filterCategoryId.value!!,
            searchText.value!!
        )
        val list = viewModelScope.async {
            val listNotes = getListNotesUseCase.execute(query)
            lastNotes?.let { mediatorNotes.removeSource(it) }
            mediatorNotes.addSource(listNotes) {
                mediatorNotes.value = it
            }
            mediatorNotes
        }


        lastNotes = list.await()

    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNoteUseCase.execute(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNoteUseCase.execute(note)
        }
    }

    fun resetSort() {
        viewModelScope.launch(Dispatchers.IO) {
            _sortColumn.value = DEFAULT_SORT_COLUMN
            _sortOrder.value = DEFAULT_SORT_ORDER
            loadNotes()
        }
    }

    fun resetFilterCategoryId() {
        viewModelScope.launch(Dispatchers.IO) {
            _filterCategoryId.value = DEFAULT_FILTER_CATEGORY_ID
            loadNotes()
        }
    }

    fun setFilterCategoryId(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _filterCategoryId.value = id
            loadNotes()
        }
    }

    fun setSort(sortColumn: String, sortOrder: Int = DEFAULT_SORT_ORDER) {
        viewModelScope.launch(Dispatchers.IO) {
            _sortColumn.value = sortColumn

            _sortOrder.value = sortOrder
            loadNotes()
        }
    }

    fun setSearchText(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchText.value = text
            loadNotes()
        }
    }

    fun updateStatusNote(isDelete: Boolean? = null, isArchive: Boolean? = null) {
        //Add isPin, isFavorite
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

    fun changeNotePin(isPin: Boolean) {
        val selectedNote = selectedNote.value
        if (selectedNote != null) {
            val changedSelectedNote = selectedNote.copy(isPin = isPin)
            updateNote(changedSelectedNote)
        }
    }


    fun changeNoteFavorite(isFavorite: Boolean) {
        val selectedNote = selectedNote.value
        if (selectedNote != null) {
            val changedSelectedNote = selectedNote.copy(isFavorite = isFavorite)
            updateNote(changedSelectedNote)
        }
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

    fun deleteOrUpdateSelectionNotesFromActionMode() {
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
        clearSelectedNotesFromActionMode()
    }

    fun clearSelectedNotesFromActionMode() {
        _selectedNotesFromActionMode.value = mutableSetOf()
    }
}
