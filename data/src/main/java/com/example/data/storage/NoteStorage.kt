package com.example.data.storage

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.data.storage.models.StorageNote


interface NoteStorage {

    fun setNote(storageNote: StorageNote): Long
    fun getNotes(query: SimpleSQLiteQuery): LiveData<List<StorageNote>>
}