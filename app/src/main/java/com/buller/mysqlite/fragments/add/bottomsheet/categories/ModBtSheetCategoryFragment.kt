package com.buller.mysqlite.fragments.add.bottomsheet.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.databinding.FragmentCategoryBinding
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModBtSheetCategoryFragment() : BottomSheetDialogFragment(),
    BtSheetCategoryAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var mNotesViewModel: NotesViewModel
    private lateinit var categoryBtSheetCategoryAdapter: BtSheetCategoryAdapter
    private val listSelectedCategory = arrayListOf<Category>()

    companion object {
        const val TAG = "ModBtSheetCategoryFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        mNotesViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        if (mNotesViewModel.editedSelectCategoryFromAddFragment.value != null) {
            listSelectedCategory.addAll(mNotesViewModel.editedSelectCategoryFromAddFragment.value!!)
        } else {
            listSelectedCategory.clear()
        }

        categoryBtSheetCategoryAdapter = BtSheetCategoryAdapter(this, listSelectedCategory)
        binding.apply {
            rcCategories.apply {
                layoutManager = LinearLayoutManager(requireContext())
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
        return binding.root
    }

    private fun saveCategory() = with(binding) {
        val title = etNameNewCategory.text.toString()
        //должно быть не пустое поле и не пробелы
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

}