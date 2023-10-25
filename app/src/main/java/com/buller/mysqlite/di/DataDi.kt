package com.buller.mysqlite.di

import com.easynote.domain.repository.AuthenticationRepository
import com.easynote.domain.repository.FirebaseRepository
import com.example.data.storage.room.NotesDao
import com.example.data.storage.repository.NotesRepositoryImpl
import com.example.data.storage.sharedprefs.SharedPrefNoteManager
import com.easynote.domain.repository.NoteRepository
import com.easynote.domain.repository.SharedPrefRepository
import com.example.data.storage.room.mappers.CategoryListMapper
import com.example.data.storage.room.mappers.CategoryMapper
import com.example.data.storage.room.mappers.FavColorListMapper
import com.example.data.storage.room.mappers.FavColorMapper
import com.example.data.storage.room.mappers.ImageItemMapper
import com.example.data.storage.room.mappers.ImageListMapper
import com.example.data.storage.room.mappers.ImageMapper
import com.example.data.storage.room.mappers.NoteListMapper
import com.example.data.storage.room.mappers.NoteMapper
import com.example.data.storage.room.mappers.TextItemMapper
import com.example.data.storage.repository.FirebaseAuthenticationRepositoryImpl
import com.example.data.storage.room.LocalDatabase
import com.example.data.storage.room.RoomDataSource
import com.example.data.storage.firebase.FirebaseAuthDataSource
import com.example.data.storage.firebase.FirebaseDatabaseDataSource
import com.example.data.storage.firebase.FirebaseStorageDataSource
import com.example.data.storage.repository.FirebaseRepositoryImpl
import com.example.data.storage.repository.SharedPrefRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.koin.dsl.module

val dataModule = module {

    single<SharedPrefNoteManager> {
        SharedPrefNoteManager(context = get())
    }

    single<SharedPrefRepository> {
        SharedPrefRepositoryImpl(get())
    }

    single<LocalDatabase> {
        LocalDatabase.newInstance(context = get())
    }

    single<NotesDao> {
        val database = get<LocalDatabase>()
        database.getDao()
    }

    single<NoteRepository> {
        NotesRepositoryImpl(
            roomDataSource = get(),
            sharedPrefNoteManager = get()
        )
    }

    single<RoomDataSource> {
        RoomDataSource(notesDao = get(), localDatabase = get())
    }

    single<FirebaseDatabase> {
        Firebase.database
    }

    single<FirebaseStorage> {
        Firebase.storage
    }

    single<FirebaseAuth> {
        Firebase.auth
    }

    single<FirebaseRepository> {
        FirebaseRepositoryImpl(
            database = get(), storage = get()
        )
    }

    single<FirebaseAuthDataSource> {
        FirebaseAuthDataSource(get())
    }

    single<AuthenticationRepository> {
        FirebaseAuthenticationRepositoryImpl(get())
    }

    single<FirebaseStorageDataSource> {
        FirebaseStorageDataSource(get())
    }

    single<FirebaseDatabaseDataSource> {
        FirebaseDatabaseDataSource(get())
    }

    single<NoteMapper> {
        NoteMapper()
    }

    single<NoteListMapper> {
        NoteListMapper(get())
    }

    single<FavColorMapper> {
        FavColorMapper()
    }

    single<FavColorListMapper> {
        FavColorListMapper(get())
    }

    single<TextItemMapper> {
        TextItemMapper()
    }

    single<ImageMapper> {
        ImageMapper()
    }
    single<ImageListMapper> {
        ImageListMapper(get())
    }

    single<ImageItemMapper> {
        ImageItemMapper()
    }
    single<CategoryMapper> {
        CategoryMapper()
    }

    single<CategoryListMapper> {
        CategoryListMapper(get())
    }
}