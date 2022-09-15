package com.buller.mysqlite.fragments.image


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentImageBinding
import com.buller.mysqlite.dialogs.DialogDeleteImage
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.viewmodel.NotesViewModel

class ImageFragment : Fragment(), DialogDeleteImage.OnCloseDialogListener {
    private lateinit var binding: FragmentImageBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var currentImage: Image

    companion object {
        const val TAG = "ImageFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ImageFragment onCreate")
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        if (arguments != null) {
            currentImage = requireArguments().getParcelable(FragmentConstants.IMAGE_TO_VIEW)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        Log.d(TAG, "ImageFragment onCreateView")

        binding.image.setImageURI(Uri.parse(currentImage.uri))

        binding.imageFragment.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    private val menuItemClickListener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (item != null) {
                when (item.itemId) {
                    R.id.shareImage -> {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "image/jpeg"
                        share.putExtra(
                            Intent.EXTRA_STREAM,
                            Uri.parse(currentImage.uri)
                        )
                        startActivity(Intent.createChooser(share, "Share Image"))
                        return true
                    }
                    R.id.deleteImage -> {
                        val dialog = DialogDeleteImage()
                        dialog.show(childFragmentManager, DialogDeleteImage.TAG)
                        return true
                    }
                    else -> return false
                }
            }
            return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setToolbarMenu(
            R.menu.menu_toolbar_image_fragment,
            menuItemClickListener
        )
    }


    override fun onCloseDialog(isDelete: Boolean) {
        if (isDelete) {
            mNoteViewModel.deleteImage(currentImage)
        }
        findNavController().popBackStack()
    }
}