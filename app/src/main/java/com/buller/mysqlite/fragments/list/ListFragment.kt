package com.buller.mysqlite.fragments.list

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.icu.util.Calendar
import android.os.Build
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.fragments.list.bottomsheet.CategoryFromListFragmentAdapter
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.SpacingItemDecorator
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.google.android.material.snackbar.Snackbar


class ListFragment : ThemeFragment() {

    lateinit var binding: FragmentListBinding
    private lateinit var mNoteViewModel: NotesViewModel

    private val noteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private lateinit var categoryAdapter: CategoryFromListFragmentAdapter
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelperNote: ItemTouchHelper
    private var isLinearLayout: Boolean = false
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
                val sortButton = menu.findItem(R.id.sort).actionView as ImageButton?
                if (sortButton != null) {
                    sortButton.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_sort_24, null)
                }
                sortButton?.setOnClickListener {
                    val viewButton: ImageButton = requireActivity().findViewById(R.id.sort)
                    showPopupMenuSort(viewButton)
                }

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
                    showPopupMenuToolbarMenu(viewButton)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    fun showPopupMenuSort(view: View) {
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

    fun showPopupMenuToolbarMenu(view: View) {
        val popupMenu = PopupMenu(wrapper, view)
        val currentTheme = mNoteViewModel.currentTheme.value

        val selectorViewListNote = setSelectorItem(popupMenu)

        popupMenu.menuInflater.inflate(
            R.menu.menu_toolbar_context_menu_list_fragment,
            popupMenu.menu
        )
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                item?.actionView = View(requireContext())
                item?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return false
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        return false
                    }
                })
                when (item!!.itemId) {
                    0 -> {
                        if (isLinearLayout) {
                            if (currentTheme != null) {
                                selectorViewListNote.icon = DecoratorView.setIcon(
                                    requireContext(),
                                    currentTheme.themeId, R.drawable.ic_grid_view_24
                                )
                            }
                            item.title = "Grid list"
                            val linearLayoutManager = LinearLayoutManager(requireContext())
                            linearLayoutManager.reverseLayout = true
                            linearLayoutManager.stackFromEnd = true
                            binding.rcView.layoutManager = linearLayoutManager
                            binding.rcView.adapter = noteAdapter
                            noteAdapter.notifyDataSetChanged()
                            isLinearLayout = false

                        } else {
                            if (currentTheme != null) {
                                selectorViewListNote.icon = DecoratorView.setIcon(
                                    requireContext(),
                                    currentTheme.themeId, R.drawable.ic_view_list
                                )
                            }
                            item.title = "View list"

                            val gridLayoutManager =
                                GridLayoutManager(context, 2, RecyclerView.VERTICAL, true)
                            binding.rcView.layoutManager = gridLayoutManager
                            binding.rcView.adapter = noteAdapter
                            noteAdapter.notifyDataSetChanged()
                            isLinearLayout = true
                        }
                    }


                }
                return false
            }
        })
        showIconPopupMenu(popupMenu)
        popupMenu.show()
    }
    private fun showIconPopupMenu(popupMenu: PopupMenu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        } else {
            try {
                val fieldPopupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldPopupMenu.isAccessible = true
                val mPopup = fieldPopupMenu.get(popupMenu)
                mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            }
        }
    }
    private fun setSelectorItem(popupMenu: PopupMenu): MenuItem {
        var title = ""
        var resIcon: Drawable? = null
        val currentTheme = mNoteViewModel.currentTheme.value
        if (isLinearLayout) {
            title = "View list"
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    requireContext(),
                    currentTheme.themeId,
                    R.drawable.ic_view_list
                )!!
            }
        } else {
            title = "Grid list"
            if (currentTheme != null) {
                resIcon = DecoratorView.setIcon(
                    requireContext(),
                    currentTheme.themeId,
                    R.drawable.ic_grid_view_24
                )!!
            }
        }
        return popupMenu.menu.add(Menu.NONE, 0, 0, title).setIcon(resIcon)
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
            btAdd.setColorFilter(theme.backgroundDrawer(requireContext()))
            btAdd.backgroundTintList =
                ColorStateList.valueOf(theme.akcColor(requireContext()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "ListFragment onCreateView")
        binding = FragmentListBinding.inflate(inflater, container, false)
        mNoteViewModel =
            ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        initNoteList()
        initCategory()
        initCategoriesLiveDataObserver()
        initThemeObserver()
        initTouchHelperNote()
        initBottomBar()
        touchHelperNote.attachToRecyclerView(binding.rcView)
        undoEventNote()
        initNotesLiveDataObserver()
        return binding.root
    }

    private fun initNoteList() = with(binding) {

        rcView.apply {
            addItemDecoration(SpacingItemDecorator(-30))
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            layoutManager = linearLayoutManager
            adapter = noteAdapter
        }
    }

    private fun initCategoriesLiveDataObserver() {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    private fun initCategory() = with(binding) {
        rcCategories.apply {
            categoryAdapter =
                CategoryFromListFragmentAdapter(requireContext(), viewLifecycleOwner)
            adapter = categoryAdapter
            val linearLayout =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            layoutManager = linearLayout
        }
    }

    private fun initBottomBar() = with(binding) {
        btAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE, true)
            findNavController().navigate(
                R.id.action_listFragment_to_addFragment,
                bundle
            )
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
            ItemTouchHelperCallbackNotes(
                noteAdapter,
                swipeBackground,
                deleteIcon,
                mNoteViewModel
            )
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
            categoryAdapter.themeChanged(currentTheme)
        }
    }

    private fun undoEventNote() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mNoteViewModel.noteEvent.collect { event ->
                when (event) {
                    is NotesViewModel.NoteEvent.ShowUndoDeleteNoteMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Note deleted",
                            Snackbar.LENGTH_LONG
                        )
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