package com.easynote.domain.utils.theme

import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeManager

abstract class ThemeDialogFragment : DialogFragment() {

    override fun onResume() {
        getThemeManager()?.getCurrentLiveTheme()?.observe(this) {
            syncTheme(it)
        }
        super.onResume()
    }
    protected fun getThemeManager() : ThemeManager? {
        return ThemeManager.instance
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels*0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    abstract fun syncTheme(appTheme: AppTheme)
}