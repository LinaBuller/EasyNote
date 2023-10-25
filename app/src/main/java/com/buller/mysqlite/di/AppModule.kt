package com.buller.mysqlite.di

import com.easynote.domain.utils.BackupManagerLocalDatabase
import com.easynote.domain.utils.ImageManager
import com.easynote.domain.viewmodels.AddFragmentViewModel
import com.easynote.domain.viewmodels.ArchiveFragmentViewModel
import com.easynote.domain.viewmodels.CategoriesFragmentViewModel
import com.easynote.domain.viewmodels.ColorPikerViewModel
import com.easynote.domain.viewmodels.ListFragmentViewModel
import com.easynote.domain.viewmodels.LoginFragmentViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import com.easynote.domain.viewmodels.RecycleBinFragmentViewModel
import com.easynote.domain.viewmodels.DeveloperFragmentViewModel
import com.easynote.domain.viewmodels.FirebaseViewModel
import com.easynote.domain.viewmodels.PermissionFragmentViewModel
import com.easynote.domain.viewmodels.SplashFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel {
        NotesViewModel(
            getIsFirstUsagesUseCase = get(),
            setIsFirstUsagesUseCase = get(),
            getPreferredThemeUseCase = get(),
            setPreferredThemeUseCase = get()
        )
    }

    viewModel {
        CategoriesFragmentViewModel(
            getCategoriesUseCase = get(),
            setCategoriesUseCase = get(),
            updateCategoryUseCase = get(),
            deleteCategoryUseCase = get()
        )
    }

    viewModel {
        ListFragmentViewModel(
            getListNotesUseCase = get(),
            updateNoteUseCase = get(),
            deleteNoteUseCase = get(),
            getCategoriesUseCase = get(),
            setCategoryUseCase = get(),
            setNoteWithCategoryUseCase = get(),
            getNoteWithCategoriesUseCase = get(),
            getTypeListUseCase = get(),
            setTypeListUseCase = get()
        )
    }

    viewModel {
        AddFragmentViewModel(
            setNoteUseCase = get(),
            updateNoteUseCase = get(),
            getCategoriesUseCase = get(),
            setCategoriesUseCase = get(),
            setNoteWithCategoryUseCase = get(),
            getNoteWithCategoriesUseCase = get(),
            getTextItemsFromNoteUseCase = get(),
            setTextItemFromNoteUseCase = get(),
            updateTextItemFromNoteUseCase = get(),
            deleteTextItemFromNoteUseCase = get(),
            getImageItemsFromNoteUseCase = get(),
            setImageItemsWithImagesFromNoteUseCase = get(),
            updateImageItemFromNoteUseCase = get(),
            deleteImageUseCase = get(),
            getImagesFromImageItemUseCase = get(), deleteImageItemFromNoteUseCase = get()
        )
    }

    viewModel {
        ColorPikerViewModel(
            getFavoriteColorsUseCase = get(),
            setFavoriteColorsUseCase = get(),
            deleteFavoriteColorsUseCase = get(),
        )
    }

    viewModel {
        LoginFragmentViewModel(
            getIsBioAuthUseCase = get(),
            setIsBioAuthUseCase = get()
        )
    }

    viewModel {
        ArchiveFragmentViewModel(
            getListNotesUseCase = get(),
            updateNoteUseCase = get(),
        )
    }

    viewModel {
        RecycleBinFragmentViewModel(
            getListNotesUseCase = get(),
            updateNoteUseCase = get(),
            deleteNoteUseCase = get(),
            getImageItemsFromNoteUseCase = get(),
            getTextItemsFromNoteUseCase = get(),
            getImagesFromImageItemUseCase = get(),
            deleteImageFromImageItemUseCase = get(),
            deleteTextItemFromNoteUseCase = get(),
            deleteImageItemFromNoteUseCase = get()
        )
    }

    viewModel {
        DeveloperFragmentViewModel()
    }

    viewModel {
        FirebaseViewModel(
            getCurrentUserUseCase = get(),
            signInWithEmailFirebaseUseCase = get(),
            signUpWithEmailFirebaseUseCase = get(),
            sendEmailVerificationFirebaseUseCase = get(),
            sendEmailRecoveryFirebaseUseCase = get(),
            logoutFirebaseUseCase = get(),
            signInWithGoogleFirebaseUseCase = get(),
            getPathLocalDatabaseUseCase = get(),
            setBackupToFirebaseUseCase = get(),
            setBackupToRealtimeDBFirebaseUseCase = get(),
            getBackupUriFromRealtimeDBFirebaseUseCase = get(),
            getBackupFromStorageFirebaseUseCase = get(),
            setImageFromStorageFirebaseUseCase = get(),
            getUidUserFirebaseUseCase = get(),
            getAllImagesForBackupUseCase = get(),
            setImageFromRealtimeFirebaseUseCase = get(),
            getBackupImageUriFromRealtimeFirebaseUseCase = get(),
            getBackupImageFromStorageFirebaseUseCase = get(),
            backupManager = get(),
            imageManager = get()
        )
    }

    viewModel {
        PermissionFragmentViewModel(sharedPref = get())
    }

    viewModel {
        SplashFragmentViewModel(sharedPrefRepository = get())
    }

    single<BackupManagerLocalDatabase> {
        BackupManagerLocalDatabase(context = get())
    }

    single<ImageManager> {
        ImageManager(context = get())
    }
}