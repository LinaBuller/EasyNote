package com.buller.mysqlite.dialogs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.databinding.DialogMoveCategoryBinding
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeDialogFragment
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogMoveCategory: ThemeDialogFragment(),DialogCategoryAdapter.OnItemClickListener {

    private lateinit var binding: DialogMoveCategoryBinding
    private lateinit var categoryAdapter: DialogCategoryAdapter
    private lateinit var mNotesViewModel: NotesViewModel

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
        mNotesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        mNotesViewModel.setSelectedCategoryFromItemList()


        categoryAdapter = DialogCategoryAdapter(this@DialogMoveCategory)
        binding.rcCategoryDialog.adapter = categoryAdapter

        mNotesViewModel.editedSelectCategoryFromDialogMoveCategory.observe(viewLifecycleOwner) {
            categoryAdapter.updateList(it)
        }

        binding.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            rcCategoryDialog.layoutManager = linearLayoutManager

            submitButton.setOnClickListener {
                mNotesViewModel.updateCategoryFromItemList()
                mNotesViewModel.clearSelectedNote()
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }

        mNotesViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }

        initThemeObserver()

        return binding.root
    }


    override fun onCheckBoxClick(category: Category, isChecked: Boolean) {
        mNotesViewModel.changeCheckboxCategory(category, isChecked)
    }

    private fun initThemeObserver() {
        mNotesViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }
}