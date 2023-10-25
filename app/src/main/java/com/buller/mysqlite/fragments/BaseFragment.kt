package com.buller.mysqlite.fragments

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.easynote.domain.viewmodels.BaseViewModel

abstract class BaseFragment:ThemeFragment() {
    abstract val mBaseViewModel: BaseViewModel
    protected open fun initEventObservers() {
        mBaseViewModel.message.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                requireContext().toast(message)
            }
        }
    }

    fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
        this?.let { Toast.makeText(it, textId, duration).show() }

}