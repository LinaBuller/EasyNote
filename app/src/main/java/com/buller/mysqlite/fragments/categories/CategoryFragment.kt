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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.databinding.FragmentCategoryBinding
import com.buller.mysqlite.dialogs.DialogCategoryAdapter
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
import java.util.Collections

class CategoryFragment: ThemeFragment(), OnCloseDialogListener {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var mNotesViewModel: NotesViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private var wrapper: Context? = null

//    private val itemTouchHelper by lazy {
//        val simpleItemTouchCallback = object :
//            ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {
//
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                val adapter = recyclerView.adapter as CategoryAdapter
//                val fromPosition = viewHolder.adapterPosition
//                val toPosition =  target.adapterPosition
//                adapter.itemMoved(fromPosition,toPosition)
//                return true
//            }
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                return
//            }
//
//            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//                super.onSelectedChanged(viewHolder, actionState)
//                if (actionState == ACTION_STATE_DRAG){
//                    viewHolder?.itemView?.alpha =0.5f
//                }
//            }
//
//            override fun clearView(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder
//            ) {
//                super.clearView(recyclerView, viewHolder)
//                viewHolder.itemView.alpha = 1.0f
//            }
//        }
//       ItemTouchHelper(simpleItemTouchCallback)
//    }

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
        mNotesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        categoryAdapter = CategoryAdapter()
        binding.apply {
            rcCategories.apply {
                layoutManager = LinearLayoutManager(requireContext())
                val callback = ItemMoveCallback(categoryAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(rcCategories)
                adapter = categoryAdapter

            }

           // itemTouchHelper.attachToRecyclerView(rcCategories)
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

            categoryAdapter.onItemClickCrypto = {

            }
            categoryAdapter.onItemClickPopupMenu = { category, view ->
                val currentTheme = mNotesViewModel.currentTheme.value
                mNotesViewModel.setSelectedCategory(category)
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
        initCategoryLiveDataObserver()
        initThemeObserver()

        return binding.root
    }

    private fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title.isNotEmpty()) {

            lifecycleScope.launch(Dispatchers.IO) {
                val tempCategory = Category(titleCategory = title)
                mNotesViewModel.addCategory(tempCategory)
            }
            etNameNewCategory.setText("")

        } else {
            Toast.makeText(requireContext(), "Add text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryLiveDataObserver() {
        mNotesViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    private fun initThemeObserver() {
        mNotesViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            categoryAdapter.themeChanged(currentTheme)
        }
    }


    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        val currentCategory = mNotesViewModel.selectedCategory.value
        if (currentCategory != null) {
            mNotesViewModel.deleteCategory(currentCategory)
        }
    }
}