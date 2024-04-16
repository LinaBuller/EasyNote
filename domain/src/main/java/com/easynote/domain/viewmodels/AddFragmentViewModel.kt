package com.easynote.domain.viewmodels

import android.text.Html
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.BackgroundColor
import com.easynote.domain.models.Category
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.Note
import com.easynote.domain.models.TextItem
import com.easynote.domain.usecase.DeleteNoteUseCase
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
import com.example.domain.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayDeque

class AddFragmentViewModel(
    val setNoteUseCase: SetNoteUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
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
) : BaseViewModel() {

    private var noteId = 0L

    fun setNoteId(id: Long) {
        noteId = id
        if (noteId == 0L) {
            _selectedNote.postValue(Note(isChanged = true))
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

    suspend fun setNote(note: Note): Long {
        val id = viewModelScope.async(Dispatchers.IO) {
            setNoteUseCase.execute(note)
        }
        return id.await()
    }


    /**
     * This method is used to determine if changes have been made by the user.
     * Specifically:
     * - pinning/unpinning a note,
     * - adding/deleting to favorites,
     * - adding/deleting text items,
     * - adding/deleting image items,
     * - adding/deleting pictures,
     * - adding/deleting pictures,
     * - after changing categories,
     * - after changing color background,
     * - after moving pictures between two items,
     * - after block/unblock edited text
     * **/
    fun setChangeInSelectedNote() {
        _selectedNote.value?.isChanged = true
        _selectedNote.value?.lastChangedTime = CurrentTimeInFormat.getCurrentTime()
    }



    fun setIsChange(){

    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNoteUseCase.execute(note)
        }

    }

    fun changeNoteArchive(isArchive: Boolean) {
        val selectedNote = selectedNote.value
        if (selectedNote != null) {
            val changedSelectedNote = selectedNote.copy(
                isArchive = isArchive,
                isDeleted = false,
                lastChangedTime = CurrentTimeInFormat.getCurrentTime()
            )
            updateNote(changedSelectedNote)
        }
    }

    fun changeNoteDelete(isDelete: Boolean) {
        val selectedNote = selectedNote.value
        if (selectedNote != null) {
            val changedSelectedNote = selectedNote.copy(
                isDeleted = isDelete,
                isArchive = false,
                lastChangedTime = CurrentTimeInFormat.getCurrentTime()
            )
            updateNote(changedSelectedNote)
        }
    }

    fun changeNotePin(isPin: Boolean) {
        val selectedNote = selectedNote.value
        selectedNote?.isPin = isPin
        _selectedNote.value = _selectedNote.value
        setChangeInSelectedNote()
    }

    fun changeNoteFavorite(isFavorite: Boolean) {
        val selectedNote = selectedNote.value
        selectedNote?.isFavorite = isFavorite
        _selectedNote.value = _selectedNote.value
        setChangeInSelectedNote()
    }

    private val _existCategories = getCategoriesUseCase.execute()
    val existCategories: LiveData<List<Category>> get() = _existCategories

    fun setCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            category.position = _existCategories.value!!.size
            setCategoriesUseCase.execute(category)
        }
    }

    private var _currentCategories = MutableLiveData<List<Category>>(emptyList())
    val currentCategories: LiveData<List<Category>> get() = _currentCategories

    fun updateCurrentCategories(listSelectedCategory: List<Category>) {
        _currentCategories.postValue(listOf())
        _currentCategories.postValue(listSelectedCategory)
        setChangeInSelectedNote()
    }

    private var _currentItemsFromNote = MutableLiveData<List<MultiItem>>()
    val currentItemsNote: LiveData<List<MultiItem>> get() = _currentItemsFromNote

    val undo: ArrayDeque<OnUndoRedo> = ArrayDeque()
    val redo: ArrayDeque<OnUndoRedo> = ArrayDeque()

    fun undoTextFromItem() {
        val currentCommand = undo.pop()
        redo.push(currentCommand)
        val command = currentCommand as CommandReplaceText
        currentItemsNote.value?.filterIsInstance(TextItem::class.java)?.forEach { item ->
            if (item.itemTextId == command.idItems) {
                val simpleText = Html.fromHtml(item.text,Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL).toString().trimEnd('\n')
                val text = item.text
                val newText = currentCommand.undo(simpleText)
                updateTextItemsFromUndoRedo(item, newText, command.positionItem)
            }
        }
    }

    fun redoTextFromItem() {
        val currentCommand = redo.pop()
        undo.push(currentCommand)
        val command = currentCommand as CommandReplaceText
        currentItemsNote.value?.filterIsInstance(TextItem::class.java)?.forEach { item ->
            if (item.itemTextId == command.idItems) {
                val text = item.text
                val newText = currentCommand.redo(text)
                updateTextItemsFromUndoRedo(item, newText, command.positionItem)
            }
        }
    }

    private fun updateTextItemsFromUndoRedo(item: MultiItem, newText: String, position: Int) {
        val currentItemViewModel = currentItemsNote.value!!.toMutableList()
        var newItem: TextItem? = null
        var indexItem = 0

        currentItemViewModel
            .forEachIndexed { index, currItem ->
                if (item is TextItem && currItem is TextItem) {
                    if (currItem.itemTextId == item.itemTextId){
                        newItem = currItem
                        indexItem = index
                    }
                }
            }


        currentItemViewModel[indexItem] = newItem?.copy(text = newText, position = position)!!
        setChangeInSelectedNote()
        _currentItemsFromNote.value = currentItemViewModel
    }

    private val _listCurGradientColors = MutableLiveData(
        listOf(
            BackgroundColor(0, ColorWithHSL(-1, 0F, 0F, 1F)),
            BackgroundColor(1, ColorWithHSL(-1, 0F, 0F, 1F))
        )
    )
    val listCurrentGradientColors: LiveData<List<BackgroundColor>> get() = _listCurGradientColors

    fun setSelectedColors(listGradientColors: List<BackgroundColor>) {
        _listCurGradientColors.value = listGradientColors
        setChangeInSelectedNote()
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

    fun setImagesToNote(uris: Map<String, String>) {
        val imageItem = getImageItemForNewImage()

        if (imageItem == null) {
            val imageItemToCurrentList = setImagesToImageItem(uris, ImageItem())
            setItemFromCurrentListItemsForNote(imageItemToCurrentList)
        } else {
            val updatedItem = setImagesToImageItem(uris, imageItem)
            updateItemFromCurrentListForNote(updatedItem)
        }

        setChangeInSelectedNote()
    }

    private fun getImageItemForNewImage(): ImageItem? {
        val currentItems = currentItemsNote.value ?: return null
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

    private fun setImagesToImageItem(
        uris: Map<String, String>,
        itemImage: ImageItem
    ): ImageItem {
        val newImageList = arrayListOf<Image>()
        val oldListImages = itemImage.listImageItems
        var sizeOldListImages = oldListImages.size

        uris.forEach { (id, uri) ->
            val image =
                Image(id = id, uri = uri, position = sizeOldListImages, isNew = true)
            newImageList.add(image)
            sizeOldListImages++
        }

        val union = arrayListOf<Image>()
        union.addAll(oldListImages)
        union.addAll(newImageList)
        return itemImage.copy(listImageItems = union)
    }

    private fun updateItemFromCurrentListForNote(item: MultiItem) {
        val currentItemViewModel = _currentItemsFromNote.value!!.toMutableList()
        currentItemViewModel.forEachIndexed { index, multiItem ->
            if (multiItem.position == item.position) {
                currentItemViewModel[index] = item
            }
        }
        _currentItemsFromNote.postValue(currentItemViewModel)
    }

    fun setItemFromCurrentListItemsForNote(item: MultiItem) {
        val currentItemViewModel = currentItemsNote.value!!.toMutableList()

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
        val currentItems = currentItemsNote.value
        val position = currentItems!!.size
        val newListItems = currentItems.plus(TextItem(position = position))
        _currentItemsFromNote.value = newListItems
        setChangeInSelectedNote()
    }

    fun getImageFromImageItem(idImageItem: Long): List<Image> {
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


    private val _actionMode = MutableLiveData<ActionMode?>()
    val actionMode: LiveData<ActionMode?> get() =  _actionMode
    //var actionMode: ActionMode? = null

    fun setActionMode(actionMode: ActionMode?){
        _actionMode.value = actionMode
    }

    private val _selectedItemsNoteFromActionMode =
        MutableLiveData<MutableSet<MultiItem>>(mutableSetOf())
    val selectedItemsNoteFromActionMode: LiveData<MutableSet<MultiItem>> get() = _selectedItemsNoteFromActionMode

    fun changeSelectedItemsNoteFromActionMode(selectedItem: MultiItem) {
        val list = _selectedItemsNoteFromActionMode.value!!.toMutableSet()
        if (list.contains(selectedItem)) {
            list.remove(selectedItem)
        } else {
            list.add(selectedItem)
        }
        _selectedItemsNoteFromActionMode.postValue(list)
    }

    fun deleteSelectionItemsNoteFromActionMode() {
        val listSelectedItems = _selectedItemsNoteFromActionMode.value!!.toMutableSet()
        val listItems = currentItemsNote.value!!.toMutableList()
        listSelectedItems.forEach { multiItem ->
            multiItem.isDeleted = true
            multiItem.position = -1
            listItems.remove(multiItem)
        }

        listItems.forEachIndexed { index, multiItem ->
            multiItem.position = index
        }
        listItems.addAll(listSelectedItems)
        _currentItemsFromNote.value = listItems
        clearSelectedItemsNoteFromActionMode()
        setChangeInSelectedNote()
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
        currentItemsNote.value?.filterIsInstance<ImageItem>()?.forEach { imageItem ->
            imageItem.listImageItems.forEach { image ->
                if (image.isNew) {
                    deleteImageFile(image.uri)
                }
            }
        }
    }

    fun deleteImageFromImageItem(imageForDelete: Image) {
        var updatingImageItem: ImageItem? = null
        currentItemsNote.value?.filterIsInstance<ImageItem>()?.forEach { imageItem ->
            imageItem.listImageItems.forEach { image ->
                if (image.id == imageForDelete.id) {

                    deleteImage(imageForDelete)
                    deleteImageFile(imageForDelete.uri)
                    updatingImageItem = imageItem
                }
            }
        }

        if (updatingImageItem != null) {
            val list = updatingImageItem!!.listImageItems.toMutableList()
            list.remove(imageForDelete)

            if (list.isEmpty()) {
                val currentItems = currentItemsNote.value?.toMutableList()
                updatingImageItem!!.isDeleted = true
                _currentItemsFromNote.value = currentItems!!

//                if (updatingImageItem!!.foreignId != 0L) {
//                    deleteImageItemFromNote(updatingImageItem!!)
//                }

            } else {
                updatingImageItem!!.listImageItems = list
                updateItemFromCurrentListForNote(updatingImageItem!!)
            }
            setChangeInSelectedNote()
        }

    }

    private fun deleteImage(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteImageUseCase.execute(image)
        }
    }


    private fun deleteImageItemFromNote(imageItem: ImageItem) {
        deleteImageItemFromNoteUseCase.execute(imageItem)
    }

    private fun deleteTextItemFromNote(textItem: TextItem) {
        deleteTextItemFromNoteUseCase.execute(textItem)
    }

    fun setImageFromTarget(image: Image, targetPosition: Int, targetImageItem: ImageItem) {
        val targetList = targetImageItem.listImageItems.toMutableList()
        val newImage = image.copy(foreignId = targetImageItem.imageItemId, isNew = true)
        targetList.add(newImage)
        val newTargetImageItem = targetImageItem.copy(listImageItems = targetList)
        updateItemFromCurrentListForNote(targetImageItem, newTargetImageItem)
    }

    private fun updateItemFromCurrentListForNote(originalItem: MultiItem, updatedItem: MultiItem) {
        val currentList = _currentItemsFromNote.value?.toMutableList()
        currentList?.forEachIndexed { index, multiItem ->
            if (multiItem == originalItem) {
                currentList[index] = updatedItem
            }
        }
        _currentItemsFromNote.value = currentList!!
        setChangeInSelectedNote()
    }

    fun removeSourceImage(image: Image, sourceImageItem: ImageItem) {
        val sourceList = sourceImageItem.listImageItems.toMutableList()
        sourceList.remove(image)
        val newSourceImageItem = if (sourceList.isEmpty()) {
            sourceImageItem.copy(listImageItems = sourceList, isDeleted = true)
        } else {
            sourceImageItem.copy(listImageItems = sourceList)
        }
        updateItemFromCurrentListForNote(sourceImageItem, newSourceImageItem)
    }

    fun removingTextItemByClick(item: TextItem) {
        item.isDeleted = true
    }

    fun saveNoteToDatabase(title: String) {
        var currentNote = selectedNote.value
        val items = currentItemsNote.value
        val mergedText = mergeText(items!!)
        if (currentNote!!.isChanged) {
            setVisibleProgressBar(true)
            val colors = listCurrentGradientColors.value
            val categories = currentCategories.value
            currentNote = setFieldToNote(title, mergedText, currentNote, colors!!)

            if (noteId == 0L) {
                saveNewNote(currentNote, items, categories!!)
                setMessage(R.string.note_added)
                setVisibleProgressBar(false)
            } else {
                saveOldNote(currentNote, items, categories!!)
                setMessage(R.string.note_updated)
                setVisibleProgressBar(false)
            }
            clearOldDataFromNote()
        } else {
            setMessage(R.string.note_not_saved)
        }
    }

    private fun saveNewNote(currentNote: Note, items: List<MultiItem>, categories: List<Category>) {
        viewModelScope.launch {
            currentNote.createTime = CurrentTimeInFormat.getCurrentTime()
            val id = setNote(currentNote)
            currentNote.id = id

            items.forEach { multiItem ->
                when (multiItem) {
                    is TextItem -> {
                        setTextItemFromNote(currentNote.id, multiItem)
                    }

                    is ImageItem -> {
                        setImageItemFromNote(currentNote.id, multiItem)
                    }
                }
            }
            setCategoriesWithNoteId(currentNote, categories)
        }
    }

    private fun saveOldNote(currentNote: Note, items: List<MultiItem>, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNote(currentNote)
            items.forEach { item ->
                when (item) {
                    is TextItem -> {
                        if (item.isDeleted) {
                            deleteTextItemFromNote(item)
                            return@forEach
                        }

                        if (item.foreignId == 0L) {
                            setTextItemFromNote(currentNote.id, item)
                        } else {
                            updateTextItemFromNote(item)
                        }
                    }

                    is ImageItem -> {
                        if (item.isDeleted) {
                            deleteImageItemAndImageFiles(item)
                            return@forEach
                        }
                        if (item.foreignId == 0L) {
                            setImageItemFromNote(currentNote.id, item)
                        } else {
                            updateImageItemFromNote(item)
                        }

                    }
                }
            }
            setCategoriesWithNoteId(currentNote, categories)
        }
    }

    private fun mergeText(items: List<MultiItem>): String {
        val text = StringBuilder("")
        items.filterIsInstance<TextItem>()
            .forEach { textItem ->
                text.append(textItem.text)
                    .append("\n")
            }
        return text.toString()
    }

    private fun setFieldToNote(
        title: String,
        text: String,
        currentNote: Note,
        colors: List<BackgroundColor>
    ): Note {
        val note: Note
        if (title.isEmpty()) {
            title.plus("Untitled")
        }
        val firstColorGradient = colors[0]
        val secondColorGradient = colors[1]

        note = currentNote.copy(
            title = title,
            content = text,
            text = text,
            lastChangedTime = CurrentTimeInFormat.getCurrentTime(),
            gradientColorFirst = firstColorGradient.colorWithHSL.color,
            gradientColorFirstH = firstColorGradient.colorWithHSL.h,
            gradientColorFirstS = firstColorGradient.colorWithHSL.s,
            gradientColorFirstL = firstColorGradient.colorWithHSL.l,
            gradientColorSecond = secondColorGradient.colorWithHSL.color,
            gradientColorSecondH = secondColorGradient.colorWithHSL.h,
            gradientColorSecondS = secondColorGradient.colorWithHSL.s,
            gradientColorSecondL = secondColorGradient.colorWithHSL.l,
            isChanged = false,
        )

        return note
    }

    fun setTextItemFromNote(id: Long, item: TextItem) {
        viewModelScope.launch(Dispatchers.IO) {
            if (item.text.isNotEmpty()) {
                item.foreignId = id
                setTextItemFromNoteUseCase.execute(item)
            }
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

    fun setCategoriesWithNoteId(note: Note, categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) {
            setNoteWithCategoryUseCase.execute(note, categories)
        }
    }

    fun setEditable(isEditable: Boolean) {
        val selectedNote = selectedNote.value
        selectedNote?.isEditable = isEditable
        setChangeInSelectedNote()
    }

    fun deleteSelectedNote() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedNote.value?.let {
                deleteTextItem(it.id)
                deleteImageItem(it.id)
                deleteNote(it)
                clearOldDataFromNote()
            }
        }
    }

    private fun deleteTextItem(noteId: Long) {
        val deletingTextItem = getTextItemsFromNote(noteId)
        deletingTextItem.forEach {
            deleteTextItemFromNoteUseCase.execute(it as TextItem)
        }
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

    private fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNoteUseCase.execute(note)
        }
    }

    private fun clearOldDataFromNote() {
        _selectedNote.postValue(null)
        _currentItemsFromNote.value = emptyList()
        _listCurGradientColors.value = emptyList()
        _currentCategories.value = emptyList()
    }

}