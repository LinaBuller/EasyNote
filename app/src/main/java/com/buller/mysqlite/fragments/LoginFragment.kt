package com.buller.mysqlite.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.databinding.FragmentLoginBinding
import com.buller.mysqlite.utils.biometric.BiometricAuthListener
import com.buller.mysqlite.utils.biometric.BiometricUtil
import com.buller.mysqlite.utils.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment

class LoginFragment : ThemeFragment(), BiometricAuthListener {
    private lateinit var binding: FragmentLoginBinding
    private var isAlwaysBioAuth = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(
            inflater, container, false
        )

        binding.apply {
            isAlwaysBioAuth =
                (requireActivity() as MainActivity).sharedPref.getBoolean("IS_BIO_AUTH", false)

            if (isAlwaysBioAuth) {
                textInputLayout.visibility = View.GONE
                btOpenBioAuth.visibility = View.GONE
                showBiometricPrompt()
            } else {
                textInputLayout.visibility = View.VISIBLE
                btOpenBioAuth.visibility = View.VISIBLE
            }
            cbBiometricAuth.isChecked = isAlwaysBioAuth

            btOpenBioAuth.setOnClickListener {
                showBiometricPrompt()
            }

            cbBiometricAuth.setOnClickListener {
                val editor = (requireActivity() as MainActivity).sharedPref.edit()
                if (isAlwaysBioAuth) {
                    cbBiometricAuth.isChecked = false
                    isAlwaysBioAuth = false
                    textInputLayout.visibility = View.VISIBLE
                    btOpenBioAuth.visibility = View.VISIBLE
                } else {
                    textInputLayout.visibility = View.GONE
                    btOpenBioAuth.visibility = View.GONE
                    cbBiometricAuth.isChecked = true
                    isAlwaysBioAuth = true
                    showBiometricPrompt()
                }
                editor.apply {
                    putBoolean("IS_BIO_AUTH", isAlwaysBioAuth)
                    apply()
                }
            }
        }
        return binding.root
    }


    private fun showBiometricPrompt() {
        BiometricUtil.showBiometricPrompt(
            activity = requireActivity() as MainActivity,
            listener = this@LoginFragment,
            cryptoObject = null,
            allowDeviceCredential = true
        )
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            textInputLayout.boxStrokeColor = theme.akcColor(requireContext())
            textInputLayout.setEndIconTintList(ColorStateList.valueOf(theme.akcColor(requireContext())))
            textInputLayout.boxBackgroundColor = theme.backgroundDrawer(requireContext())
            textInputLayout.hintTextColor = ColorStateList.valueOf(theme.textColor(requireContext()))

            imLogin.setColorFilter(theme.akcColor(requireContext()))
            textInputLayout.setStartIconTintList(ColorStateList.valueOf(theme.akcColor(requireContext())))
            btOpenBioAuth.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btOpenBioAuth.outlineAmbientShadowColor = theme.akcColor(requireContext())
            btOpenBioAuth.outlineSpotShadowColor = theme.akcColor(requireContext())
            btOpenBioAuthText.setTextColor(theme.textColor(requireContext()))

            cbBiometricAuth.buttonTintList = ColorStateList.valueOf(theme.akcColor(requireContext()))
            cbBiometricAuth.setTextColor(theme.textColorTabUnselect(requireContext()))
            layoutLogin.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundColor(requireContext()))
        }
    }

    override fun onBiometricAuthenticationError(errorCode: Int, toString: String) {
        Toast.makeText(requireContext(), "You close biometric authentication", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToListFragment2())
    }


}