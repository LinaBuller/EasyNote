package com.buller.mysqlite.di

import androidx.room.Room
import com.example.data.storage.ConstantsDbName
import com.example.data.storage.NotesDao
import com.example.data.storage.NotesDatabase
import com.example.data.storage.NoteStorage
import com.example.data.storage.repository.NotesRepositoryImpl
import com.example.data.storage.sharedprefs.SharedPrefNoteStorage
import com.easynote.domain.repository.NoteRepository
import org.koin.dsl.module

val dataModule = module {

    single<NoteStorage> {
        SharedPrefNoteStorage(context = get())
    }


    single<NotesDatabase> {
        NotesDatabase.newInstance(context = get())
    }

    single<NotesDao> {
        val database = get<NotesDatabase>()
        database.getDao()
    }

    single<com.easynote.domain.repository.NoteRepository> {
        NotesRepositoryImpl(notesDao = get())
    }


}