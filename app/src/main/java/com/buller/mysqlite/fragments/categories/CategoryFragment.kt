package com.buller.mysqlite.fragments.categories

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.databinding.FragmentCategoryBinding
import com.buller.mysqlite.dialogs.DialogChangeTitleCategory
import com.buller.mysqlite.dialogs.DialogDeleteCategory
import com.buller.mysqlite.dialogs.OnCloseDialogListener
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.CustomPopupMenu
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryFragment : ThemeFragment(), OnCloseDialogListener {
    private lateinit var binding: FragmentCategoryBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter() }
    private var wrapper: Context? = null

    companion object {
        const val TAG = "ModBtSheetCategoryFragment"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            etNameNewCategory.setTextColor(theme.textColor(requireContext()))
            etNameNewCategory.setHintTextColor(theme.textColorTabUnselect(requireContext()))
            imBtAddCategory.background.setTintList(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
            imBtAddCategory.setColorFilter(theme.akcColor(requireContext()))
        }
        wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        initAdapter()
        binding.apply {

            etNameNewCategory.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    imBtAddCategory.visibility = View.VISIBLE

                } else {
                    imBtAddCategory.visibility = View.INVISIBLE
                }
            }

            imBtAddCategory.setOnClickListener {
                saveCategory()
            }

        }
        initCategoryLiveDataObserver()
        initThemeObserver()

        return binding.root
    }

    private fun initAdapter() = with(binding) {

        rcCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            val callback = ItemMoveCallback(categoryAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(rcCategories)
            adapter = categoryAdapter
        }

        categoryAdapter.onItemClickCrypto = {

        }

        categoryAdapter.onItemClickPopupMenu = { category, view ->
            val currentTheme = mNoteViewModel.currentTheme.value
            mNoteViewModel.setSelectedCategory(category)
            val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
            popupMenu.gravity = Gravity.END
            popupMenu.onDeleteItemCategory = {
                DialogDeleteCategory().show(childFragmentManager, DialogDeleteCategory.TAG)
            }
            popupMenu.onChangeItemCategory = {
                DialogChangeTitleCategory().show(
                    childFragmentManager,
                    DialogChangeTitleCategory.TAG
                )
            }
            popupMenu.showPopupMenuCategoryItem(category)
        }
    }

    private fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title.isNotEmpty()) {

            lifecycleScope.launch(Dispatchers.IO) {
                val tempCategory = Category(titleCategory = title)
                mNoteViewModel.addCategory(tempCategory)
            }
            etNameNewCategory.setText("")

        } else {
            Toast.makeText(requireContext(), "Add text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryLiveDataObserver() {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }


    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        val currentCategory = mNoteViewModel.selectedCategory.value
        if (currentCategory != null) {
            mNoteViewModel.deleteCategory(currentCategory)
        }
    }
}