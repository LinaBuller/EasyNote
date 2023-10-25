package com.buller.mysqlite.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentSplashBinding
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment

class SplashFragment : ThemeFragment() {
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSplashBinding.inflate(
            inflater, container, false
        )
        binding.lottiAnim.playAnimation()

        val handler = Handler(Looper.myLooper()!!)

        if (checkPermission()) {
            handler.postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_listFragment)
            }, 3000)
        } else {
            handler.postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_permissionFragment)
            }, 3000)
        }
        return binding.root
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            root.setBackgroundColor(theme.akcColor(requireContext()))
            activity?.window?.navigationBarColor = theme.setStatusBarColor(requireContext())
        }
    }
}