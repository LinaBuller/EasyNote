package com.buller.mysqlite.dialogs

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.buller.mysqlite.OnPassUserToViewModel
import com.buller.mysqlite.R

import com.easynote.domain.models.UserCredential

class DialogHelper(private val onPass: OnPassUserToViewModel) {
    fun createSignUpDialog(wrapper: Context) {

        MaterialDialog(wrapper).show {
            title(R.string.hello)
            message(R.string.hello_sign_up_message)
            val customDialog = customView(
                R.layout.dialog_auth,
                scrollable = false,
                horizontalPadding = true
            )
            val emailField = customDialog.findViewById<EditText>(R.id.et_email)
            if (emailField.requestFocus()) {
                customDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }

            val btSignInGoogle = customDialog.findViewById<ImageButton>(R.id.googleSignIn)
            btSignInGoogle.setOnClickListener {
                onPass.onSignInWithGoogleToViewModel()
                customDialog.dismiss()
            }
            val tvPasswordContains = customDialog.findViewById<TextView>(R.id.tv_password_contains)
            tvPasswordContains.visibility = View.VISIBLE
            positiveButton(R.string.sign_up_action) { dialog ->
                val email = dialog.getCustomView().findViewById<EditText>(R.id.et_email)
                val password = dialog.getCustomView().findViewById<EditText>(R.id.et_password)
                val newUserCredential =
                    UserCredential(email.text.toString(), password.text.toString())
                onPass.onSignUpWithEmailToViewModel(newUserCredential)
                dialog.dismiss()
            }

            negativeButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
        }

    }


    fun createSignInDialog(wrapper: Context) {
        MaterialDialog(wrapper).show {
            title(R.string.hello)
            message(R.string.hello_sign_in_message)
            val customDialog = customView(
                R.layout.dialog_auth,
                scrollable = false,
                horizontalPadding = true
            )
            val emailField = customDialog.findViewById<EditText>(R.id.et_email)
            if (emailField.requestFocus()) {
                customDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
            val btSignInGoogle = customDialog.findViewById<ImageButton>(R.id.googleSignIn)
            btSignInGoogle.setOnClickListener {
                onPass.onSignInWithGoogleToViewModel()
                customDialog.dismiss()
            }

            positiveButton(R.string.sign_in_action) { dialog ->
                val email = dialog.getCustomView().findViewById<EditText>(R.id.et_email)
                val password =
                    dialog.getCustomView().findViewById<EditText>(R.id.et_password)
                val oldUserCredential =
                    UserCredential(email.text.toString(), password.text.toString())
                onPass.onSignInWithEmailToViewModel(oldUserCredential)
                dialog.dismiss()
            }

            neutralButton(R.string.forget_password) { dialog ->
                val email = dialog.getCustomView().findViewById<EditText>(R.id.et_email)
                if (email.text.toString().isNotBlank()) {
                    onPass.onSendRecoveryPassword(email.text.toString())
                    dialog.dismiss()
                } else {
                    createPasswordsRecoveryDialog(wrapper)
                    dialog.dismiss()
                }
            }

            negativeButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun createPasswordsRecoveryDialog(wrapper: Context) {
        MaterialDialog(wrapper).show {
            title(R.string.forget_password)
            message(R.string.forget_password_message)
            val customDialog = customView(
                R.layout.dialog_forget_password,
                scrollable = false,
                horizontalPadding = true
            )
            val emailField = customDialog.findViewById<EditText>(R.id.et_email_forget_password)
            if (emailField.requestFocus()) {
                customDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }

            positiveButton(R.string.ok) { dialog ->
                val email = dialog.findViewById<EditText>(R.id.et_email_forget_password)
                onPass.onSendRecoveryPassword(email.text.toString())
            }
            negativeButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
        }
    }
}