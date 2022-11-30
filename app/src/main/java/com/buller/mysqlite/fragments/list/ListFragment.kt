package com.buller.mysqlite.fragments.list

import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.fragments.constans.SortedConstants
import com.buller.mysqlite.fragments.list.category.CategoryFromListFragmentAdapter
import com.buller.mysqlite.fragments.list.category.ItemTouchHelperCallbackCategories
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.SpacingItemDecorator
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar


class ListFragment : ThemeFragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var mNoteViewModel: NotesViewModel

    private lateinit var noteAdapter: NotesAdapter
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelperNote: ItemTouchHelper

    private lateinit var categoryAdapter: CategoryFromListFragmentAdapter
    private lateinit var callbackCategories: ItemTouchHelperCallbackCategories
    private lateinit var touchHelperCategories: ItemTouchHelper

    companion object {
        const val TAG = "MyLog"
    }

    private val menuItemClickListener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (item != null) {
                when (item.itemId) {

                    R.id.noSort -> {
                        mNoteViewModel.sort(SortedConstants.NO_SORT)
                        Toast.makeText(requireContext(), "no sort", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.sortAZ -> {
                        mNoteViewModel.sort(SortedConstants.SORT_AZ)
                        Toast.makeText(requireContext(), "sortAZ", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.sortZA -> {
                        mNoteViewModel.sort(SortedConstants.SORT_ZA)
                        Toast.makeText(requireContext(), "sortZA", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.sort_newest_oldest -> {
                        mNoteViewModel.sort(SortedConstants.SORT_NEWOLD)

                        return true
                    }
                    R.id.sort_oldest_newest -> {
                        mNoteViewModel.sort(SortedConstants.SORT_OLDNEW)
                        return true
                    }
                    R.id.filter_by_date -> {
                        val c: Calendar = Calendar.getInstance();
                        val mYear = c.get(Calendar.YEAR);
                        val mMonth = c.get(Calendar.MONTH);
                        val mDay = c.get(Calendar.DAY_OF_MONTH);
                        val dpd = DatePickerDialog(
                            requireContext(),
                            { view, year, monthOfYear, dayOfMonth -> // Display Selected date in textbox
                                //isSelectedDate = true
                                //readDbFromSelectData(year, monthOfYear + 1, dayOfMonth)
                            }, mYear, mMonth, mDay
                        )
                        dpd.show()
                    }
                    else -> {

                    }
                }
            }
            return false
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setToolbarMenu(
            R.menu.menu_toolbar_list_fragment,
            menuItemClickListener
        )
        Log.d(TAG, "ListFragment onResume")
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.bottomAppBar.setBackgroundColor(theme.backgroundBottomDrawer(requireContext()))
        binding.bottom.frame.setBackgroundColor(theme.backgroundBottomDrawer(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "ListFragment onCreateView")
        binding = FragmentListBinding.inflate(inflater, container, false)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        initNoteList()
        initTouchHelperNote()
        initBottomBar()
        initBottomList()
        initTouchHelperCategories()
        touchHelperNote.attachToRecyclerView(binding.rcView)
        undoEventNote()
        initNotesLiveDataObserver()
        initCategoriesLiveDataObserver()

        return binding.root
    }

    private fun initNoteList() = with(binding) {
        noteAdapter = NotesAdapter(requireContext())

        rcView.apply {
            adapter = noteAdapter
            addItemDecoration(SpacingItemDecorator(-30))
            layoutManager =LinearLayoutManager(requireContext())
        }
    }

    private fun initBottomBar() = with(binding) {
        btAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE, true)
            findNavController().navigate(R.id.action_listFragment_to_addFragment, bundle)
        }
        btFind.setOnClickListener {

        }

    }

    private fun initBottomList() = with(binding) {
        categoryAdapter = CategoryFromListFragmentAdapter(mNoteViewModel, context)

        BottomSheetBehavior.from(bottom.linearLayoutBottom).apply {
            peekHeight = 220
            maxHeight = 1200
            (this as CustomDraggableBottomSheetBehavior).draggableView = bottom.linearLayoutDrag
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottom.rcCategoriesBottom.apply {
            adapter = categoryAdapter
            isNestedScrollingEnabled = false;
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
            //layoutManager = GridLayoutManager(requireContext(),4,GridLayoutManager.HORIZONTAL,true)
        }

        bottom.etNameNewCategory.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                bottom.imBtAddCategoryList.visibility = View.VISIBLE
                btAdd.hide()
            } else {
                btAdd.show()
                bottom.imBtAddCategoryList.visibility = View.INVISIBLE
            }
        }
        bottom.imBtAddCategoryList.setOnClickListener {
            saveCategory()
        }
    }

    private fun initTouchHelperNote() {
        val swipeBackground = ColorDrawable(resources.getColor(R.color.red_delete, null))
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        callbackNotes =
            ItemTouchHelperCallbackNotes(noteAdapter, swipeBackground, deleteIcon, mNoteViewModel)
        touchHelperNote = ItemTouchHelper(callbackNotes)
    }

    private fun undoEventNote() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mNoteViewModel.noteEvent.collect { event ->
                when (event) {
                    is NotesViewModel.NoteEvent.ShowUndoDeleteNoteMessage -> {
                        Snackbar.make(requireView(), "Note deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                mNoteViewModel.onUndoClickNote(event.note)
                            }.show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initTouchHelperCategories() {
        val swipeBackground = ColorDrawable(resources.getColor(R.color.red_delete, null))
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        callbackCategories =
            ItemTouchHelperCallbackCategories(
                categoryAdapter,
                swipeBackground,
                deleteIcon,
                mNoteViewModel
            )
        touchHelperCategories = ItemTouchHelper(callbackCategories)
    }

    private fun initCategoriesLiveDataObserver() {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            val listOfNoteNotDelete = arrayListOf<Note>()
            listNotes.forEach { note ->
                if (!note.isDeleted) {
                    listOfNoteNotDelete.add(note)
                }
            }
            noteAdapter.submitList(listOfNoteNotDelete)
        }
    }

    private fun saveCategory() = with(binding) {
        val title = bottom.etNameNewCategory.text.toString()
//        должно быть не пустое поле и не пробелы
        if (title.isNotEmpty()) {
            val tempCategory = Category(titleCategory = title)
            mNoteViewModel.addCategory(tempCategory)

            bottom.etNameNewCategory.setText("")
            bottom.rcCategoriesBottom.scrollToPosition(categoryAdapter.listArray.size)
        } else {
            Toast.makeText(requireContext(), "Add text", Toast.LENGTH_SHORT).show()
        }
    }

}