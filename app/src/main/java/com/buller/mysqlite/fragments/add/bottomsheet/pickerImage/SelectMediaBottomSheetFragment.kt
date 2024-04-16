package com.buller.mysqlite.fragments.add.bottomsheet.pickerImage

import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.buller.mysqlite.BuildConfig
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentBottomSheetListDialogBinding
import com.buller.mysqlite.hasCameraPermission
import com.buller.mysqlite.hasWriteStoragePermission
import com.buller.mysqlite.isPermissionGranted
import com.buller.mysqlite.requestCameraPermission
import com.buller.mysqlite.requestWriteStoragePermission
import com.buller.mysqlite.theme.BaseTheme
import com.buller.mysqlite.theme.ThemeBottomSheetFragment
import com.dolatkia.animatedThemeManager.AppTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar


class SelectMediaBottomSheetFragment(private val onImageSelectListener: OnImageSelectListener) :
    ThemeBottomSheetFragment() {
    private var _binding: FragmentBottomSheetListDialogBinding? = null
    private val binding get() = _binding!!
    private var currentPhotoUri: Uri? = null
    private var providerAuthority = ""


    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            ivCamera.imageTintList = ColorStateList.valueOf(theme.akcColor(requireContext()))
            ivGallery.imageTintList = ColorStateList.valueOf(theme.akcColor(requireContext()))
            btCamera.setTextColor(theme.akcColor(requireContext()))
            btGallery.setTextColor(theme.akcColor(requireContext()))
            selectImageLayout.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
        }
    }


    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, state: Bundle?): View {
        _binding = FragmentBottomSheetListDialogBinding.inflate(inflater, group, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btCamera.setOnClickListener {
                launchCamera()
            }
            ivCamera.setOnClickListener {
                launchCamera()
            }
            btGallery.setOnClickListener {
                requestPermission()
            }
            ivGallery.setOnClickListener {
                requestPermission()
            }
        }
    }


    private fun launchCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!requireContext().hasWriteStoragePermission) {
                requestWriteStoragePermission(PermissionConst.REQUEST_PERMISSION_WRITE_STORAGE)
                return
            }
        } else {
            if (!requireContext().hasCameraPermission) {
                requestCameraPermission(PermissionConst.REQUEST_PERMISSION_CAMERA)
                return
            }
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) == null) return
        val photoUri = try {
            getPhotoUri()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) Log.w(TAG, "could not prepare image file", e)
            return
        }
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoUri = photoUri

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        requireContext().packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).forEach { info ->
            val packageName = info.activityInfo.packageName
            requireContext().grantUriPermission(
                packageName,
                photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivityForResult(intent, PermissionConst.REQUEST_PHOTO)
    }

    private fun getPhotoUri(): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = requireContext().contentResolver
            val contentVals = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, getImageFileName() + ".jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")

                //put images in DCIM folder
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
            }
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentVals)
        } else {
            val imageFileName = getImageFileName()
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            storageDir.mkdirs()
            val image = File.createTempFile(imageFileName + "_", ".jpg", storageDir)

            //no need to create empty file; camera app will create it on success
            val success = image.delete()
            if (!success && BuildConfig.DEBUG) {
                Log.d(TAG, "Failed to delete temp file: $image")
            }
            FileProvider.getUriForFile(requireContext(), providerAuthority, image)
        }

    @SuppressLint("SimpleDateFormat")
    private fun getImageFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().time)
        return "IMG_$timeStamp"
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionConst.REQUEST_PERMISSION_READ_STORAGE ->
                if (grantResults.isPermissionGranted)
                    launchCamera()
                else
                    Toast.makeText(
                        requireContext(),
                        R.string.toastImagePickerNoWritePermission,
                        Toast.LENGTH_LONG
                    ).show()

            PermissionConst.REQUEST_PERMISSION_WRITE_STORAGE ->
                if (grantResults.isPermissionGranted)
                    launchCamera()
                else
                    Toast.makeText(
                        requireContext(),
                        R.string.toastImagePickerNoWritePermission,
                        Toast.LENGTH_LONG
                    ).show()

            PermissionConst.REQUEST_GALLERY ->
                if (grantResults.isPermissionGranted) {
                    launchCamera()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Android 11 not permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        when (requestCode) {
            PermissionConst.REQUEST_PHOTO -> {
                notifyGallery()
                currentPhotoUri?.let { uri ->
                    onImageSelectListener.onImagesSelected(listOf(uri))
                }
                dismissAllowingStateLoss()
                return
            }

            PermissionConst.REQUEST_GALLERY -> {
                data?.data?.let { uri ->
                    onImageSelectListener.onImagesSelected(listOf(uri))
                }
                dismissAllowingStateLoss()
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun notifyGallery() {
        context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = currentPhotoUri
        })
    }

    private fun launchGallery() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PermissionConst.REQUEST_GALLERY)
        } else {
            pickMultipleMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private var pickMultipleMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        if (uris.isNotEmpty()) {
            Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
            onImageSelectListener?.onImagesSelected(uris)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PermissionConst.STORAGE_PERMISSION_CODE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {

            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    val uri =
                        Uri.parse("package:${(requireActivity() as MainActivity).packageName}")
                    data = uri
                }
                storageAccess.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                    val uri =
                        Uri.parse("package:${(requireActivity() as MainActivity).packageName}")
                    data = uri
                }
                storageAccess.launch(intent)
            }


        } else {
            permissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES))
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { mapResults ->
            if (mapResults.values.any { it }) {
                launchGallery()
            }
        }


    private val storageAccess =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                if (it.resultCode == RESULT_OK) {
                    launchGallery()
                } else {
                    Toast.makeText(requireContext(), "No permissions", Toast.LENGTH_LONG).show()
                }

            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (Environment.isExternalStorageManager()) {
                    launchGallery()
                } else {
                    Toast.makeText(requireContext(), "No permissions", Toast.LENGTH_LONG).show()
                }
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BottomSheetFragment"
    }
}