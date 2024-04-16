package com.buller.mysqlite.fragments


import android.Manifest
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentPermissionBinding
import com.buller.mysqlite.hasNewMediaPermission
import com.buller.mysqlite.hasReadStoragePermission
import com.buller.mysqlite.hasWriteStoragePermission
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.PermissionFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PermissionFragment : BaseFragment() {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!
    private val permissionFragmentViewModel: PermissionFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = permissionFragmentViewModel
    private var wrapperDialog: Context? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btPermission.setOnClickListener {
            if (checkPermission()){
                findNavController().navigate(R.id.action_permissionFragment_to_listFragment)
            }else{
                requestPermission()
            }
        }
    }
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
//                    val uri =
//                        Uri.parse("package:${(requireActivity() as MainActivity).packageName}")
//                    data = uri
//                }
//                storageAccess.launch(intent)
//            } catch (e: Exception) {
//                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
//                    val uri =
//                        Uri.parse("package:${(requireActivity() as MainActivity).packageName}")
//                    data = uri
//                }
//                storageAccess.launch(intent)
//            }
            permissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES))

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { mapResults ->
            if (mapResults.values.any { it }) {
                findNavController().navigate(R.id.action_permissionFragment_to_listFragment)
            }
        }
    private val storageAccess =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11(R) or above
                if (Environment.isExternalStorageManager()) {
                    findNavController().navigate(R.id.action_permissionFragment_to_listFragment)
                } else {
                    requestPermission()
                }
            } else {
                requestPermission()
            }
        }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Environment.isExternalStorageManager()

            requireContext().hasNewMediaPermission

        } else {
            requireContext().hasWriteStoragePermission && requireContext().hasReadStoragePermission

//            val write = ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            val read = ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                //check each permission if granted or not
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read) {
                    //External Storage Permission granted
                    findNavController().navigate(R.id.action_permissionFragment_to_listFragment)
                } else {
                    //External Storage Permission denied...
                    showPermissionDialog()
                }
            }
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            root.setBackgroundColor(theme.akcColor(requireContext()))
            activity?.window?.navigationBarColor = theme.setStatusBarColor(requireContext())
        }
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
    }


    private fun showPermissionDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.permission_required)
            positiveButton(R.string.permission_grand) { dialog ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    val uri =
                        Uri.parse("package:${(requireActivity() as MainActivity).packageName}")
                    data = uri
                }
                startActivity(intent)
                dialog.dismiss()
            }
            negativeButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
