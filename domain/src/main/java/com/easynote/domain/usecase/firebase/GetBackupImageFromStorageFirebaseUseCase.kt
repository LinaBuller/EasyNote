package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.BackupImage
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.InputStream

class GetBackupImageFromStorageFirebaseUseCase(private val firebaseRepository: FirebaseRepository) {

    fun execute(image: BackupImage): Flow<NetworkResult<InputStream>> = callbackFlow {
        trySend(NetworkResult.Loading())
        val ref = firebaseRepository.downloadImageFromStorage(image.uriStorage!!)
        ref.getStream().addOnSuccessListener { task ->
            trySend(NetworkResult.Success(task.stream))
            close()
        }.addOnFailureListener { exception ->
            trySend(NetworkResult.Error(message = exception.message))
            close()
        }
        awaitClose()
    }
}