package com.easynote.domain.usecase.firebase

import android.util.Log
import com.easynote.domain.models.BackupImage
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GetBackupImageUriFromRealtimeFirebaseUseCase(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(uid: String): Flow<NetworkResult<List<BackupImage>>> = callbackFlow {
        trySend(NetworkResult.Loading())
        firebaseRepository.readImageFromDatabase(uid).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("msg", "Successful read realtime database")
                val list = mutableListOf<BackupImage>()
                task.result.children.forEach { dataSnapshot ->
                    dataSnapshot.getValue(BackupImage::class.java)?.let {
                        list.add(it)
                    }
                }
                trySend(NetworkResult.Success(list))
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