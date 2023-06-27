package com.buller.mysqlite.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.databinding.DialogMoveCategoryBinding
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme

class DialogMoveCategory: ThemeDialogFragment(),DialogCategoryAdapter.OnItemClickListener {

    private lateinit var binding: DialogMoveCategoryBinding
    private lateinit var categoryAdapter: DialogCategoryAdapter
    private val mNoteViewModel: NotesViewModel by activityViewModels()

    companion object {
        const val TAG = "PurchaseConfirmationDialogMoveCategory"
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            submitButton.setTextColor(theme.akcColor(requireContext()))
            submitButton.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            cancelButton.setTextColor(theme.akcColor(requireContext()))
            cancelButton.backgroundTintList =
                ColorStateList.valueOf(theme.backgroundDrawer(requireContext()))
            dialog?.window?.setBackgroundDrawableResource(theme.backgroundResDialogFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMoveCategoryBinding.inflate(inflater, container, false)

        mNoteViewModel.setSelectedCategoryFromItemList()


        categoryAdapter = DialogCategoryAdapter(this@DialogMoveCategory)
        binding.rcCategoryDialog.adapter = categoryAdapter

        mNoteViewModel.editedSelectCategoryFromDialogMoveCategory.observe(viewLifecycleOwner) {
            categoryAdapter.updateList(it)
        }

        binding.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            rcCategoryDialog.layoutManager = linearLayoutManager

            submitButton.setOnClickListener {
                mNoteViewModel.updateCategoryFromItemList()
                mNoteViewModel.clearSelectedNote()
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }

        mNoteViewModel.categories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }

        initThemeObserver()

        return binding.root
    }


    override fun onCheckBoxClick(category: com.easynote.domain.models.Category, isChecked: Boolean) {
        mNoteViewModel.changeCheckboxCategory(category, isChecked)
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }
}