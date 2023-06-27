package com.easynote.domain.utils.biometric

import androidx.biometric.BiometricPrompt

interface BiometricAuthListener {
    fun onBiometricAuthenticationError(errorCode: Int, toString: String)
    fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult)
}
