package com.buller.mysqlite.fragments.image


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentImageBinding
import com.buller.mysqlite.dialogs.DialogDeleteImage
import com.buller.mysqlite.model.Image

class ImageFragment : Fragment() {
    private lateinit var binding: FragmentImageBinding
    private var uri: String = ""
    private val args by navArgs<ImageFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(inflater, container, false)

        val image = args.selectImage

        if (image.isDelete) {
            val action = ImageFragmentDirections.actionImageFragmentToAddFragment()
            findNavController().navigate(action)
        } else {
            //val action = ImageFragmentDirections.actionImageFragmentToAddFragment(null)
            findNavController().navigate(R.id.action_imageFragment_to_addFragment)
        }

        binding.image.setImageURI(Uri.parse(args.selectImage.uri))

        binding.imageFragment.setOnClickListener {
            findNavController().navigate(R.id.action_imageFragment_to_addFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.shareImage -> {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "image/jpeg"
                    share.putExtra(
                        Intent.EXTRA_STREAM,
                        Uri.parse(uri)
                    )
                    startActivity(Intent.createChooser(share, "Share Image"))
                    true
                }
                R.id.deleteImage -> {
                    findNavController().navigate(R.id.action_imageFragment_to_dialogDeleteImage)
//                    val dialog = DialogDeleteImage()
//                    dialog.show(childFragmentManager, DialogDeleteImage.TAG)
                    true
                }
                else -> false
            }
        }
    }

}