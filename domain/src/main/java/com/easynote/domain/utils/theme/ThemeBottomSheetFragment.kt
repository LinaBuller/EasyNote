package com.easynote.domain.utils.theme

import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class ThemeBottomSheetFragment: BottomSheetDialogFragment() {
    override fun onResume() {
        getThemeManager()?.getCurrentLiveTheme()?.observe(this) {
            syncTheme(it)
        }

        super.onResume()
    }

    protected fun getThemeManager() : ThemeManager? {
        return ThemeManager.instance
    }

    // to sync ui with selected theme
    abstract fun syncTheme(appTheme: AppTheme)
}