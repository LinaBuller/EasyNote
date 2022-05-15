package com.buller.mysqlite.accounthelper

import android.util.Log
import android.widget.Toast
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.constans.FirebaseAuthConstans
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccountHelper(contextActivity: MainActivity) {
    private val context = contextActivity
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            context.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        context.uiUpdate(task.result?.user)
                    } else {
                        // Toast.makeText(context, R.string.sign_up_error, Toast.LENGTH_SHORT).show()
                        Log.d("MyLog", "${task.exception}")
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            if (exception.errorCode == FirebaseAuthConstans.ERROR_EMAIL_ALREADY_IN_USE) {
//                                Toast.makeText(
//                                    context,
//                                    FirebaseAuthConstans.ERROR_EMAIL_ALREADY_IN_USE,
//                                    Toast.LENGTH_SHORT
//                                ).show()
                                linkEmailToGoogleAccount(email, password)
                            }
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == FirebaseAuthConstans.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    context,
                                    FirebaseAuthConstans.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception = task.exception as FirebaseAuthWeakPasswordException
                            if (exception.errorCode == FirebaseAuthConstans.ERROR_WEAK_PASSWORD) {
                                Toast.makeText(
                                    context,
                                    FirebaseAuthConstans.ERROR_WRONG_PASSWORD,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            context.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        context.uiUpdate(task.result?.user)
                    } else {
                        Toast.makeText(context, R.string.sign_in_error, Toast.LENGTH_SHORT).show()
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == FirebaseAuthConstans.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    context,
                                    FirebaseAuthConstans.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (exception.errorCode == FirebaseAuthConstans.ERROR_WRONG_PASSWORD) {
                                Toast.makeText(
                                    context,
                                    FirebaseAuthConstans.ERROR_WRONG_PASSWORD,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (task.exception is FirebaseAuthInvalidUserException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidUserException
                            if (exception.errorCode == FirebaseAuthConstans.ERROR_USER_NOT_FOUND) {
                                Toast.makeText(
                                    context,
                                    FirebaseAuthConstans.ERROR_USER_NOT_FOUND,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        context.startActivityForResult(intent, GoogleAccountConst.GOOGLE_SIGN_REQUEST_CODE)
    }

    fun signOutGoogleAccount() {
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        context.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, context.getString(R.string.sign_in_done), Toast.LENGTH_LONG)
                    .show()
                context.uiUpdate(task.result?.user)
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, R.string.sign_up_verification, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, R.string.sign_up_error_send_email, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun linkEmailToGoogleAccount(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (context.mAuth.currentUser != null) {
            context.mAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            R.string.email_link_google_account,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(context, R.string.email_already_used, Toast.LENGTH_SHORT)
                .show()
        }

    }

}