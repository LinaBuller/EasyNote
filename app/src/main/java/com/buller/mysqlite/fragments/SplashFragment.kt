package com.buller.mysqlite.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentLoginBinding
import com.buller.mysqlite.databinding.FragmentSplashBinding
import com.buller.mysqlite.utils.theme.BaseTheme
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

        handler.postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToListFragment())
        }, 3000)
        return binding.root
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            root.setBackgroundColor(theme.akcColor(requireContext()))
        }
    }
}