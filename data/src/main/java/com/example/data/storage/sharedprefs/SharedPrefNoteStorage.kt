package com.example.data.storage.sharedprefs

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.data.storage.NoteStorage
import com.example.data.storage.models.StorageNote

private const val SHARED_PREFS_NAME = "shared_prefs_name"
class SharedPrefNoteStorage(val context: Context) : NoteStorage {
    val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun setNote(storageNote: StorageNote): Long {
        TODO("Not yet implemented")
    }

    override fun getNotes(query: SimpleSQLiteQuery): LiveData<List<StorageNote>> {
        TODO("Not yet implemented")
    }
}