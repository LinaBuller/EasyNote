package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SendEmailVerificationFirebaseUseCase(private val authRepository: AuthenticationRepository) {
    fun execute(user: FirebaseUser): Flow<NetworkResult<Boolean>> = callbackFlow {
        trySend(NetworkResult.Loading())
        authRepository.sendEmailVerification(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
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