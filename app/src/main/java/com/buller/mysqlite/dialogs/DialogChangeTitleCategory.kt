package com.buller.mysqlite.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.buller.mysqlite.databinding.DialogChangeTitleCategoryBinding
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme

class DialogChangeTitleCategory: ThemeDialogFragment(){
    private lateinit var binding:DialogChangeTitleCategoryBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()


    companion object{
        const val TAG = "DialogChangeTitleCategory"
    }
    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            tvTitleAddNewCategory.setTextColor(theme.textColorTabUnselect(requireContext()))
            filledTextInputLayout.boxBackgroundColor  = theme.setStatusBarColor(requireContext())
            filledTextInputLayout.boxStrokeColor = theme.akcColor(requireContext())
            etEditCategory.setTextColor(theme.textColor(requireContext()))
            btCancelButton.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btCancelButton.setTextColor(theme.akcColor(requireContext()))
            btEditCategory.backgroundTintList = ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            btEditCategory.setTextColor(theme.akcColor(requireContext()))
            dialog?.window?.setBackgroundDrawableResource(theme.backgroundResDialogFragment())
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChangeTitleCategoryBinding.inflate(inflater,container,false)

        binding.apply {
            val currentCategoryItem = mNoteViewModel.selectedCategory.value
            if (currentCategoryItem != null) {
                etEditCategory.setText(currentCategoryItem.titleCategory)
            }
            if (etEditCategory.requestFocus()){
                dialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }

            btEditCategory.setOnClickListener {
                if (currentCategoryItem != null) {
                    if (!currentCategoryItem.titleCategory.equals(etEditCategory)){
                        changeTitleCategory(currentCategoryItem,etEditCategory.text.toString())
                    }
                }
                dismiss()
            }
            btCancelButton.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }
    private fun changeTitleCategory(currentCategoryItem: Category, titleCategory: String){
        if (titleCategory != "") {
            mNoteViewModel.updateCategory(currentCategoryItem.copy(titleCategory = titleCategory))
            Toast.makeText(
                requireContext(),
                "You change $titleCategory",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), "We need more letters", Toast.LENGTH_SHORT)
                .show()
        }
    }


}