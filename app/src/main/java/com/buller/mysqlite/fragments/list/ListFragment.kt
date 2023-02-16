package com.buller.mysqlite.fragments.list

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.R
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.fragments.list.bottomsheet.ModBtSheetCategoryFromListFragment
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.SpacingItemDecorator
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.google.android.material.snackbar.Snackbar


class ListFragment : ThemeFragment() {
    lateinit var binding: FragmentListBinding
    private lateinit var mNoteViewModel: NotesViewModel

    private val noteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelperNote: ItemTouchHelper
    var wrapper: Context? = null

    companion object {
        const val TAG = "MyLog"
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
        Log.d(TAG, "ListFragment onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_list_fragment, menu)
                val locButton = menu.findItem(R.id.menu_filter).actionView as ImageButton?
                if (locButton != null) {
                    locButton.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_filters_menu_list_fragment,
                            null
                        )
                }
                locButton?.setOnClickListener {
                    val viewButton: ImageButton = requireActivity().findViewById(R.id.menu_filter)
                    showPopupMenu(viewButton)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                   return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(
            R.menu.menu_filter_list_fragment,
            popupMenu.menu
        )
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.noSort -> {
                    mNoteViewModel.resetSort()
                    Toast.makeText(requireContext(), "no sort", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }
                R.id.sortAZ -> {
                    mNoteViewModel.setSort(
                        sortColumn = ConstantsDbName.NOTE_TITLE,
                        sortOrder = 0
                    )
                    Toast.makeText(requireContext(), "sortAZ", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }
                R.id.sortZA -> {
                    mNoteViewModel.setSort(sortColumn = ConstantsDbName.NOTE_TITLE)
                    Toast.makeText(requireContext(), "sortZA", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnMenuItemClickListener true
                }
                R.id.sort_newest_oldest -> {
                    mNoteViewModel.setSort(sortColumn = ConstantsDbName.NOTE_TIME)
                    return@setOnMenuItemClickListener true
                }
                R.id.sort_oldest_newest -> {
                    mNoteViewModel.setSort(
                        sortColumn = ConstantsDbName.NOTE_TIME,
                        sortOrder = 0
                    )
                    return@setOnMenuItemClickListener true
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
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }

        }
        popupMenu.show()
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
            imBottomSheetCategoryOpen.setBackgroundColor(
                theme.backgroundDrawer(
                    requireContext()
                )
            )

            btAdd.setColorFilter(theme.backgroundDrawer(requireContext()))
            btAdd.backgroundTintList = ColorStateList.valueOf(theme.akcColor(requireContext()))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "ListFragment onCreateView")
        binding = FragmentListBinding.inflate(inflater, container, false)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        initNoteList()
        initThemeObserver()
        initTouchHelperNote()
        initBottomBar()
        touchHelperNote.attachToRecyclerView(binding.rcView)
        undoEventNote()
        initNotesLiveDataObserver()

        binding.imBottomSheetCategoryOpen.setOnClickListener {
            ModBtSheetCategoryFromListFragment().show(
                childFragmentManager,
                ModBtSheetCategoryFromListFragment.TAG
            )
        }

        return binding.root
    }

    private fun initNoteList() = with(binding) {
        rcView.apply {
            adapter = noteAdapter
            addItemDecoration(SpacingItemDecorator(-30))
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            layoutManager = linearLayoutManager

        }
    }

    private fun initBottomBar() = with(binding) {
        btAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE, true)
            findNavController().navigate(R.id.action_listFragment_to_addFragment, bundle)
        }
    }

    private fun initTouchHelperNote() {
        val swipeBackground = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                resources.getColor(R.color.red_delete, null),
                resources.getColor(R.color.red_delete, null)
            )
        )
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        callbackNotes =
            ItemTouchHelperCallbackNotes(noteAdapter, swipeBackground, deleteIcon, mNoteViewModel)
        touchHelperNote = ItemTouchHelper(callbackNotes)

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
            binding.rcView.smoothScrollToPosition(noteAdapter.itemCount)
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteAdapter.themeChanged(currentTheme)
        }
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
}