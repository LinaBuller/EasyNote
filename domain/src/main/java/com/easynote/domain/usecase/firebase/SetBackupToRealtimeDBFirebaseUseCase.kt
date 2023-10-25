package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SetBackupToRealtimeDBFirebaseUseCase(private val firebaseRepository: FirebaseRepository) {
    fun execute(database: BackupDatabase): Flow<NetworkResult<Boolean>> = callbackFlow {
        trySend(NetworkResult.Loading())
        firebaseRepository.insertDumpToDatabase(database)
            .addOnCompleteListener { insertTask ->
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