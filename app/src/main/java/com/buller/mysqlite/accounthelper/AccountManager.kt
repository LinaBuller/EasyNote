package com.buller.mysqlite.accounthelper

import android.content.Context
import com.buller.mysqlite.R
import com.buller.mysqlite.constans.FirebaseAuthConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class AccountManager (val context: Context){

    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getPersonInfo(): Map<String, String>? {
        val acct = GoogleSignIn.getLastSignedInAccount(context)
        if (acct != null) {
            val personName = acct.displayName
            val personEmail = acct.email
            val personPhoto = acct.photoUrl

            return hashMapOf(
                Pair(FirebaseAuthConstants.NAME, personName!!),
                Pair(FirebaseAuthConstants.EMAIL, personEmail!!),
                Pair(FirebaseAuthConstants.PHOTO, personPhoto.toString())
            )
        }
        return null
    }
}