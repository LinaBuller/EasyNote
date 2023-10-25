package com.buller.mysqlite.di

import com.easynote.domain.usecase.DeleteNoteUseCase
import com.easynote.domain.usecase.GetListNotesUseCase
import com.easynote.domain.usecase.GetPathLocalDatabaseUseCase
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
import com.easynote.domain.usecase.firebase.GetBackupFromStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupImageFromStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupImageUriFromRealtimeFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetBackupUriFromRealtimeDBFirebaseUseCase
import com.easynote.domain.usecase.firebase.GetCurrentUserUseCase
import com.easynote.domain.usecase.firebase.GetUidUserFirebaseUseCase
import com.easynote.domain.usecase.firebase.LogoutFirebaseUseCase
import com.easynote.domain.usecase.firebase.SendEmailRecoveryFirebaseUseCase
import com.easynote.domain.usecase.firebase.SendEmailVerificationFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetBackupToRealtimeDBFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetBackupToStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetImageFromRealtimeFirebaseUseCase
import com.easynote.domain.usecase.firebase.SetImageFromStorageFirebaseUseCase
import com.easynote.domain.usecase.firebase.SignInWithEmailFirebaseUseCase
import com.easynote.domain.usecase.firebase.SignInWithGoogleFirebaseUseCase
import com.easynote.domain.usecase.firebase.SignUpWithEmailFirebaseUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.DeleteImageItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.DeleteTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetAllImagesForBackupUseCase
import com.easynote.domain.usecase.itemsNote.GetImageItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.GetImagesFromImageItemUseCase
import com.easynote.domain.usecase.itemsNote.GetTextItemsFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.SetImageItemsWithImagesFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.SetTextItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.UpdateImageItemFromNoteUseCase
import com.easynote.domain.usecase.itemsNote.UpdateTextItemFromNoteUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetIsBioAuthUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetIsFirstUsagesUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetPreferredThemeUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetTypeListUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetIsBioAuthUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetIsFirstUsagesUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetPreferredThemeUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetTypeListUseCase
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

    factory<GetIsFirstUsagesUseCase> {
        GetIsFirstUsagesUseCase(noteRepository = get())
    }

    factory<SetIsFirstUsagesUseCase> {
        SetIsFirstUsagesUseCase(noteRepository = get())
    }

    factory<GetPreferredThemeUseCase> {
        GetPreferredThemeUseCase(noteRepository = get())
    }

    factory<SetPreferredThemeUseCase> {
        SetPreferredThemeUseCase(noteRepository = get())
    }

    factory<GetTypeListUseCase> {
        GetTypeListUseCase(noteRepository = get())
    }

    factory<SetTypeListUseCase> {
        SetTypeListUseCase(noteRepository = get())
    }

    factory<GetIsBioAuthUseCase> {
        GetIsBioAuthUseCase(noteRepository = get())
    }

    factory<SetIsBioAuthUseCase> {
        SetIsBioAuthUseCase(noteRepository = get())
    }

    factory<GetImagesFromImageItemUseCase> {
        GetImagesFromImageItemUseCase(noteRepository = get())
    }

    factory<DeleteImageItemFromNoteUseCase> {
        DeleteImageItemFromNoteUseCase(noteRepository = get())
    }

    factory<SetBackupToStorageFirebaseUseCase> {
        SetBackupToStorageFirebaseUseCase(firebaseRepository = get())
    }

    factory<SetBackupToRealtimeDBFirebaseUseCase> {
        SetBackupToRealtimeDBFirebaseUseCase(firebaseRepository = get())
    }

    factory<GetBackupUriFromRealtimeDBFirebaseUseCase> {
        GetBackupUriFromRealtimeDBFirebaseUseCase(firebaseRepository = get())
    }

    factory<GetBackupFromStorageFirebaseUseCase> {
        GetBackupFromStorageFirebaseUseCase(firebaseRepository = get())
    }

    factory<GetCurrentUserUseCase> {
        GetCurrentUserUseCase(authRepository = get())
    }

    factory<SignInWithEmailFirebaseUseCase> {
        SignInWithEmailFirebaseUseCase(authRepository = get())
    }

    factory<SignUpWithEmailFirebaseUseCase> {
        SignUpWithEmailFirebaseUseCase(authRepository = get())
    }

    factory<SendEmailVerificationFirebaseUseCase> {
        SendEmailVerificationFirebaseUseCase(authRepository = get())
    }

    factory<SendEmailRecoveryFirebaseUseCase> {
        SendEmailRecoveryFirebaseUseCase(authRepository = get())
    }

    factory<LogoutFirebaseUseCase> {
        LogoutFirebaseUseCase(authRepository = get())
    }

    factory<SignInWithGoogleFirebaseUseCase> {
        SignInWithGoogleFirebaseUseCase(authRepository = get())
    }

    factory<GetPathLocalDatabaseUseCase> {
        GetPathLocalDatabaseUseCase(noteRepository = get())
    }

    factory<SetImageFromStorageFirebaseUseCase> {
        SetImageFromStorageFirebaseUseCase(firebaseRepository = get())
    }

    factory<GetUidUserFirebaseUseCase> {
        GetUidUserFirebaseUseCase(get())
    }

    factory<GetAllImagesForBackupUseCase> {
        GetAllImagesForBackupUseCase(get())
    }

    factory <SetImageFromRealtimeFirebaseUseCase>{
        SetImageFromRealtimeFirebaseUseCase(get())
    }

    factory <GetBackupImageUriFromRealtimeFirebaseUseCase>{
        GetBackupImageUriFromRealtimeFirebaseUseCase(get())
    }

    factory <GetBackupImageFromStorageFirebaseUseCase>{
        GetBackupImageFromStorageFirebaseUseCase(get())
    }



}