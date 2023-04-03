package com.buller.mysqlite.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.buller.mysqlite.databinding.DialogDeleteBinding
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.dolatkia.animatedThemeManager.AppTheme

class DialogDeleteNote: ThemeDialogFragment()  {
    private lateinit var onCloseDialogListener: OnCloseDialogListener
    private lateinit var binding: DialogDeleteBinding

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            deleteItem.setTextColor(theme.textColorTabUnselect(requireContext()))
            submitButton.setTextColor(theme.akcColor(requireContext()))
            submitButton.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            cancelButton.setTextColor(theme.akcColor(requireContext()))
            cancelButton.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            dialog?.window?.setBackgroundDrawableResource(theme.backgroundResDialogFragment())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCloseDialogListener) {
            onCloseDialogListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteBinding.inflate(inflater,container,false).also {
            (parentFragment as? OnCloseDialogListener)?.let {
                onCloseDialogListener = it
            }
        }

        binding.apply {
            submitButton.setOnClickListener {
                onCloseDialogListener.onCloseDialog(isDelete = true)
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }
    companion object {
        const val TAG = "PurchaseConfirmationDialogDeleteNote"
    }
}