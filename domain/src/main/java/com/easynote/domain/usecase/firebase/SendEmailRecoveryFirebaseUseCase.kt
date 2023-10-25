package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.AuthenticationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SendEmailRecoveryFirebaseUseCase(private val authRepository: AuthenticationRepository) {
    fun execute(email: String): Flow<NetworkResult<Boolean>> = callbackFlow {
        trySend(NetworkResult.Loading())
        authRepository.sendEmailRecoveryPassword(email).addOnCompleteListener { task ->
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