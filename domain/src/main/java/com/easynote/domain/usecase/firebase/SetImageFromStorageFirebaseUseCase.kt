package com.easynote.domain.usecase.firebase

import android.net.Uri
import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SetImageFromStorageFirebaseUseCase(
    private val firebaseRepository: FirebaseRepository
) {
    fun execute(prepareImages: ByteArray,uid:String,idImage:String): Flow<NetworkResult<Uri>> = callbackFlow {
        trySend(NetworkResult.Loading())
        val ref = firebaseRepository.uploadImage(uid,idImage)
        val upTask = ref.putBytes(prepareImages)
        upTask.continueWithTask {
            ref.downloadUrl
        }.addOnCompleteListener {
            trySend(NetworkResult.Success(it.result))
            close()
        }.addOnFailureListener { exception ->
            trySend(NetworkResult.Error(message = exception.message))
            close()
        }
        awaitClose()
    }

}