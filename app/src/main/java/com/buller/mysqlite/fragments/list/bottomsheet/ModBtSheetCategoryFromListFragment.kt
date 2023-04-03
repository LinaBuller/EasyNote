package com.buller.mysqlite.fragments.list.bottomsheet

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.databinding.BottomSheetOfListFragmentBinding
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeBottomSheetFragment
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme

class ModBtSheetCategoryFromListFragment : ThemeBottomSheetFragment() {
    private lateinit var binding: BottomSheetOfListFragmentBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var categoryAdapter: CategoryFromListFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetOfListFragmentBinding.inflate(inflater, container, false)

        initCategoryAdapter()
        initCurrentThemeObserver()
        initCategoriesLiveDataObserver()

        binding.apply {

            etNameNewCategory.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    imBtAddCategoryList.visibility = View.VISIBLE
                } else {
                    imBtAddCategoryList.visibility = View.INVISIBLE
                }
            }
            imBtAddCategoryList.setOnClickListener {
                saveCategory()
                imBtAddCategoryList.visibility = View.INVISIBLE
            }
            imBottomSheetCategoryDismiss.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }

    private fun initCategoryAdapter() = with(binding) {
        categoryAdapter =
            CategoryFromListFragmentAdapter(context, viewLifecycleOwner)
        rcCategoriesBottom.apply {
            adapter = categoryAdapter
            isNestedScrollingEnabled = false;
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        }
    }

    private fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
//        должно быть не пустое поле и не пробелы
        if (title.isNotEmpty()) {
            val tempCategory = Category(titleCategory = title)
            mNoteViewModel.addCategory(tempCategory)

            etNameNewCategory.setText("")
            rcCategoriesBottom.scrollToPosition(categoryAdapter.listArray.size)
        } else {
            Toast.makeText(requireContext(), "Add text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoriesLiveDataObserver() {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            root.setBackgroundColor(theme.backgroundColor(requireContext()))
            etNameNewCategory.setTextColor(theme.textColor(requireContext()))
            etNameNewCategory.setHintTextColor(theme.textColorTabUnselect(requireContext()))
            imBtAddCategoryList.background.mutate()
            imBtAddCategoryList.setBackgroundColor(theme.backgroundDrawer(requireContext()))
            imBtAddCategoryList.setColorFilter(theme.akcColor(requireContext()))
        }
        if(dialog!=null){
            dialog!!.window!!.navigationBarColor = theme.setStatusBarColor(requireContext())
        }
    }

    private fun initCurrentThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetCategoryFromListFragment"
    }
}