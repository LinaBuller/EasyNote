package com.buller.mysqlite.fragments.list.category

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentCategoryBinding
import com.buller.mysqlite.fragments.list.category.ItemTouchHelperCallbackCategories
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class ModBtSheetCategoryFragmentFomListFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var mNotesViewModel: NotesViewModel
    private lateinit var categoryBtSheetCategoryAdapter: CategoryFromListFragmentAdapter
    private lateinit var callbackCategories: ItemTouchHelperCallbackCategories
    private lateinit var touchHelper: ItemTouchHelper
    private var onSelectedCategoryListener: OnSelectedCategoryListener? = null
    private var idNewCategory: Long = 0L

    companion object{
        const val TAG = "ModBtSheetCategoryFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelectedCategoryListener) {
            onSelectedCategoryListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false).also {
            (parentFragment as? OnSelectedCategoryListener)?.let {
                onSelectedCategoryListener = it
            }
        }
        mNotesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        categoryBtSheetCategoryAdapter = CategoryFromListFragmentAdapter()
        binding.apply {
            rcCategories.apply {
                adapter = categoryBtSheetCategoryAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            initTouchHelper(rcCategories)
            imBtSaveCategory.setOnClickListener {
                saveCategory()
            }
        }
        undoEvent()
        initCategoryLiveDataObserver()
        return binding.root
    }

    fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
        //должно быть не пустое поле и не пробелы
        if (title.isNotEmpty()) {
            idNewCategory = mNotesViewModel.addCategory(Category(titleCategory = title))
        } else {
            Toast.makeText(requireContext(), "Add text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCategoryLiveDataObserver() {
        mNotesViewModel.editedNewCategory.observe(viewLifecycleOwner) { listCategories ->
            categoryBtSheetCategoryAdapter.submitList(listCategories)
        }
    }

    interface OnSelectedCategoryListener {
        fun idCategoryFromSave(listIdSelectedCategory: List<Int>)
    }

    private fun initTouchHelper(rcView: RecyclerView) {
        val swipeBackground = ColorDrawable(resources.getColor(R.color.akcient2, null))
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        callbackCategories = ItemTouchHelperCallbackCategories(
            categoryBtSheetCategoryAdapter,
            swipeBackground,
            deleteIcon,
            mNotesViewModel
        )
        touchHelper = ItemTouchHelper(callbackCategories)
        touchHelper.attachToRecyclerView(rcView)
    }

    private fun undoEvent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mNotesViewModel.categoryEvent.collect { event ->
                when (event) {
                    is NotesViewModel.CategoryEvent.ShowUndoDeleteCategoryMessage -> {
                        Snackbar.make(requireView(), "Category deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                mNotesViewModel.onUndoDeleteClickCategory(event.category)
                            }.show()
                    }
                }
            }
        }
    }

    //Add menu to toolbar
    override fun onResume() {
        super.onResume()
//        (requireActivity() as MainActivity).setToolbarMenu(
//            R.menu.menu_toolbar_list_fragment,
//            menuItemClickListener
//        )
    }

//    fun onClickAddNewCategory(view: View) = with(binding) {
//        val title = etNameNewCategory.text.toString()
//        if (title != "") {
//            val id = notesViewModel.insertCategory(ItemCategoryBase(etNameNewCategory.text.toString()))
//            list.add(ItemCategory(id, title))
//            categoryAdapter.notifyDataSetChanged()
//            etNameNewCategory.text.clear()
//        }
//    }

    private fun fillAdapter() {
//        job?.cancel()
//        job = CoroutineScope(Dispatchers.Main).launch {
//            val items =  notesViewModel.readCategoriesDbForItemCategory()
//            list.addAll(items)
//            categoryAdapter.notifyDataSetChanged()
//        }
    }

    fun removeItemDb(id: Long) {
//       notesViewModel.removeCategory(id)
    }
}