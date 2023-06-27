package com.buller.mysqlite.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.buller.mysqlite.databinding.DialogAddNewCategoryBinding
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme


class DialogAddNewCategory: ThemeDialogFragment() {

    private lateinit var binding:DialogAddNewCategoryBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddNewCategoryBinding.inflate(inflater,container,false)
        binding.apply {
            if (etAddCategory.requestFocus()){
                dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }

            btAddCategory.setOnClickListener {
                addNewCategoryToViewModel(etAddCategory.text.toString())
                dismiss()
            }
            btCancelButton.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    private fun addNewCategoryToViewModel(titleCategory: String){
        if (titleCategory != "") {
            mNoteViewModel.setCategory(com.easynote.domain.models.Category(titleCategory = titleCategory))
            Toast.makeText(
                requireContext(),
                "You add $titleCategory in categories",
                Toast.LENGTH_SHORT
            ).show()
            //добавить в текущую заметку тоже
        } else {
            Toast.makeText(requireContext(), "We need more letters", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val TAG = "AddNewConfirmationDialog"
    }
}