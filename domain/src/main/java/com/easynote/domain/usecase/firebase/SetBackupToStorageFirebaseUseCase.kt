package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream
import java.util.Calendar

class SetBackupToStorageFirebaseUseCase(private val firebaseRepository: FirebaseRepository) {
    fun execute(backupFile: File,uid:String): Flow<NetworkResult<BackupDatabase>> = callbackFlow {
        trySend(NetworkResult.Loading())
        val dataObj = BackupDatabase(uid = uid)
        val ref = firebaseRepository.uploadDatabaseFile(uid, backupFile.name)

        FileInputStream(backupFile).use { stream ->
            val allBytes = stream.readBytes()
            val upTask = ref.putBytes(allBytes)
            upTask.continueWithTask {
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dataObj.backupDatabase = task.result.toString()
                    val dateNow = Calendar.getInstance().time
                    dataObj.timeStamp = dateNow.time.toString()
                    trySend(NetworkResult.Success(dataObj))
                    close()
                }
            }.addOnFailureListener { exception ->
                trySend(NetworkResult.Error(message = exception.message))
                close()
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

}