package com.easynote.domain.usecase.firebase

import com.easynote.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser

class GetCurrentUserUseCase (
    private val authRepository: AuthenticationRepository,
){
    fun execute(): FirebaseUser?{
        return authRepository.getCurrentUser()
    }
}