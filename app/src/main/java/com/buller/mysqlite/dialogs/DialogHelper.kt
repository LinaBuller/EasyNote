package com.buller.mysqlite.dialogs

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.accounthelper.AccountHelper
import com.buller.mysqlite.databinding.DialogSignBinding

class DialogHelper(contextActivity: MainActivity) {
    private val context = contextActivity
    val accountHelper = AccountHelper(context)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(context)
        val binding = DialogSignBinding.inflate(context.layoutInflater)
        val view = binding.root
        builder.setView(view)
        setDialogState(index,binding)
        val dialog = builder.create()

        binding.btSignUpAndSignIn.setOnClickListener {
            setOnClickSignUpIn(index,binding,dialog)
        }
        binding.btForgetPassword.setOnClickListener {
            setOnClickResetPassword(binding,dialog)
        }
        binding.btSignGoogle.setOnClickListener {
            accountHelper.signInWithGoogle()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(binding: DialogSignBinding, dialog: AlertDialog?) {
        if (binding.edSignEmail.text.isNotEmpty()){
            context.mAuth.sendPasswordResetEmail(binding.edSignEmail.text.toString()).addOnCompleteListener{ task->
                if (task.isSuccessful){
                    Toast.makeText(context,R.string.e_mail_reset_password_was_send,Toast.LENGTH_LONG).show()
                }
            }
            dialog?.dismiss()
        }else{
            Toast.makeText(context,R.string.e_mail_reset_password_not_email,Toast.LENGTH_LONG).show()
        }
    }

    private fun setOnClickSignUpIn(index: Int, binding: DialogSignBinding, dialog: AlertDialog?) {
        dialog?.dismiss()
        if (index == ContentConstants.SIGN_UP_STATE) {
            accountHelper.signUpWithEmail(
                binding.edSignEmail.text.toString(),
                binding.edSignPassword.text.toString()
            )
        } else {
            accountHelper.signInWithEmail(
                binding.edSignEmail.toString(),
                binding.edSignPassword.text.toString()
            )
        }
    }

    private fun setDialogState(index: Int, binding: DialogSignBinding) {
        if (index == ContentConstants.SIGN_UP_STATE) {
            binding.tvTitleSingUpIn.text = context.getString(R.string.sign_up)
            binding.btSignUpAndSignIn.text = context.getString(R.string.sign_up)
            binding.btSignGoogle.visibility = View.GONE
        } else {
            binding.tvTitleSingUpIn.text = context.getString(R.string.sign_in)
            binding.btSignUpAndSignIn.text = context.getString(R.string.sign_in)
            binding.btForgetPassword.visibility = View.VISIBLE
        }
    }
}