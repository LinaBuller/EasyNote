package com.easynote.domain.viewmodels

import androidx.appcompat.view.ActionMode
import android.net.Uri
import androidx.lifecycle.*

import com.easynote.domain.models.Category
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.NoteWithCategoriesModel
import com.easynote.domain.models.TextItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.usecase.DeleteNoteUseCase
import com.easynote.domain.usecase.GetListNotesUseCase
import com.easynote.domain.usecase.SetNoteUseCase
import com.easynote.domain.usecase.SetNoteWithCategoryUseCase
import com.easynote.domain.usecase.UpdateNoteUseCase
import com.easynote.domain.usecase.categories.DeleteCategoryUseCase
import com.easynote.domain.usecase.categories.GetCategoriesUseCase
import com.easynote.domain.usecase.categories.GetNoteWithCategoriesUseCase
import com.easynote.domain.usecase.categories.SetCategoriesUseCase
import com.easynote.domain.usecase.categories.UpdateCategoryUseCase
import com.easynote.domain.usecase.favoriteColors.DeleteFavoriteColorsUseCase
import com.easynote.domain.usecase.favoriteColors.GetFavoriteColorsUseCase
import com.easynote.domain.usecase.favoriteColors.SetFavoriteColorsUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.DeleteTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetImageItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetTextItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.SetImageItemsWithImagesFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.SetTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.UpdateImageItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.UpdateTextItemFromNoteUseCase
import com.easynote.domain.utils.BuilderQuery
import com.easynote.domain.utils.CurrentTimeInFormat
import com.easynote.domain.utils.edittextnote.CommandReplaceText
import com.easynote.domain.utils.edittextnote.OnUndoRedo
import com.easynote.domain.utils.theme.CurrentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList


