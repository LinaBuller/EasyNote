package com.example.data.storage.firebase

import com.easynote.domain.models.UserCredential
import com.example.data.storage.DataSource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthDataSource(private val auth: FirebaseAuth) : DataSource {

    fun getCurrentUser() = auth.currentUser

    fun signUpWithEmail(credential: UserCredential) =
        auth.createUserWithEmailAndPassword(credential.email, credential.password)

    fun signInWithEmail(credential: UserCredential) =
        auth.signInWithEmailAndPassword(credential.email, credential.password)

    fun sendEmailVerification(user: FirebaseUser) = user.sendEmailVerification()

    fun logOut() = auth.signOut()

    fun sendRecoveryPassword(email: String) = auth.sendPasswordResetEmail(email)

    fun signInWithCredential(credential: AuthCredential) = auth.signInWithCredential(credential)

    fun getUid(): String? = auth.uid
}