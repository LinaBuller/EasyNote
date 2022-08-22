package com.buller.mysqlite.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.image.ImageFragmentArgs
import com.buller.mysqlite.model.Image


class DialogDeleteImage : DialogFragment() {
    private val args by navArgs<DialogDeleteImageArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_delete_image, container, false)
        val subButton: Button = rootView.findViewById(R.id.submitButton)
        val canButton: Button = rootView.findViewById(R.id.cancelButton)
        subButton.setOnClickListener {
            val image: Image = args.selectedImage
            val action = DialogDeleteImageDirections.actionDialogDeleteImageToImageFragment(image.copy(isDelete = true))
            findNavController().navigate(action)
        }

        canButton.setOnClickListener {
            findNavController().navigate(R.id.action_dialogDeleteImage_to_imageFragment)
        }
        return rootView
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}