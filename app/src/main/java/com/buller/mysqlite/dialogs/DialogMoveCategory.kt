package com.buller.mysqlite.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.databinding.DialogMoveCategoryBinding
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.models.Category
import com.buller.mysqlite.theme.BaseTheme
import com.buller.mysqlite.theme.ThemeDialogFragment

class DialogMoveCategory(
    private val existCategory: List<Category>?,
    private val currentCategory: List<Category>?,
    private val listener: DialogCategoryAdapter.OnItemClickListener
) : ThemeDialogFragment() {

    private lateinit var binding: DialogMoveCategoryBinding
    private lateinit var categoryAdapter: DialogCategoryAdapter
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private lateinit var onUpdateSelectedCategory: OnUpdateSelectedCategory

    companion object {
        const val TAG = "PurchaseConfirmationDialogMoveCategory"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUpdateSelectedCategory) {
            onUpdateSelectedCategory = context
        }
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
        binding = DialogMoveCategoryBinding.inflate(inflater, container, false).also {
            (parentFragment as? OnUpdateSelectedCategory)?.let {
                onUpdateSelectedCategory = it
            }
        }

        categoryAdapter = DialogCategoryAdapter(listener)
        categoryAdapter.updateCurrentCategories(currentCategory)
        if (existCategory != null) {
            categoryAdapter.submitList(existCategory)
        }

        binding.rcCategoryDialog.adapter = categoryAdapter

        categoryAdapter.onChangeTheme = { currentTheme, holder ->
            DecoratorView.changeBackgroundCardView(
                currentTheme,
                holder.itemLayout,
                holder.context
            )
            DecoratorView.changeText(currentTheme, holder.textView, holder.context)
            DecoratorView.changeCheckBox(currentTheme, holder.checkBox, holder.context)
        }


        binding.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            rcCategoryDialog.layoutManager = linearLayoutManager

            submitButton.setOnClickListener {
                onUpdateSelectedCategory.onUpdateCategoriesFromSelectedNote()
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }

        initThemeObserver()

        return binding.root
    }


    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }
}