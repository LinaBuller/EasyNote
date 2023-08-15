package com.easynote.domain.viewmodels

import androidx.appcompat.view.ActionMode
import android.net.Uri
import androidx.lifecycle.*

import com.easynote.domain.models.Category
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.Note
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
import com.easynote.domain.utils.edittextnote.CommandReplaceText
import com.easynote.domain.utils.edittextnote.OnUndoRedo
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.usecase.sharedPreferenses.GetIsFirstUsagesUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetPreferredThemeUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetIsFirstUsagesUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetPreferredThemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList


class NotesViewModel(
    val getIsFirstUsagesUseCase: GetIsFirstUsagesUseCase,
    val setIsFirstUsagesUseCase: SetIsFirstUsagesUseCase,
    val getPreferredThemeUseCase: GetPreferredThemeUseCase,
    val setPreferredThemeUseCase: SetPreferredThemeUseCase
) : ViewModel() {

    private val _currentTheme = MutableLiveData(CurrentTheme(0))
    val currentTheme: LiveData<CurrentTheme> = _currentTheme

    fun changeTheme(id: Int) {
        val currentThemeNow = _currentTheme.value
        if (currentThemeNow != null) {
            _currentTheme.value = currentThemeNow.copy(themeId = id)
        }
        if (id == 0) {
            setPreferredThemeSharedPref(true)
        } else {
            setPreferredThemeSharedPref(false)
        }
    }

    var isFirstUsages: Boolean = true
    var preferredTheme: Boolean = true

    fun getIsFirstUsagesSharedPref() {
        isFirstUsages = getIsFirstUsagesUseCase.execute()
    }

    fun setIsFirstUsagesSharPref(isFirst: Boolean) {
        isFirstUsages = isFirst
        setIsFirstUsagesUseCase.execute(isFirst)
    }

    fun getPreferredThemeSharedPref() {
        preferredTheme = getPreferredThemeUseCase.execute()
    }

    private fun setPreferredThemeSharedPref(prefTheme: Boolean) {
        preferredTheme = prefTheme
        setPreferredThemeUseCase.execute(prefTheme)
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
                //updateNote(it)
            } else {
                // deleteNote(it)
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
            //updateNote(it)
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
            //updateNote(it)
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



}



