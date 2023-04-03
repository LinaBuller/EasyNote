package com.buller.mysqlite.fragments.add.bottomsheet.categories

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation.Callback.DISPATCH_MODE_STOP
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.InsetsWithKeyboardCallback
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.BottomSheetFragmentCategoryAddFragmentBinding
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeBottomSheetFragment
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class ModBtSheetCategoryFragment() : ThemeBottomSheetFragment(),
    BtSheetCategoryAdapter.OnItemClickListener {
    private lateinit var binding: BottomSheetFragmentCategoryAddFragmentBinding
    private lateinit var mNotesViewModel: NotesViewModel
    private lateinit var categoryBtSheetCategoryAdapter: BtSheetCategoryAdapter
    private val listSelectedCategory = arrayListOf<Category>()

    companion object {
        const val TAG = "ModBtSheetCategoryFragment"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            layoutCategoryAddFragment.background.setTintList(
                ColorStateList.valueOf(
                    theme.backgroundColor(
                        requireContext()
                    )
                )
            )
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
            imBtSaveCategory.background.setTintList(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
            imBtSaveCategory.setColorFilter(theme.akcColor(requireContext()))
        }
        if (dialog != null) {
            dialog!!.window!!.navigationBarColor = theme.setStatusBarColor(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentCategoryAddFragmentBinding.inflate(inflater, container, false)
        mNotesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        if (mNotesViewModel.editedSelectCategoryFromAddFragment.value != null) {
            listSelectedCategory.addAll(mNotesViewModel.editedSelectCategoryFromAddFragment.value!!)
        } else {
            listSelectedCategory.clear()
        }

        categoryBtSheetCategoryAdapter = BtSheetCategoryAdapter(this, listSelectedCategory)
        binding.apply {
            rcCategories.apply {
                layoutManager = StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL)
                adapter = categoryBtSheetCategoryAdapter

            }

            etNameNewCategory.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    imBtAddCategory.visibility = View.VISIBLE

                } else {
                    imBtAddCategory.visibility = View.INVISIBLE
                }
            }

            imBtSaveCategory.setOnClickListener {
                mNotesViewModel.selectEditedCategory(listSelectedCategory)
                dismiss()
            }
            imBtAddCategory.setOnClickListener {
                saveCategory()
            }
        }
        initCategoryLiveDataObserver()
        initThemeObserver()

            //(dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return binding.root
    }

    private fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title.isNotEmpty()) {

            lifecycleScope.launch(Dispatchers.IO) {
                val tempCategory = Category(titleCategory = title)
                onCheckBoxClick(tempCategory, true)
                mNotesViewModel.addCategory(tempCategory)
            }
            etNameNewCategory.setText("")

        } else {
            Toast.makeText(requireContext(), "Add text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryLiveDataObserver() {
        mNotesViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryBtSheetCategoryAdapter.submitList(listCategories)
        }
    }

    override fun onCheckBoxClick(category: Category, isChecked: Boolean) {
        synchronized(listSelectedCategory) {
            if (isChecked) {
                listSelectedCategory.add(category)
            } else {
                listSelectedCategory.remove(category)
            }
        }
    }

    private fun initThemeObserver() {
        mNotesViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryBtSheetCategoryAdapter.themeChanged(currentTheme)
        }
    }
}