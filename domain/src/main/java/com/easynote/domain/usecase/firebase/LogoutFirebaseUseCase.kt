package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.AuthenticationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LogoutFirebaseUseCase (private val authRepository: AuthenticationRepository){

    fun execute(): Flow<NetworkResult<Boolean>> = callbackFlow {
        trySend(NetworkResult.Loading())
        authRepository.logOut()
        trySend(NetworkResult.Success(true))
        close()
        awaitClose()
    }
}