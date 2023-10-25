package com.easynote.domain.repository

import com.easynote.domain.models.UserCredential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthenticationRepository {
     fun getCurrentUser():FirebaseUser?
     fun signInWithEmail(userCredential: UserCredential): Task<AuthResult>
     fun signUpWithEmail(userCredential: UserCredential): Task<AuthResult>
     fun sendEmailVerification(user: FirebaseUser): Task<Void>
     fun sendEmailRecoveryPassword(email: String): Task<Void>
     fun logOut()
     fun signInWithGoogle(token: String?): Task<AuthResult>
     fun getUid():String?
}