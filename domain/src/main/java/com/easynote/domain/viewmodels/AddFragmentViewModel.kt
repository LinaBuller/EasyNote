package com.easynote.domain.viewmodels

import androidx.appcompat.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.BackgroungColor
import com.easynote.domain.models.Category
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.TextItem
import com.easynote.domain.usecase.SetNoteUseCase
import com.easynote.domain.usecase.SetNoteWithCategoryUseCase
import com.easynote.domain.usecase.UpdateNoteUseCase
import com.easynote.domain.usecase.categories.GetCategoriesUseCase
import com.easynote.domain.usecase.categories.GetNoteWithCategoriesUseCase
import com.easynote.domain.usecase.categories.SetCategoriesUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.DeleteTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetImageItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetImagesFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.GetTextItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.SetImageItemsWithImagesFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.SetTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.UpdateImageItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.UpdateTextItemFromNoteUseCase
import com.easynote.domain.utils.CurrentTimeInFormat
import com.easynote.domain.utils.edittextnote.CommandReplaceText
import com.easynote.domain.utils.edittextnote.OnUndoRedo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayDeque

class AddFragmentViewModel(
    val setNoteUseCase: SetNoteUseCase,
    val updateNoteUseCase: UpdateNoteUseCase,
    val getCategoriesUseCase: GetCategoriesUseCase,
    val setCategoriesUseCase: SetCategoriesUseCase,
    val setNoteWithCategoryUseCase: SetNoteWithCategoryUseCase,
    val getNoteWithCategoriesUseCase: GetNoteWithCategoriesUseCase,
    val getTextItemsFromNoteUseCase: GetTextItemsFromNoteUseCase,
    val setTextItemFromNoteUseCase: SetTextItemFromNoteUseCase,
    val updateTextItemFromNoteUseCase: UpdateTextItemFromNoteUseCase,
    val deleteTextItemFromNoteUseCase: DeleteTextItemFromNoteUseCase,
    val getImageItemsFromNoteUseCase: GetImageItemsFromNoteUseCase,
    val setImageItemsWithImagesFromNoteUseCase: SetImageItemsWithImagesFromNoteUseCase,
    val updateImageItemFromNoteUseCase: UpdateImageItemFromNoteUseCase,
    val deleteImageItemFromNoteUseCase: DeleteImageItemFromNoteUseCase,
    val deleteImageUseCase: DeleteImageFromImageItemUseCase,
    val getImagesFromImageItemUseCase: GetImagesFromImageItemUseCase
) : ViewModel() {

    private var noteId = 0L

    fun setNoteId(id: Long) {
        noteId = id
        if (noteId == 0L) {
            _selectedNote.postValue(Note())
            _currentItemsFromNote.value = listOf(TextItem(position = 0))
        } else {
            getSelectedNote()
            getMultiItemsNoteFromNote()
        }
    }

    private var _selectedNote = MutableLiveData<Note?>()
    val selectedNote: LiveData<Note?> get() = _selectedNote

    private fun getSelectedNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val noteWithCategories = getNoteWithCategoriesUseCase.execute(noteId)
            setSelectedNote(noteWithCategories.note)
            if (noteWithCategories.listOfCategories != null) {
                updateCurrentCategories(noteWithCategories.listOfCategories!!)
            }
        }
    }

    fun setSelectedNote(note: Note) {
        _selectedNote.postValue(note)
    }

    fun setNote(note: Note): Long {
        return setNoteUseCase.execute(note)
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNoteUseCase.execute(note)
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
        _selectedNote = MutableLiveData()
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

    private val _existCategories = getCategoriesUseCase.execute()
    val existCategories: LiveData<List<Category>> get() = _existCategories

    fun setCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            setCategoriesUseCase.execute(category)
        }
    }

    private var _currentCategories = MutableLiveData<List<Category>>()
    val currentCategories: LiveData<List<Category>> get() = _currentCategories

    fun updateCurrentCategories(listSelectedCategory: List<Category>) {
        _currentCategories.postValue(listOf())
        _currentCategories.postValue(listSelectedCategory)
    }

    private var _currentItemsFromNote = MutableLiveData<List<MultiItem>>()
    val currentItemsFromNote: LiveData<List<MultiItem>> get() = _currentItemsFromNote

    fun saveData(title: String) {
        val listCurrentColors: List<BackgroungColor>? = listCurrentGradientColors.value
        val listCurrentCategories: List<Category>? = currentCategories.value
        var currentNote = selectedNote.value
        val currentListNoteItems: List<MultiItem> = currentItemsFromNote.value ?: return

        val firstText = StringBuilder("")
        currentListNoteItems?.filterIsInstance<TextItem>()
            ?.forEach { textItem ->
                firstText.append(textItem.text)
                    .append("\n")
            }
//
//        var isImage = 0
//        currentListNoteItems?.filterIsInstance<ImageItem>()?.forEach {
//            isImage++
//        }

        if (!listCurrentColors.isNullOrEmpty()) {
            val firstColor = listCurrentColors[0]
            val secondColor = listCurrentColors[1]
            currentNote = currentNote!!.copy(
                title = title,
                content = firstText.toString(),
                time = CurrentTimeInFormat.getCurrentTime(),
                gradientColorFirst = firstColor.colorWithHSL.color,
                gradientColorFirstH = firstColor.colorWithHSL.h,
                gradientColorFirstS = firstColor.colorWithHSL.s,
                gradientColorFirstL = firstColor.colorWithHSL.l,
                gradientColorSecond = secondColor.colorWithHSL.color,
                gradientColorSecondH = secondColor.colorWithHSL.h,
                gradientColorSecondS = secondColor.colorWithHSL.s,
                gradientColorSecondL = secondColor.colorWithHSL.l
            )

        } else {
            currentNote = currentNote!!.copy(
                title = title,
                content = firstText.toString(),
                time = CurrentTimeInFormat.getCurrentTime(),
            )
        }

        addOrUpdateNoteWithImages(
            currentNote, currentListNoteItems,
            listCurrentCategories
        )

    }

    private fun addOrUpdateNoteWithImages(
        note: Note,
        noteItems: List<MultiItem>,
        categories: List<Category>?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                //new note

                val id = setNote(note)
                note.id = id

                noteItems.forEach { item ->
                    when (item) {
                        is TextItem -> {
                            setTextItemFromNote(note.id, item)
                        }

                        is ImageItem -> {
                            setImageItemFromNote(note.id, item)
                        }
                    }
                }

            } else {
                //old note
                updateNote(note)

                noteItems.forEach { item ->
                    when (item) {
                        is TextItem -> {
                            if (item.foreignId == 0L) {
                                setTextItemFromNote(note.id, item)
                            } else {
                                updateTextItemFromNote(item)
                            }
                        }

                        is ImageItem -> {
                            if (item.foreignId == 0L) {
                                setImageItemFromNote(note.id, item)
                            } else {

                                updateImageItemFromNote(item)
                            }

                        }
                    }
                }
            }
        }
        if (categories != null) {
            setCategoriesWithNoteId(note, categories)
        }
    }

    fun setCategoriesWithNoteId(note: Note, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            setNoteWithCategoryUseCase.execute(note, categories)
        }
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

    private fun updateItemFromCurrentListItemsForNote(
        item: MultiItem,
        newText: String,
        positionItem: Int
    ) {
        val currentItemViewModel =
            arrayListOf<MultiItem>()
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

        _currentItemsFromNote.value = currentItemViewModel
    }


    private val _listCurrentGradientColors = MutableLiveData<List<BackgroungColor>>()
    val listCurrentGradientColors: LiveData<List<BackgroungColor>> get() = _listCurrentGradientColors


    fun setSelectedColors(listGradientColors: List<BackgroungColor>) {
        _listCurrentGradientColors.value = listGradientColors
    }

    private fun getMultiItemsNoteFromNote() {
        val unionList = arrayListOf<MultiItem>()
        viewModelScope.launch(Dispatchers.IO) {
            val first = getTextItemsFromNote(noteId)
            val second = selectImageInImageItem(noteId)

            unionList.addAll(first)
            unionList.addAll(second)

            unionList.add(TextItem(position = unionList.size))
            val sortedItemsList = unionList.sortedBy { it.position }

            _currentItemsFromNote.postValue(sortedItemsList)
        }
    }

    private fun getTextItemsFromNote(id: Long): List<MultiItem> {
        return getTextItemsFromNoteUseCase.execute(id)
    }

    fun setImagesToNote(uris: List<String>) {
        val imageItem = getImageItemForNewImage()

        if (imageItem == null) {
            val imageItemToCurrentList = setImagesToImageItem(uris, ImageItem())
            setItemFromCurrentListItemsForNote(imageItemToCurrentList)
        } else {
            val updatedItem = setImagesToImageItem(uris, imageItem)
            updateItemFromCurrentListForNote(updatedItem)
        }
    }

    private fun getImageItemForNewImage(): ImageItem? {
        val currentItems = currentItemsFromNote.value ?: return null
        var lastItem = currentItems.last()
        if (lastItem is TextItem && lastItem.isEmpty()) {
            val secondLastPos = currentItems.size - 2
            if (secondLastPos < 0) return null
            lastItem = currentItems[secondLastPos]
        }

        return if (lastItem is ImageItem) {
            lastItem
        } else {
            null
        }
    }

    private fun setImagesToImageItem(uris: List<String>, itemImage: ImageItem): ImageItem {
        val newImageList = arrayListOf<Image>()
        uris.forEach {
            val image = Image(uri = it, isNew = true)
            newImageList.add(image)
        }
        val oldListImages = itemImage.listImageItems
        val union = arrayListOf<Image>()
        union.addAll(oldListImages)
        union.addAll(newImageList)
        return itemImage.copy(listImageItems = union)
    }


    private fun updateItemFromCurrentListForNote(item: MultiItem) {
        val currentItemViewModel = arrayListOf<MultiItem>()
        if (_currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(_currentItemsFromNote.value!!)
        }
        currentItemViewModel.forEachIndexed { index, multiItem ->
            if (multiItem.position == item.position) {
                currentItemViewModel[index] = item
            }

        }
        _currentItemsFromNote.postValue(currentItemViewModel)
    }

    fun setItemFromCurrentListItemsForNote(item: MultiItem) {
        val currentItemViewModel = arrayListOf<MultiItem>()
        if (_currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(_currentItemsFromNote.value!!)
        }

        val last = currentItemViewModel.last()
        if (last is TextItem && last.isEmpty()) {
            currentItemViewModel.remove(last)
        }
        item.position = currentItemViewModel.size
        currentItemViewModel.add(item)

        if (item is ImageItem) {
            currentItemViewModel.add(TextItem(position = item.position + 1))
        }

        _currentItemsFromNote.postValue(currentItemViewModel)
    }

    fun createNewItemTextFromNote() {
        val currentItems = currentItemsFromNote.value
        val position = currentItems!!.size
        val currentItemViewModel = arrayListOf<MultiItem>()
        if (_currentItemsFromNote.value != null) {
            currentItemViewModel.addAll(_currentItemsFromNote.value!!)
        }
        currentItemViewModel.add(TextItem(position = position))
        _currentItemsFromNote.value = currentItemViewModel
    }


    fun setTextItemFromNote(noteId: Long, item: TextItem) {
        if (item.text.isNotEmpty()) {
            item.foreignId = noteId
            setTextItemFromNoteUseCase.execute(item)
        }
    }

    fun updateTextItemFromNote(item: TextItem) {
        updateTextItemFromNoteUseCase.execute(item)
    }


    private fun setImageItemFromNote(noteId: Long, item: ImageItem): Long {
        var id = 0L
        if (item.listImageItems.isNotEmpty()) {
            item.foreignId = noteId
            viewModelScope.launch(Dispatchers.IO) {
                val job = async {
                    setImageItemsWithImagesFromNoteUseCase.execute(item, item.listImageItems)
                }
                id = job.await()
            }
        }
        return id
    }

    fun updateImageItemFromNote(item: ImageItem) {
        viewModelScope.launch(Dispatchers.IO) {
            updateImageItemFromNoteUseCase.execute(item)
        }
    }

    fun getImageFromImageItem(idImageItem:Long): List<Image> {
        return getImagesFromImageItemUseCase.execute(idImageItem)
    }

    fun getImageItemsFromNote(id: Long): List<ImageItem> {
        return getImageItemsFromNoteUseCase.execute(id)
    }

    fun selectImageInImageItem(id: Long): List<ImageItem> {
        val listImageItems = getImageItemsFromNote(id)

        listImageItems.forEach { imageItem ->
            val listImage = getImageFromImageItem(imageItem.imageItemId)
            imageItem.listImageItems = listImage as ArrayList<Image>
        }

        return listImageItems
    }


    var actionMode: ActionMode? = null
    private val _selectedItemsNoteFromActionMode = MutableLiveData<MutableSet<MultiItem>>()
    val selectedItemsNoteFromActionMode: LiveData<MutableSet<MultiItem>> get() = _selectedItemsNoteFromActionMode

    fun changeSelectedItemsNoteFromActionMode(selectedItem: MultiItem) {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        if (list.contains(selectedItem)) {
            list.remove(selectedItem)
        } else {
            list.add(selectedItem)
        }
        _selectedItemsNoteFromActionMode.postValue(list)
    }

    fun deleteSelectionItemsNoteFromActionMode() {
        val list = mutableSetOf<MultiItem>()
        if (_selectedItemsNoteFromActionMode.value != null) {
            list.addAll(_selectedItemsNoteFromActionMode.value!!)
        }
        val listCurrent = _currentItemsFromNote.value!!.toMutableList()
        list.forEach {
            if (it is TextItem) { // @todo add Image Item
                listCurrent.remove(it)
                viewModelScope.launch(Dispatchers.IO) {
                    deleteTextItemFromNoteUseCase.execute(it)
                }
            } else if (it is ImageItem) {
                listCurrent.remove(it)
                viewModelScope.launch(Dispatchers.IO) {
                    deleteImageItemAndImageFiles(it)
                }
            }
        }
        _currentItemsFromNote.value = listCurrent
        clearSelectedItemsNoteFromActionMode()
    }

    fun clearSelectedItemsNoteFromActionMode() {
        _selectedItemsNoteFromActionMode.value = mutableSetOf()
    }

    private fun deleteImageItemAndImageFiles(imageItem: ImageItem) {
        deleteImagesFiles(imageItem.listImageItems)
        if (imageItem.foreignId != 0L) {
            deleteImageItemFromNote(imageItem)
        }
    }

    private fun deleteImagesFiles(listImages: List<Image>) {
        listImages.forEach { image ->
            deleteImageFile(image.uri)
            deleteImage(image)
        }
    }

    private fun deleteImageFile(imageUri: String) {
        val deletingImage = File(imageUri)
        if (deletingImage.exists()) {
            deletingImage.delete()
        }
    }

    fun deleteTempImageFiles() {
        currentItemsFromNote.value?.filterIsInstance<ImageItem>()?.forEach { imageItem ->
            imageItem.listImageItems.forEach { image ->
                if (image.isNew) {
                    deleteImageFile(image.uri)
                }
            }
        }
    }

    fun deleteImageFromImageItem(imageForDelete: Image) {
        var updatingImageItem: ImageItem? = null
        var indexUpdatedImageItem = 0
        currentItemsFromNote.value?.filterIsInstance<ImageItem>()
            ?.forEachIndexed { index, imageItem ->
                imageItem.listImageItems.forEach { image ->
                    if (image.id == imageForDelete.id) {

                        deleteImage(imageForDelete)
                        deleteImageFile(imageForDelete.uri)

                        updatingImageItem = imageItem
                        indexUpdatedImageItem = index
                    }
//                return@forEach
                }

//            val list  = imageItem.listImageItems.toMutableList()
//            list.remove(imageForDelete)
//            imageItem.listImageItems = list
//            updatingImageItem = imageItem
//            indexUpdatedImageItem = index
            }

        if (updatingImageItem != null) {
            val list = updatingImageItem!!.listImageItems.toMutableList()
            list.remove(imageForDelete)

            if (list.isEmpty()) {
                val currentItems = currentItemsFromNote.value?.toMutableList()
                if (currentItems != null) {

                    currentItems.remove(updatingImageItem!!)
                    _currentItemsFromNote.value = currentItems!!

                    if (updatingImageItem!!.foreignId != 0L) {
                        deleteImageItemFromNote(updatingImageItem!!)
                    }
                }


            } else {
                updatingImageItem!!.listImageItems = list
                updateItemFromCurrentListForNote(updatingImageItem!!)
            }

        }

    }


    private fun deleteImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteImageUseCase.execute(image)
        }
    }

    private fun deleteImageItemFromNote(imageItem: ImageItem) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteImageItemFromNoteUseCase.execute(imageItem)
        }
    }
}