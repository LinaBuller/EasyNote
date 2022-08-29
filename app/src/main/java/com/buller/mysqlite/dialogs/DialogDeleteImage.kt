package com.buller.mysqlite.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.buller.mysqlite.databinding.DialogDeleteImageBinding

class DialogDeleteImage: DialogFragment() {
    private lateinit var onCloseDialogListener: OnCloseDialogListener
    private lateinit var binding:DialogDeleteImageBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCloseDialogListener) {
            onCloseDialogListener = context
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteImageBinding.inflate(inflater,container,false).also {
            (parentFragment as? OnCloseDialogListener)?.let {
                onCloseDialogListener = it
            }
        }
        binding.apply {
            submitButton.setOnClickListener {
                onCloseDialogListener.onCloseDialog(true)
                dismiss()
            }
            cancelButton.setOnClickListener {
                onCloseDialogListener.onCloseDialog(false)
                dismiss()
            }

        }
        return binding.root
    }

    interface OnCloseDialogListener {
        fun onCloseDialog(isDelete:Boolean)
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}