package com.buller.mysqlite.fragments.add.bottomsheet

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.DialogModBottomSheetChooseImageBinding
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.utils.CreateNewImageFile
import com.buller.mysqlite.utils.ImagePicker
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModBtSheetChooseAddImageGalleryOrCamera() : BottomSheetDialogFragment() {
    private lateinit var binding: DialogModBottomSheetChooseImageBinding
    lateinit var currentPhotoUri: Uri
    var editLauncherImage: ActivityResultLauncher<Intent>? = null
    private lateinit var mNoteViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_mod_bottom_sheet_choose_image, container, false)
        binding = DialogModBottomSheetChooseImageBinding.bind(view)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btImageCamera.setOnClickListener {
            Toast.makeText(requireContext(), "Camera", Toast.LENGTH_SHORT).show()
            //ImagePicker.getImage(requireActivity() as MainActivity)
            //setIntentCameraOrGallery(ContentConstants.CAMERA)
            //dismiss()
        }
        binding.btImageGallery.setOnClickListener {
            Toast.makeText(requireContext(), "Gallery", Toast.LENGTH_SHORT).show()
            //ImagePicker.getImage(requireActivity() as MainActivity)
            //setIntentCameraOrGallery(ContentConstants.GALLERY)
            //dismiss()
        }
    }

//    private fun setIntentCameraOrGallery(type: Int) {
//        if (type == ContentConstants.CAMERA) {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            val photoFile = CreateNewImageFile(requireContext()).createImageFile()
//            val photoURI: Uri = FileProvider.getUriForFile(
//                requireContext(),
//                "com.buller.mysqlite.fileprovider",
//                photoFile
//            )
//            currentPhotoUri = photoURI
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//            startActivity(intent)
//            editLauncherImage?.launch(intent)
//            dismiss()
//        } else {
//            val intent = Intent().setType("image/*").setAction(Intent.ACTION_OPEN_DOCUMENT)
//            editLauncherImage?.launch(intent)
//        }
//    }



//    private fun sendArgsToAddFragment(uri: Uri) {
//        val image = Image(uri = uri.toString())
//        val imageList = mNoteViewModel.editedImages.value
//        val mutList = mutableListOf<Image>()
//        if (imageList != null) {
//            mutList.addAll(imageList)
//            mutList.add(image)
//        }
//        mNoteViewModel.selectEditedImages(mutList)
//        findNavController().navigate(R.id.action_modBtSheetChooseAddImageGalleryOrCamera_to_addFragment)
//
//    }

//    private fun initResultLauncher() {
//        editLauncherImage =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == RESULT_OK) {
//                    //rcViewImages.visibility = View.VISIBLE
//                    val uri: Uri = if (it.data?.data != null) {
//                        requireActivity().contentResolver.takePersistableUriPermission(
//                            it.data!!.data!!,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                        )
//                        it.data!!.data!!
//                    } else {
//                        currentPhotoUri
//                    }
//                    sendArgsToAddFragment(uri)
//                    //tempArrayImageUri.add(ImageModel(uri.toString()))
//                    //imageAdapter.notifyItemInserted(tempArrayImageUri.size - 1)
//                }
//            }
//    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}