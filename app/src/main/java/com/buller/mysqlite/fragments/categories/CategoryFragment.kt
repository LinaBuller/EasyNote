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
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.buller.mysqlite.BaseItemAdapterCallback
import com.buller.mysqlite.BaseViewHolder
import com.buller.mysqlite.CustomPopupMenu
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentCategoryBinding
import com.buller.mysqlite.fragments.BaseFragment
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.models.Category
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.CategoriesFragmentViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class CategoryFragment : BaseFragment(), BaseItemAdapterCallback<Category> {
    private lateinit var binding: FragmentCategoryBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val mCategoriesFragmentViewModel: CategoriesFragmentViewModel by viewModel()
    override val mBaseViewModel: BaseViewModel get() = mCategoriesFragmentViewModel

    private val categoryAdapter: CategoryAdapter by lazy { CategoryAdapter() }
    private val callback: ItemMoveCallback by lazy { ItemMoveCallback(categoryAdapter) }
    private val touchHelper: ItemTouchHelper by lazy { ItemTouchHelper(callback) }

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
        onBackPressedAndBackArrow()
        return binding.root
    }

    private fun onBackPressedAndBackArrow() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mCategoriesFragmentViewModel.updateCategories.value!!.isNotEmpty()) {
                    mCategoriesFragmentViewModel.updateAfterMovedCategories()
                }
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            if (mCategoriesFragmentViewModel.updateCategories.value!!.isNotEmpty()) {
                mCategoriesFragmentViewModel.updateAfterMovedCategories()
            }
            findNavController().popBackStack()
        }
    }


    private fun initAdapter() = with(binding) {

        rcCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            touchHelper.attachToRecyclerView(rcCategories)
            adapter = categoryAdapter
        }
        categoryAdapter.attachCallback(this@CategoryFragment)

        categoryAdapter.onItemMove = {
            mCategoriesFragmentViewModel.setUpdateCategories(it)
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
                val position = categoryAdapter.itemCount
                val tempCategory = Category(titleCategory = title, position = position)
                mCategoriesFragmentViewModel.setCategory(tempCategory)
            }
            etNameNewCategory.setText("")

        } else {
            Toast.makeText(requireContext(), R.string.need_more_letters, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryLiveDataObserver() = with(binding) {
        mCategoriesFragmentViewModel.categories.observe(viewLifecycleOwner) { listCategories ->
            if (listCategories.isEmpty()) {
                backgroundCategoryIcon.visibility = View.VISIBLE
            } else {
                backgroundCategoryIcon.visibility = View.INVISIBLE
            }
            val sortedCategories = listCategories.sortedBy { it.position }
            categoryAdapter.submitList(sortedCategories)
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
            categoryAdapter.setTheme(currentTheme)
        }
    }

    override fun onMultiItemClick(
        model: Category,
        view: View,
        position: Int,
        holder: BaseViewHolder<Category>
    ) {
            mCategoriesFragmentViewModel.setSelectedCategory(model)
            val currentTheme = mNoteViewModel.currentTheme.value
            val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
            popupMenu.gravity = Gravity.END

            popupMenu.onDeleteItemCategory = {
                showDeleteDialog()
            }

            popupMenu.onChangeItemCategory = {
                showEditTitleCategoryDialog(model)
            }

            popupMenu.showPopupMenuCategoryItem(model)

    }

    override fun onMultiItemLongClick(model: Category, view: View): Boolean {
        return false
    }
}