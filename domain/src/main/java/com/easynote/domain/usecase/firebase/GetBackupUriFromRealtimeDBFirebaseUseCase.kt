package com.easynote.domain.usecase.firebase

import android.util.Log
import com.easynote.domain.models.BackupDatabase
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GetBackupUriFromRealtimeDBFirebaseUseCase(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(uid: String): Flow<NetworkResult<BackupDatabase>> = callbackFlow {
        trySend(NetworkResult.Loading())
        firebaseRepository.readDumpFromDatabase(uid)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dump = task.result.getValue(BackupDatabase::class.java)
                    if (dump != null) {
                        trySend(NetworkResult.Success(dump))
                        Log.d("msg", "Successful read realtime database")
                    } else {
                        trySend(NetworkResult.Error(message = "dump is null"))
                    }
                    close()
                } else {
                    Log.d("msg", "Unsuccessful read realtime database")
                }
            }.addOnFailureListener { exception ->
                trySend(NetworkResult.Error(message = exception.message))
                close()
            }
        awaitClose()
    }
}