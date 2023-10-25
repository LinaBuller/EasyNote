package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.BackupImage
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SetImageFromRealtimeFirebaseUseCase(private val firebaseRepository: FirebaseRepository) {

    fun execute(backupImage: BackupImage): Flow<NetworkResult<Boolean>> = callbackFlow {
        trySend(NetworkResult.Loading())
        firebaseRepository.insertImageToDatabase(backupImage).addOnCompleteListener { insertTask ->
            if (insertTask.isSuccessful) {
                trySend(NetworkResult.Success(true))
                close()
            }
        }.addOnFailureListener { exception ->
            trySend(NetworkResult.Error(message = exception.message))
            close()
        }
        awaitClose()
    }
}