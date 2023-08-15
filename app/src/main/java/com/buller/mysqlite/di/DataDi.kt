package com.buller.mysqlite.di

import com.example.data.storage.NotesDao
import com.example.data.storage.NotesDatabase
import com.example.data.storage.repository.NotesRepositoryImpl
import com.example.data.storage.sharedprefs.SharedPrefNoteManager
import com.easynote.domain.repository.NoteRepository
import org.koin.dsl.module

val dataModule = module {

    single<SharedPrefNoteManager> {
        SharedPrefNoteManager(context = get())
    }


    single<NotesDatabase> {
        NotesDatabase.newInstance(context = get())
    }

    single<NotesDao> {
        val database = get<NotesDatabase>()
        database.getDao()
    }

    single<NoteRepository> {
        NotesRepositoryImpl(notesDao = get(), sharedPrefNoteManager = get())
    }


}