package com.buller.mysqlite.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.buller.mysqlite.R


class DialogDeleteImage : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_delete_image, container,false)
        val subButton:Button = rootView.findViewById(R.id.submitButton)
        val canButton:Button = rootView.findViewById(R.id.cancelButton)
        subButton.setOnClickListener {
            val isDelete = true
            val fragmentManager = parentFragmentManager
            fragmentManager.setFragmentResult("reqKey", bundleOf("isDelete" to isDelete))
            dismiss()
        }

        canButton.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}