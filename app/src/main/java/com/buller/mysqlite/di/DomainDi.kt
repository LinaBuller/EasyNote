package com.buller.mysqlite.di

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
import org.koin.dsl.module

val domainModule = module {

    factory<GetListNotesUseCase> {
        GetListNotesUseCase(noteRepository = get())
    }

    factory<SetNoteUseCase> {
        SetNoteUseCase(noteRepository = get())
    }

    factory<UpdateNoteUseCase> {
        UpdateNoteUseCase(noteRepository = get())
    }

    factory<DeleteNoteUseCase> {
        DeleteNoteUseCase(noteRepository = get())
    }
    factory<GetCategoriesUseCase> {
        GetCategoriesUseCase(noteRepository = get())
    }
    factory<SetCategoriesUseCase> {
        SetCategoriesUseCase(noteRepository = get())
    }
    factory<UpdateCategoryUseCase> {
        UpdateCategoryUseCase(noteRepository = get())
    }
    factory<DeleteCategoryUseCase> {
        DeleteCategoryUseCase(noteRepository = get())
    }
    factory<SetNoteWithCategoryUseCase> {
        SetNoteWithCategoryUseCase(noteRepository = get())
    }


    factory<GetFavoriteColorsUseCase> {
        GetFavoriteColorsUseCase(noteRepository = get())
    }

    factory<SetFavoriteColorsUseCase> {
        SetFavoriteColorsUseCase(noteRepository = get())
    }

    factory<DeleteFavoriteColorsUseCase> {
        DeleteFavoriteColorsUseCase(noteRepository = get())
    }

    factory<GetTextItemsFromNoteUseCase> {
        GetTextItemsFromNoteUseCase(noteRepository = get())
    }

    factory<SetTextItemFromNoteUseCase> {
        SetTextItemFromNoteUseCase(noteRepository = get())
    }
    factory<UpdateTextItemFromNoteUseCase> {
        UpdateTextItemFromNoteUseCase(noteRepository = get())
    }

    factory<DeleteTextItemFromNoteUseCase> {
        DeleteTextItemFromNoteUseCase(noteRepository = get())
    }

    factory<GetImageItemsFromNoteUseCase> {
        GetImageItemsFromNoteUseCase(noteRepository = get())
    }

    factory<SetImageItemsWithImagesFromNoteUseCase> {
        SetImageItemsWithImagesFromNoteUseCase(noteRepository = get())
    }

    factory<UpdateImageItemFromNoteUseCase> {
        UpdateImageItemFromNoteUseCase(noteRepository = get())
    }

    factory<GetNoteWithCategoriesUseCase> {
        GetNoteWithCategoriesUseCase(noteRepository = get())
    }

    factory<DeleteImageFromImageItemUseCase> {
        DeleteImageFromImageItemUseCase(noteRepository = get())
    }


}