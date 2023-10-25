package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.NetworkResult
import com.easynote.domain.models.UserCredential
import com.easynote.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SignUpWithEmailFirebaseUseCase(private val authRepository: AuthenticationRepository) {
    fun execute(credential: UserCredential): Flow<NetworkResult<FirebaseUser>> = callbackFlow {
        trySend(NetworkResult.Loading())
        authRepository.signUpWithEmail(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(NetworkResult.Success(task.result.user!!))
                close()
            }
        }.addOnFailureListener { exception ->
            trySend(NetworkResult.Error(message = exception.message))
            close()
        }
        awaitClose()
    }
}