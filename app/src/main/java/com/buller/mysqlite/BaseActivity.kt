package com.buller.mysqlite

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.buller.mysqlite.dialogs.ProgressDialogFragment
import com.dolatkia.animatedThemeManager.ThemeActivity
import com.easynote.domain.viewmodels.BaseViewModel

abstract class BaseActivity : ThemeActivity() {

    abstract val mBaseViewModel: BaseViewModel

    protected open fun initObservers() {
        mBaseViewModel.message.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                toast(message)
            }
        }

        mBaseViewModel.progress.observe(this) { event ->
            event.getContentIfNotHandled().let { progress ->
                when (progress) {
                    true -> ProgressDialogFragment.showProgressBar(this)
                    false -> ProgressDialogFragment.hideProgressBar(this)
                    null -> {}
                }
            }
        }
    }


    fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
        this?.let { Toast.makeText(it, textId, duration).show() }

}