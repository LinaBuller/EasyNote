package com.buller.mysqlite.fragments.image


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentImageBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.easynote.domain.models.Image
import com.squareup.picasso.Picasso
import java.io.File

class ImageFragment : ThemeFragment() {
    private lateinit var binding: FragmentImageBinding
    private lateinit var currentImage: Image
    private var wrapperDialog: Context? = null

    companion object {
        const val TAG = "ImageFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ImageFragment onCreate")
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


        binding.apply {
            val file = File(currentImage.uri)
            Picasso.get().isLoggingEnabled = true
            Picasso.get()
                .load(file)
                .placeholder(R.drawable.ic_broken_image_glade)
                .into(this.image)
            Picasso.get().isLoggingEnabled = true


            imageFragment.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_image_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
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

                    R.id.delete_item -> {
                        showDeleteDialog()
                        return true
                    }

                    else -> return false

                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())

    }


    private fun showDeleteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_text)
            positiveButton(R.string.yes) { dialog ->
                setFragmentResult("imageFragment", bundleOf("deleteImageId" to currentImage))
                dialog.dismiss()
                findNavController().popBackStack()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }
}