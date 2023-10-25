package com.example.data.storage.repository

import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.BackupImage
import com.easynote.domain.repository.FirebaseRepository
import com.example.data.storage.firebase.FirebaseDatabaseDataSource
import com.example.data.storage.firebase.models.FirebaseDump
import com.example.data.storage.firebase.FirebaseStorageDataSource
import com.example.data.storage.firebase.models.FirebaseImage
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.StorageReference

class FirebaseRepositoryImpl(
    val database: FirebaseDatabaseDataSource,
    val storage: FirebaseStorageDataSource,
) : FirebaseRepository {

    override fun uploadImage(uid: String?,idImage:String): StorageReference = storage.getRefForImage(uid!!,idImage)

    override fun uploadDatabaseFile(uid: String?, name: String): StorageReference =
        storage.getRefForDatabase(uid!!, name)

    override fun insertDumpToDatabase(backup: BackupDatabase): Task<Void> {
        val firebaseDump = FirebaseDump(
            uid = backup.uid,
            backupDatabase = backup.backupDatabase,
            timeStamp = backup.timeStamp
        )
        return database.insertDump(firebaseDump)
    }

    override fun readDumpFromDatabase(uid: String): Task<DataSnapshot> {
        return database.readDump(uid)
    }

    override fun downloadDumpFromStorage(uid: String, name: String): StorageReference {
        return storage.download(uid, name)
    }

    override fun downloadDumpFromStorageUrl(url: String): StorageReference {
        return storage.downloadFromUrl(url)
    }

    override fun insertImageToDatabase(backupImage: BackupImage): Task<Void> {
        val imageFirebase = FirebaseImage(
            uid = backupImage.uid,
            id = backupImage.id,
            foreignId = backupImage.foreignId,
            uri = backupImage.uri,
            uriStorage = backupImage.uriStorage,
            position = backupImage.position
        )
        return database.insertImage(imageFirebase)
    }

    override fun readImageFromDatabase(uid: String): Task<DataSnapshot> {
        return database.readImage(uid)
    }

    override fun downloadImageFromStorage(url: String): StorageReference {
       return storage.downloadFromUrl(url)
    }
}