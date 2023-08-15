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
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.databinding.FragmentCategoryBinding
import com.buller.mysqlite.theme.BaseTheme
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.easynote.domain.models.Category
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.viewmodels.CategoriesFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryFragment : ThemeFragment() {
    private lateinit var binding: FragmentCategoryBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mCategoriesFragmentViewModel: CategoriesFragmentViewModel by viewModel()

    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter() }
    private var wrapper: Context? = null
    private var wrapperDialog: Context? = null
    private var wrapperDialogEditCategory: Context? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
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
            mCategoriesFragmentViewModel.setSelectedCategory(category)
            val popupMenu = CustomPopupMenu(wrapper!!, view)
            popupMenu.gravity = Gravity.END

            popupMenu.onDeleteItemCategory = {
                showDeleteDialog()
            }

            popupMenu.onChangeItemCategory = {
                showEditTitleCategoryDialog(category)
            }

            popupMenu.showPopupMenuCategoryItem(category)
        }

        categoryAdapter.onChangeTheme = { currentThemeId, holder ->
            DecoratorView.changeBackgroundCardView(currentThemeId, holder.cardItem, holder.context)
            DecoratorView.changeBackgroundText(currentThemeId, holder.titleCategory, holder.context)
            DecoratorView.changeIconColor(currentThemeId, holder.ibCrypto, holder.context)
            DecoratorView.changeIconColor(currentThemeId, holder.ibPopupmenuItem, holder.context)
        }
    }

    private fun showDeleteDialog() {
        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.message_permanent_delete_category)
            positiveButton(R.string.yes) { dialog ->
                mCategoriesFragmentViewModel.deleteSelectedCategory()
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun showEditTitleCategoryDialog(category: Category) {
        MaterialDialog(wrapperDialogEditCategory!!).show {
            title(R.string.edit_title_category)
            val titleEditedCategory = category.titleCategory
            val editCategoryDialog = customView(
                R.layout.dialog_add_new_category,
                scrollable = false,
                horizontalPadding = true
            )
            val field = editCategoryDialog.findViewById<EditText>(R.id.et_add_category)

            if (field.requestFocus()) {
                editCategoryDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
            field.setText(titleEditedCategory)
            field.setSelection(titleEditedCategory.length)

            positiveButton(R.string.yes) { dialog ->
                val input = dialog.getCustomView().findViewById<EditText>(R.id.et_add_category)
                if (input.text.toString().isNotEmpty() && input.text.toString().isNotBlank()) {
                    val updatedCategory = category.copy(titleCategory = input.text.toString())
                    mCategoriesFragmentViewModel.updateCategory(updatedCategory)
                    Toast.makeText(
                        requireContext(),
                        "You change ${input.text}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), R.string.need_more_letters, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title.isNotEmpty()) {

            lifecycleScope.launch(Dispatchers.IO) {
                val tempCategory = Category(titleCategory = title)
                mCategoriesFragmentViewModel.setCategory(tempCategory)
            }
            etNameNewCategory.setText("")

        } else {
            Toast.makeText(requireContext(), R.string.need_more_letters, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryLiveDataObserver() {
        mCategoriesFragmentViewModel.categories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme

        wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
        wrapperDialogEditCategory =
            ContextThemeWrapper(requireContext(), theme.styleDialogAddCategory())

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
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }
}