package com.buller.mysqlite

import com.easynote.domain.models.UserCredential
import org.koin.core.component.KoinComponent

interface OnPassUserToViewModel:KoinComponent {
    fun onSignInWithEmailToViewModel(userCredential: UserCredential)
    fun onSignInWithGoogleToViewModel()
    fun onSignUpWithEmailToViewModel(userCredential: UserCredential)
    fun onSendRecoveryPassword(email:String)
}