package com.buller.mysqlite.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.DialogAddNewCategoryBinding
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.dolatkia.animatedThemeManager.AppTheme


class DialogAddNewCategory: ThemeDialogFragment() {
    private lateinit var onAddCategory: OnAddCategory
    private lateinit var binding:DialogAddNewCategoryBinding

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            tvTitleAddNewCategory.setTextColor(theme.textColorTabUnselect(requireContext()))
            filledTextInputLayout.boxBackgroundColor  = theme.setStatusBarColor(requireContext())
            filledTextInputLayout.boxStrokeColor = theme.akcColor(requireContext())
            etAddCategory.setTextColor(theme.textColor(requireContext()))
            btCancelButton.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btCancelButton.setTextColor(theme.akcColor(requireContext()))
            btAddCategory.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btAddCategory.setTextColor(theme.akcColor(requireContext()))
            dialog?.window?.setBackgroundDrawableResource(theme.backgroundResDialogFragment())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAddCategory) {
            onAddCategory = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddNewCategoryBinding.inflate(inflater,container,false).also {
            (parentFragment as? OnAddCategory)?.let {
                onAddCategory = it
            }
        }
        binding.apply {
            if (etAddCategory.requestFocus()){
                dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }

            btAddCategory.setOnClickListener {
                onAddCategory.onAddCategory(etAddCategory.text.toString())
                dismiss()
            }
            btCancelButton.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    interface OnAddCategory{
        fun onAddCategory(titleCategory:String)
    }

    companion object {
        const val TAG = "AddNewConfirmationDialog"
    }
}