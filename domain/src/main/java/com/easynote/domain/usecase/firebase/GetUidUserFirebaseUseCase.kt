package com.easynote.domain.usecase.firebase

import com.easynote.domain.repository.AuthenticationRepository

class GetUidUserFirebaseUseCase( private val authRepository: AuthenticationRepository) {
    fun execute(): String?{
        return authRepository.getUid()
    }
}