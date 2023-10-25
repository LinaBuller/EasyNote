package com.buller.mysqlite.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.buller.mysqlite.databinding.LayoutCirclerBarBinding
import com.buller.mysqlite.theme.BaseTheme
import com.buller.mysqlite.theme.ThemeDialogFragment
import com.dolatkia.animatedThemeManager.AppTheme

class ProgressDialogFragment : ThemeDialogFragment() {
    lateinit var binding: LayoutCirclerBarBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutCirclerBarBinding.inflate(inflater, container, false)
        isCancelable = false
        binding.layoutCircle.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return binding.root
    }

    companion object {
        fun showProgressBar(activity: FragmentActivity){
            try{
                val fm = activity.supportFragmentManager
                val dialogFragment = ProgressDialogFragment()
                dialogFragment.show(fm,ConstansDialog.DIALOG_PROGRESS_BAR)
            }catch (_:Exception){}

        }

        fun hideProgressBar(activity: FragmentActivity) {
            try {
                val fm = activity.supportFragmentManager
                val dialog = fm.findFragmentByTag(ConstansDialog.DIALOG_PROGRESS_BAR)
                if (dialog != null && dialog is ProgressDialogFragment) {
                    dialog.dismiss()
                }
            } catch (ignored: Exception) {}
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.progressCircular.setIndicatorColor(theme.akcColor(requireContext()))
    }
}