class NotesViewModel(
    val getListNotesUseCase: GetListNotesUseCase,
    val setNoteUseCase: SetNoteUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val getCategoriesUseCase: GetCategoriesUseCase,
    val setCategoriesUseCase: SetCategoriesUseCase,
    val updateCategoryUseCase: UpdateCategoryUseCase,
    val deleteCategoryUseCase: DeleteCategoryUseCase,
    val getNoteWithCategoriesUseCase: GetNoteWithCategoriesUseCase,
    val setNoteWithCategoryUseCase: SetNoteWithCategoryUseCase,
    val getFavoriteColorsUseCase: GetFavoriteColorsUseCase,
    val setFavoriteColorsUseCase: SetFavoriteColorsUseCase,
    val deleteFavoriteColorsUseCase: DeleteFavoriteColorsUseCase,
    val getTextItemsFromNoteUseCase: GetTextItemsFromNoteUseCase,
    val setTextItemFromNoteUseCase: SetTextItemFromNoteUseCase,
    val updateTextItemFromNoteUseCase: UpdateTextItemFromNoteUseCase,
    val deleteTextItemFromNoteUseCase: DeleteTextItemFromNoteUseCase,
    val getImageItemsFromNoteUseCase: GetImageItemsFromNoteUseCase,
    val setImageItemsWithImagesFromNoteUseCase: SetImageItemsWithImagesFromNoteUseCase,
    val updateImageItemFromNoteUseCase: UpdateImageItemFromNoteUseCase,
    val deleteImageFromImageItemUseCase: DeleteImageFromImageItemUseCase
) : ViewModel() {
    private val mediatorNotes = MediatorLiveData<List<Note>>()
    val readAllNotes: LiveData<List<Note>> get() = mediatorNotes

    private var lastNotes: LiveData<List<Note>>? = null

    private val _filterCategoryId: MutableLiveData<Long> by lazy {MutableLiveData(
        DEFAULT_FILTER_CATEGORY_ID
    )}

    val filterCategoryId: LiveData<Long> get() = _filterCategoryId

    private val _sortColumn: MutableLiveData<String> by lazy { MutableLiveData(DEFAULT_SORT_COLUMN) }
    private val sortColumn: LiveData<String> get() = _sortColumn

    private val _sortOrder: MutableLiveData<Int> by lazy { MutableLiveData(DEFAULT_SORT_ORDER) }
    private val sortOrder: LiveData<Int> get() = _sortOrder

    private val _searchText: MutableLiveData<String> by lazy { MutableLiveData("") }
    private val searchText: LiveData<String> get() = _searchText


    fun loadNotes() {
        val query = BuilderQuery.buildQuery(
            sortColumn.value!!,
            sortOrder.value!!,
            filterCategoryId.value!!,
            searchText.value!!
        )

            val listNotes = getListNotesUseCase.execute(query)
            lastNotes?.let { mediatorNotes.removeSource(it) }
            mediatorNotes.addSource(listNotes) {
                mediatorNotes.value = it
            }
            lastNotes = listNotes

    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNoteUseCase.execute(note)
        }
    }

    fun setNote(note: Note): Long {
        return setNoteUseCase.execute(note)
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNoteUseCase.execute(note)
        }
    }

    fun resetSort() {
        _sortColumn.value = DEFAULT_SORT_COLUMN
        _sortOrder.value = DEFAULT_SORT_ORDER
        loadNotes()
    }

    fun resetFilterCategoryId() {
        _filterCategoryId.value =
            DEFAULT_FILTER_CATEGORY_ID
        loadNotes()
    }

    fun setFilterCategoryId(id: Long) {
        _filterCategoryId.value = id
        loadNotes()
    }

    fun setSort(
        sortColumn: String,
        sortOrder: Int = DEFAULT_SORT_ORDER
    ) {
        _sortColumn.value = sortColumn
        _sortOrder.value = sortOrder
        loadNotes()
    }

    fun setSearchText(text: String) {
        _searchText.value = text
        loadNotes()
    }

    companion object {
        const val DEFAULT_SORT_COLUMN = "n.is_pin"
        const val DEFAULT_SORT_ORDER = 1
        const val DEFAULT_FILTER_CATEGORY_ID = -1L
    }

    fun setCategoriesWitnNoteId(note: Note, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            setNoteWithCategoryUseCase.execute(note, categories)
        }
    }

    private val _categories = getCategoriesUseCase.execute()
    val categories: LiveData<List<Category>> get() = _categories

    private var _selectedCategory = MutableLiveData<Category?>()
    val selectedCategory: LiveData<Category?> get() = _selectedCategory



    fun setCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            setCategoriesUseCase.execute(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            updateCategoryUseCase.execute(category)
        }
    }

    fun deleteCategory(category: Category) {
        if (filterCategoryId.value == category.idCategory) {
            resetFilterCategoryId()
        }
        viewModelScope.launch(Dispatchers.IO) {
            deleteCategoryUseCase.execute(category)
        }
    }

    suspend fun getNoteWithCategories(note: Note): NoteWithCategoriesModel {
        return getNoteWithCategoriesUseCase.execute(note)
    }

    private var _selectedNote = MutableLiveData<Note?>()
    val selectedNote: LiveData<Note?> get() = _selectedNote

    fun setSelectedCategoryFromItemList() {
        val selectedNote = selectedNote.value
        viewModelScope.launch {
            if (selectedNote != null) {
                val selectedCategory = getNoteWithCategories(selectedNote)
                if (selectedCategory.listOfCategories != null) {
                    selectEditedCategory(
                        selectedCategory.listOfCategories!!
                    )
                } else {
                    cleanSelectedCategories()
                }
            }
        }
    }

    fun changeCheckboxCategory(category: Category, isChecked: Boolean) {
        val selectedCategory = arrayListOf<Category>()
        val list = _editedSelectCategoryFromDialogMoveCategory.value
        if (list != null) {
            selectedCategory.addAll(list)
        }
        if (isChecked) {
            selectedCategory.add(category)
        } else {
            selectedCategory.remove(category)
        }
        _editedSelectCategoryFromDialogMoveCategory.value = selectedCategory
    }

    fun selectEditedCategory(listSelectedCategory: List<Category>) {
        _editedSelectCategoryFromDialogMoveCategory.postValue(listSelectedCategory)
    }

    fun cleanSelectedCategories() {
        _editedSelectCategoryFromDialogMoveCategory.value = listOf()
    }

    fun updateCategoryFromItemList() {
        if (selectedNote.value != null && _editedSelectCategoryFromDialogMoveCategory.value != null) {
            setCategoriesWitnNoteId(
                selectedNote.value!!,
                _editedSelectCategoryFromDialogMoveCategory.value!!
            )
        }
    }

    fun setSelectedNote(note: Note) {
        _selectedNote.value = note
    }

    fun updateStatusNote(
        isDelete: Boolean?,
        isArchive: Boolean?
    ) {
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
        _selectedNote = MutableLiveData()
    }

    fun clearSelectedNote() {
        _selectedNote.value = null
        _editedSelectCategoryFromDialogMoveCategory.value = null
    }

    fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
    }


    private val _favoriteColors = MutableLiveData<List<FavoriteColor>>()
    val favoriteColors: LiveData<List<FavoriteColor>> get() = _favoriteColors
    val currentColorsFields = MutableLiveData<List<Int>>()
    val editedColorsFields = MutableLiveData<List<Int>>()

    fun getFavoriteColors(){
        _favoriteColors.value = getFavoriteColorsUseCase.execute()
    }

    fun setFavoritesColors(listFavColors: List<FavoriteColor>) {
        viewModelScope.launch(Dispatchers.IO) {
            setFavoriteColorsUseCase.execute(listFavColors)
        }
    }

    fun deleteFavoriteColor(favoriteColor: FavoriteColor) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteFavoriteColorsUseCase.execute(favoriteColor)
        }
    }

    fun selectColorFieldsNote(colors: List<Int>) {
        currentColorsFields.value = colors
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

    fun cleanSelectedColors() {
        currentColorsFields.value = listOf(0, 0)
    }




    //сделать получение и запись в SharedPref
    private val _currentKindOfList = MutableLiveData<Boolean>()
    val currentKindOfList: LiveData<Boolean> get() = _currentKindOfList

    fun setCurrentKindOfList(kindOfList: Boolean) {
        _currentKindOfList.value = kindOfList
    }


    //сделать получение и запись в SharedPref
    private val _currentTheme: MutableLiveData<CurrentTheme> = MutableLiveData(CurrentTheme(0))
    val currentTheme: LiveData<CurrentTheme> = _currentTheme

    fun changeTheme(id: Int) {
        val currentThemeNow: CurrentTheme? = _currentTheme.value
        if (currentThemeNow != null) {
            _currentTheme.value = currentThemeNow.copy(themeId = id)
        }
    }



    private val _editedSelectCategoryFromDialogMoveCategory = MutableLiveData<List<Category>?>()
    val editedSelectCategoryFromDialogMoveCategory: LiveData<List<Category>?> get() = _editedSelectCategoryFromDialogMoveCategory




    var actionMode: ActionMode? = null
    private val _selectedNotesFromActionMode = MutableLiveData<MutableSet<Note>>()
    val selectedNotesFromActionMode: LiveData<MutableSet<Note>> get() = _selectedNotesFromActionMode

    fun changeSelectedNotesFromActionMode(selectedNote:Note) {
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




    private val _selectedItemsNoteFromActionMode = MutableLiveData<MutableSet<MultiItem>>()
    val selectedItemsNoteFromActionMode: LiveData<MutableSet<MultiItem>> get() = _selectedItemsNoteFromActionMode

    fun changeSelectedItemsNoteFromActionMode(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        if (list.contains(selectedItem)) {
            removeSelectedItemsNoteFromActionMode(selectedItem)
        } else {
            addSelectedItemsNoteFromActionMode(selectedItem)
        }
    }

    private fun removeSelectedItemsNoteFromActionMode(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        list.remove(selectedItem)
        _selectedItemsNoteFromActionMode.value = list
    }

    private fun addSelectedItemsNoteFromActionMode(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        list.add(selectedItem)
        _selectedItemsNoteFromActionMode.value = list
    }

    fun deleteOrUpdateSelectionItemsNoteFromActionMode() {
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
        clearSelectedNotesFromActionMode()
    }

    fun clearSelectedItemsNoteFromActionMode() {
        _selectedItemsNoteFromActionMode.value = mutableSetOf()
    }



    val undo: ArrayDeque<OnUndoRedo> = ArrayDeque()
    val redo: ArrayDeque<OnUndoRedo> = ArrayDeque()

    fun undoTextFromItem() {
        val currentCommand = undo.pop()
        redo.push(currentCommand)
        val command =
            currentCommand as CommandReplaceText
        currentItemsFromNote.value?.forEach { item ->
            if (item is TextItem && item.itemTextId == command.idItems) {
                val text = item.text
                val newText =
                    currentCommand.undo(text)
                updateItemFromCurrentListItemsForNote(
                    item,
                    newText,
                    command.positionItem
                )
                //item.text = newText
            }
        }
    }

    fun redoTextFromItem() {
        val currentCommand = redo.pop()
        undo.push(currentCommand)
        val command =
            currentCommand as CommandReplaceText
        currentItemsFromNote.value?.forEach { item ->
            if (item is TextItem && item.itemTextId == command.idItems) {
                val text = item.text
                val newText =
                    currentCommand.redo(text)
                updateItemFromCurrentListItemsForNote(
                    item,
                    newText,
                    command.positionItem
                )
            }
        }
    }

    fun clearUndoRedo() {
        undo.clear()
        redo.clear()
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

        val listColors: List<Int>? =
            currentColorsFields.value
        val listCategories: List<com.easynote.domain.models.Category>? =
            editedSelectCategoryFromDialogMoveCategory.value
        var currentNote = selectedNote.value
        val currentListNoteItems =
            currentItemsFromNote.value

        val firstText = StringBuilder("")
        currentListNoteItems?.filterIsInstance<TextItem>()
            ?.forEach { textItem ->
                firstText.append(textItem.text)
                    .append("\n")
            }

        var isImage = 0
        currentListNoteItems?.filterIsInstance<ImageItem>()?.forEach {
                isImage++
        }

        currentNote = currentNote!!.copy(
            title = title,
            content = firstText.toString(),
            time = CurrentTimeInFormat.getCurrentTime(),
            colorFrameTitle = listColors?.get(0)
                ?: 0,
            colorFrameContent = listColors?.get(1)
                ?: 0
        )
        addOrUpdateNoteWithImages(
            currentNote, currentListNoteItems,
            listCategories
        )

    }




    /**    fun addOrUpdateNoteWithImages(
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
    }**/


    //сделать норм удаление и добавление картинок
    val editedImages = MutableLiveData<List<Image>?>()
    fun selectEditedImagesPost(images: List<Image>?) {
        editedImages.postValue(images)
    }
    fun selectEditedImages(images: List<Image>?) {
        editedImages.value = images
    }
    fun addSelectedImagesToViewModel(uris: List<Uri>) {
        val newImageList =
            arrayListOf<Image>()
        if (editedImages.value != null) {
            newImageList.addAll(editedImages.value!!)
        }
        uris.forEach { newUri ->
            newImageList.add(
                Image(
                    0,
                    0,
                    newUri.toString()
                )
            )
        }
        selectEditedImages(newImageList)
    }
    fun clearEditImages() {
        editedImages.value = listOf()
    }
    fun deleteImage(image: Image) {
        val list: ArrayList<Image> =
            ArrayList()
        editedImages.value?.let { list.addAll(it) }
        list.removeIf { imageSelect ->
            imageSelect.uri.contains(image.uri)
        }
        selectEditedImages(list)
        viewModelScope.launch(Dispatchers.IO) {
            deleteImageFromImageItemUseCase.execute(image)
        }
    }



    private val _currentItemsFromNote: MutableLiveData<List<MultiItem>> = MutableLiveData<List<MultiItem>>()
    val currentItemsFromNote: LiveData<List<MultiItem>> get() = _currentItemsFromNote


    fun getMultiItemsNoteFromNote(note: Note) {
        val first = getTextItemsFromNote(note)

        val second = selectImageInImageItem(note)
        val unionList = arrayListOf<MultiItem>()
        unionList.addAll(first)
        unionList.addAll(second)
        val sortedList =
            unionList.sortedWith(compareBy { it.position })
        _currentItemsFromNote.postValue(sortedList)
    }

    fun getTextItemsFromNote(note: Note): List<MultiItem> {
        return getTextItemsFromNoteUseCase.execute(note)
    }

    fun createNewItemTextFromNote() {
        val currentItems =
            currentItemsFromNote.value
        val position = currentItems?.size ?: 1
        setItemFromCurrentListItemsForNote(
            TextItem(
                position = position
            )
        )
    }

    fun setItemFromCurrentListItemsForNote(item: MultiItem) {
        val currentItemViewModel =
            arrayListOf<MultiItem>()
        if (_currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(
                _currentItemsFromNote.value!!
            )
        }
        currentItemViewModel.add(item)
        if (item is ImageItem) {
            currentItemViewModel.add(
               TextItem(
                    position = item.position + 1
                )
            )
        }

        _currentItemsFromNote.value =
            currentItemViewModel
    }

    fun updateItemFromCurrentListItemsForNote(item: MultiItem, newText: String, positionItem: Int) {
        val currentItemViewModel =
            arrayListOf<com.easynote.domain.models.MultiItem>()
        if (currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(
                currentItemsFromNote.value!!
            )
        }

        var newItem: TextItem? = null
        var indexItem = 0
        currentItemViewModel.forEachIndexed { index, currItem ->
            if (currItem is TextItem && item is TextItem && currItem.itemTextId == item.itemTextId) {
                newItem = currItem
                indexItem = index
            }
        }

        currentItemViewModel[indexItem] =
            newItem?.copy(
                text = newText,
                position = positionItem
            )!!

        _currentItemsFromNote.value =
            currentItemViewModel
    }

    fun updateTextItemFromNote(noteId: Long, item: TextItem) {
        if (item.itemTextId == 0L) {
            if (item.text.isNotEmpty()) {
                item.foreignId = noteId
                setTextItemFromNoteUseCase.execute(item)
            }
        } else {
            if (item.text.isEmpty()) {
                deleteTextItemFromNoteUseCase.execute(item)
            } else {
                updateTextItemFromNoteUseCase.execute(item)
            }

        }
    }

    fun getImageFromImageItem(idImageItem: Long): List<Image> {
        return getImageFromImageItem(idImageItem)

    }

    fun selectImageInImageItem(note: Note): List<ImageItem> {
        val list = getImageItemsFromNote(note)
        list.forEach { imageItem ->
            val listImage = getImageFromImageItem(imageItem.imageItemId)
            imageItem.listImageItems = listImage as ArrayList<Image>
        }
        return list
    }

    fun getImageItemsFromNote(note: Note): List<ImageItem> {
        return getImageItemsFromNoteUseCase.execute(note)
    }

    fun updateImageItemFromNote(noteId: Long, item:ImageItem) {
        if (item.imageItemId == 0L) {
            item.foreignId = noteId
            setImageItemsWithImagesFromNoteUseCase.execute(item,item.listImageItems)
        } else {
            updateImageItemFromNoteUseCase.execute(item,item.listImageItems)
        }
    }

    fun addOrUpdateNoteWithImages(note: Note, listItems: List<MultiItem>?, categories: List<Category>?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                //new note
                val id = setNote(note)
                note.id = id
                listItems?.forEach { item ->
                    when (item) {
                        is TextItem -> {
                            if (item.text.isNotEmpty()) {
                                item.foreignId = id
                                setTextItemFromNoteUseCase.execute(item)
                            }
                        }

                        is com.easynote.domain.models.ImageItem -> {
                            item.foreignId = id
                            setImageItemsWithImagesFromNoteUseCase.execute( item, item.listImageItems)
                        }
                    }
                }

            } else {
                //old note
                updateNote(note)

                listItems?.forEach { item ->
                    when (item) {
                        is TextItem -> {
                            updateTextItemFromNote(
                                note.id,
                                item
                            )
                        }

                        is ImageItem -> {
                            updateImageItemFromNote(
                                note.id,
                                item
                            )
                        }
                    }
                }
            }


            if (categories != null) {
                setCategoriesWitnNoteId(note, categories)
            }
            clearListItems()
        }
    }

    fun clearListItems() {
        _currentItemsFromNote.postValue(listOf())
    }
}



