package com.buller.mysqlite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.FragmentImageBinding
import com.buller.mysqlite.dialogs.DialogDeleteImage

class ImageFragment : Fragment() {
    private var _binding:FragmentImageBinding? = null
    private val binding get() = _binding!!
    private var uri: String = ""
    val KEY = "string_uri"
    private var mDataPasser: OnDataDeleteImagePass? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =FragmentImageBinding.inflate(inflater, container, false)

        uri = requireArguments().getString(KEY).toString()
        val imageView = binding.image
        imageView.setImageURI(Uri.parse(uri))
        val fragment = binding.image
        val imageFragment = parentFragmentManager.findFragmentByTag(ContentConstants.FRAGMENT_IMAGE)
        fragment.setOnClickListener {
            if (imageFragment != null) {
                if (imageFragment.isVisible) {
                    parentFragmentManager.beginTransaction().hide(imageFragment).commit()
                }
            }
        }

        val fragmentManager = childFragmentManager
        fragmentManager.setFragmentResultListener("reqKey", viewLifecycleOwner) { _, bundle ->
            val resultDelete = bundle.getBoolean("isDelete")
            //передать удаление и ури в активити для удаления из базы данных
            //Toast.makeText(activity, "woow - $resultDelete", Toast.LENGTH_SHORT).show()
            mDataPasser?.onDataDeleteImagePass(resultDelete,uri)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        return binding.root
    }

    override fun onAttach(a: Context) {
        super.onAttach(a)
        mDataPasser = a as OnDataDeleteImagePass?
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
                    val dialog = DialogDeleteImage()
                    dialog.show(childFragmentManager, DialogDeleteImage.TAG)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}