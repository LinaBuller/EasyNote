package com.buller.mysqlite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.DialogModBottomSheetChooseImageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModBtSheetChooseAddImageGalleryOrCamera(var contextActivity: EditActivity) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogModBottomSheetChooseImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_mod_bottom_sheet_choose_image, container, false)
        binding = DialogModBottomSheetChooseImageBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btImageCamera.setOnClickListener {
            Toast.makeText(contextActivity, "Camera", Toast.LENGTH_SHORT).show()
            contextActivity.setIntentCameraOrGallery(ContentConstants.CAMERA)
            dismiss()
        }
        binding.btImageGallery.setOnClickListener {
            Toast.makeText(contextActivity, "Gallery", Toast.LENGTH_SHORT).show()
            contextActivity.setIntentCameraOrGallery(ContentConstants.GALLERY)
            dismiss()
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}