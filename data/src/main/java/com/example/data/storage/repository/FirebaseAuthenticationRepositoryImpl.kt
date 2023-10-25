package com.example.data.storage.repository

import com.easynote.domain.models.UserCredential
import com.easynote.domain.repository.AuthenticationRepository
import com.example.data.storage.firebase.FirebaseAuthDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseAuthenticationRepositoryImpl(private val authSource: FirebaseAuthDataSource) :
    AuthenticationRepository {

    override fun getUid():String?=authSource.getUid()

    override fun getCurrentUser() = authSource.getCurrentUser()

    override fun signInWithEmail(userCredential: UserCredential): Task<AuthResult> =
        authSource.signInWithEmail(userCredential)

    override fun signUpWithEmail(userCredential: UserCredential): Task<AuthResult> =
        authSource.signUpWithEmail(userCredential)

    override fun logOut() = authSource.logOut()

    override fun sendEmailVerification(user: FirebaseUser) = authSource.sendEmailVerification(user)

    override fun sendEmailRecoveryPassword(email: String) = authSource.sendRecoveryPassword(email)


    override fun signInWithGoogle(token: String?): Task<AuthResult>{
        val credential = GoogleAuthProvider.getCredential(token,null)
        return authSource.signInWithCredential(credential)
    }




}