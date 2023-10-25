package com.easynote.domain.repository

import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.BackupImage
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.StorageReference

interface FirebaseRepository {
    fun uploadImage(uid: String?,idImage:String): StorageReference
    fun uploadDatabaseFile(uid: String?, name:String): StorageReference
    fun insertDumpToDatabase(backup: BackupDatabase): Task<Void>
    fun readDumpFromDatabase(uid: String): Task<DataSnapshot>
    fun downloadDumpFromStorage(uid: String,name: String): StorageReference
    fun downloadDumpFromStorageUrl(url: String): StorageReference
    fun insertImageToDatabase(backupImage: BackupImage): Task<Void>
     fun readImageFromDatabase(uid: String): Task<DataSnapshot>
    fun downloadImageFromStorage(url: String): StorageReference
}