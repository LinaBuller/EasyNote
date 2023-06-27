package com.buller.mysqlite.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.buller.mysqlite.databinding.DialogAddArchiveBinding
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme

class DialogIsArchive : ThemeDialogFragment() {
    private lateinit var onCloseDialogListener: OnCloseDialogListener
    private lateinit var binding: DialogAddArchiveBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()

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
        binding = DialogAddArchiveBinding.inflate(inflater, container, false).also {
            (parentFragment as? OnCloseDialogListener)?.let {
                onCloseDialogListener = it
            }
        }

        val currentNote = mNoteViewModel.selectedNote.value


        binding.apply {
            if (currentNote != null) {
                if (currentNote.isArchive) {
                    archiveItem.text = "Delete this note from the archive?"
                }else{
                   archiveItem.text = "Add this note to the archive?"
                }

            }
            submitButton.setOnClickListener {
                if (currentNote != null) {
                    onCloseDialogListener.onCloseDialog(isArchive =!currentNote.isArchive)
                }
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }
    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            archiveItem.setTextColor(theme.textColorTabUnselect(requireContext()))
            submitButton.setTextColor(theme.akcColor(requireContext()))
            submitButton.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            cancelButton.setTextColor(theme.akcColor(requireContext()))
            cancelButton.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            dialog?.window?.setBackgroundDrawableResource(theme.backgroundResDialogFragment())
        }
    }


    companion object {
        const val TAG = "PurchaseConfirmationDialogAddToArchiveNote"
    }


}