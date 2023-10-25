package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.InputStream

class GetBackupFromStorageFirebaseUseCase(
    private val firebaseRepository: FirebaseRepository
) {

    fun execute(backupDatabase: BackupDatabase): Flow<NetworkResult<InputStream>> = callbackFlow {
            trySend(NetworkResult.Loading())
            val refUrl =
                firebaseRepository.downloadDumpFromStorageUrl(backupDatabase.backupDatabase!!)
            refUrl.getStream().addOnSuccessListener { task ->
                trySend(NetworkResult.Success(task.stream))
                close()
            }.addOnFailureListener { exception ->
                trySend(NetworkResult.Error(message = exception.message))
                close()
            }
            awaitClose()
        }

}