package com.buller.mysqlite.fragments.developer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.constans.DevelopInfoConstants
import com.buller.mysqlite.databinding.FragmentDeveloperBinding
import com.buller.mysqlite.fragments.list.ListFragment


class DeveloperFragment : Fragment() {
    lateinit var binding: FragmentDeveloperBinding

    private val menuItemClickListener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setToolbarMenu(
            R.menu.menu_empty,
            menuItemClickListener
        )
        Log.d(ListFragment.TAG, "DeveloperFragment onResume")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeveloperBinding.inflate(inflater, container, false)
        binding.apply {
            imBtCopyEmail.setOnClickListener {
               copyTextToClipboard()
            }
            tv6.setOnClickListener {
                sendEmail(DevelopInfoConstants.DEV_EMAIL)
            }

            tv7.setOnClickListener {
                openWebPage(DevelopInfoConstants.DEV_GITHUB)
            }
        }
        return binding.root
    }

    private fun copyTextToClipboard(){
        if (context!=null){
            val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("E-mail", DevelopInfoConstants.DEV_EMAIL)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(),"copy: $DevelopInfoConstants.DEV_EMAIL",Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(email:String){
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        emailIntent.selector = selectorIntent
        requireActivity().startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun openWebPage(url:String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}