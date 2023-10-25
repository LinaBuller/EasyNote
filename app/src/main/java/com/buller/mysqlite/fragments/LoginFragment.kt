package com.buller.mysqlite.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.navigation.fragment.findNavController
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentLoginBinding
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.utils.biometric.BiometricAuthListener
import com.easynote.domain.utils.biometric.BiometricUtil
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.LoginFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment(), BiometricAuthListener {
    private lateinit var binding: FragmentLoginBinding
    private val mLoginFragmentViewModel: LoginFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = mLoginFragmentViewModel
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(
            inflater, container, false
        )

        binding.apply {
            mLoginFragmentViewModel.getIsBioAuthSharedPref()
            val savedState = mLoginFragmentViewModel.isBioAuth
            if (savedState) {
                textInputLayout.visibility = View.GONE
                btOpenBioAuth.visibility = View.GONE
                showBiometricPrompt()
            } else {
                textInputLayout.visibility = View.VISIBLE
                btOpenBioAuth.visibility = View.VISIBLE
            }
            cbBiometricAuth.isChecked = savedState

            btOpenBioAuth.setOnClickListener {
                showBiometricPrompt()
            }

            cbBiometricAuth.setOnClickListener {
                val saveState: Boolean
                if (mLoginFragmentViewModel.isBioAuth) {
                    cbBiometricAuth.isChecked = false
                    saveState = false
                    textInputLayout.visibility = View.VISIBLE
                    btOpenBioAuth.visibility = View.VISIBLE
                } else {
                    textInputLayout.visibility = View.GONE
                    btOpenBioAuth.visibility = View.GONE
                    cbBiometricAuth.isChecked = true
                    saveState = true
                    showBiometricPrompt()
                }
                mLoginFragmentViewModel.setIsBioAuthSharedPref(saveState)
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
            textInputLayout.hintTextColor =
                ColorStateList.valueOf(theme.textColor(requireContext()))

            imLogin.setColorFilter(theme.akcColor(requireContext()))
            textInputLayout.setStartIconTintList(
                ColorStateList.valueOf(
                    theme.akcColor(
                        requireContext()
                    )
                )
            )
            btOpenBioAuth.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btOpenBioAuth.outlineAmbientShadowColor = theme.akcColor(requireContext())
            btOpenBioAuth.outlineSpotShadowColor = theme.akcColor(requireContext())
            btOpenBioAuthText.setTextColor(theme.textColor(requireContext()))

            cbBiometricAuth.buttonTintList =
                ColorStateList.valueOf(theme.akcColor(requireContext()))
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
        findNavController().navigate( R.id.action_splashFragment_to_listFragment)
    }


}