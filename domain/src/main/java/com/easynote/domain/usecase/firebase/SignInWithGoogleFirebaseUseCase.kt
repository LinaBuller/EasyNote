package com.easynote.domain.usecase.firebase

import com.easynote.domain.models.NetworkResult
import com.easynote.domain.repository.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SignInWithGoogleFirebaseUseCase(private val authRepository: AuthenticationRepository) {

    fun execute(task: Task<GoogleSignInAccount>): Flow<NetworkResult<FirebaseUser>> = callbackFlow {
        trySend(NetworkResult.Loading())
        task.addOnCompleteListener { taskGoogle ->
            if (taskGoogle.isSuccessful) {
                authRepository.signInWithGoogle(task.result.idToken)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            trySend(NetworkResult.Success(task.result.user!!))
                            close()
                        }
                    }
            }
        }.addOnFailureListener { exception ->
            trySend(NetworkResult.Error(message = exception.message))
            close()
        }
        awaitClose()
    }
}