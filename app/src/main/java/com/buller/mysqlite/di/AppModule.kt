package com.buller.mysqlite.di

import com.easynote.domain.viewmodels.NotesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel {
        NotesViewModel(
            getListNotesUseCase = get(),
            setNoteUseCase = get(),
            updateNoteUseCase = get(),
            deleteNoteUseCase = get(),
            getCategoriesUseCase = get(),
            getNoteWithCategoriesUseCase = get(),
            setCategoriesUseCase = get(),
            updateCategoryUseCase = get(),
            deleteCategoryUseCase = get(),
            setNoteWithCategoryUseCase = get(),
            getFavoriteColorsUseCase = get(),
            setFavoriteColorsUseCase = get(),
            deleteFavoriteColorsUseCase = get(),
            getTextItemsFromNoteUseCase = get(),
            setTextItemFromNoteUseCase = get(),
            updateTextItemFromNoteUseCase = get(),
            deleteTextItemFromNoteUseCase = get(),
            getImageItemsFromNoteUseCase = get(),
            setImageItemsWithImagesFromNoteUseCase = get(),
            updateImageItemFromNoteUseCase = get(),
            deleteImageFromImageItemUseCase = get()
        )
    }
}