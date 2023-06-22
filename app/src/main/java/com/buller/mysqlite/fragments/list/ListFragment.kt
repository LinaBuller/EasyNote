package com.buller.mysqlite.fragments.list

import androidx.appcompat.app.AlertDialog

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.dialogs.DialogAddNewCategory
import com.buller.mysqlite.dialogs.DialogIsArchive
import com.buller.mysqlite.dialogs.DialogDeleteNote
import com.buller.mysqlite.dialogs.DialogMoveCategory
import com.buller.mysqlite.dialogs.OnCloseDialogListener
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.CustomPopupMenu
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment

class ListFragment : ThemeFragment(), CategoryFromListFragmentAdapter.OnClickAddNewCategory,
    OnCloseDialogListener {

    lateinit var binding: FragmentListBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()
    private val noteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private lateinit var categoryAdapter: CategoryFromListFragmentAdapter
    private var isLineOfList: Boolean = true
    private var wrapper: Context? = null
    private lateinit var sharedPref: SharedPreferences
    var isActionMode = false

    override fun onCreate(state: Bundle?) {
        sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        isLineOfList = if ((requireActivity() as MainActivity).isFirstUsages) {
            true
        } else {
            sharedPref.getBoolean("KIND_OF_LIST", true)
        }
        super.onCreate(state)

        if (state != null && state.getBoolean(FragmentConstants.ACTION_MODE_KEY, false)) {
            mNoteViewModel.actionMode = (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FragmentConstants.ACTION_MODE_KEY, isActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        initNoteList()
        initCategory()
        initCategoriesLiveDataObserver()
        initThemeObserver()
        initNotesLiveDataObserver()

        binding.btAdd.setOnClickListener {
            mNoteViewModel.setSelectedNote(Note())
            view?.findNavController()?.navigate(R.id.action_listFragment_to_addFragment)
        }

        mNoteViewModel.selectedNotesFromActionMode.observe(viewLifecycleOwner) { list ->
            mNoteViewModel.actionMode?.title = getString(R.string.selected_items, list.size)
        }
        onBackPressedAndBackArrow()
        return binding.root
    }


    private fun onBackPressedAndBackArrow() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                super.isEnabled = true
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    companion object {
        const val TAG = "MyLog"
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

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme

        binding.apply {
            wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
            btAdd.setColorFilter(theme.backgroundDrawer(requireContext()))
            btAdd.backgroundTintList =
                ColorStateList.valueOf(theme.akcColor(requireContext()))
        }
    }

    private fun initNoteList() = with(binding) {

        mNoteViewModel.currentKindOfList.observe(viewLifecycleOwner) {
            isLineOfList = it

            if (isLineOfList) {
                val linearLayoutManager = LinearLayoutManager(requireContext())
                linearLayoutManager.reverseLayout = true
                linearLayoutManager.stackFromEnd = true
                rcView.layoutManager = linearLayoutManager
            } else {
                val gridLayoutManager =
                    GridLayoutManager(context, 2, RecyclerView.VERTICAL, true)
                rcView.layoutManager = gridLayoutManager
            }

            rcView.adapter = noteAdapter
            //noteAdapter.notifyDataSetChanged()
            val editor = sharedPref.edit()
            editor.apply {
                putBoolean("KIND_OF_LIST", isLineOfList)
                apply()
            }
        }

        noteAdapter.mViewModel = mNoteViewModel

        noteAdapter.onItemClick = { note, view, position ->
            val actionMode = mNoteViewModel.actionMode
            if (actionMode != null) {
                mNoteViewModel.changeSelectedNotes(note)
                noteAdapter.notifyItemChanged(position)
            } else {
                if (!note.isDeleted || !note.isArchive) {
                    mNoteViewModel.setSelectedNote(note)
                    view.findNavController().navigate(R.id.action_listFragment_to_addFragment)
                }
            }
        }
        noteAdapter.onItemLongClick = { view, item, _ ->
            mNoteViewModel.setSelectedNote(item)
            showPopupMenuLongClickNoteItem(view)
            Toast.makeText(requireContext(), "Long click", Toast.LENGTH_SHORT).show()
        }
        mNoteViewModel.setCurrentKindOfList(isLineOfList)
    }

    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val currentTheme = mNoteViewModel.currentTheme.value

            if (currentTheme != null) {
                DecoratorView.setColorBackgroundFromActionModeToolbar(requireActivity() as MainActivity,currentTheme)
            }

            isActionMode = true
            binding.apply {
                btAdd.visibility = View.GONE
                rcCategories.visibility = View.GONE
            }

            if (mode != null) {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.menu_bin_action_mode, menu)
            }
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item!!.itemId) {
                R.id.action_delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Do you want delete selected items?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mNoteViewModel.deleteOrUpdateSelectionNotes()
                        dialog.dismiss()
                        mode?.finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            val currentTheme = mNoteViewModel.currentTheme.value
            if (currentTheme != null) {
                DecoratorView.setThemeColorBackgroundNavigationBar(requireActivity() as MainActivity,currentTheme)
            }
            binding.apply {
                btAdd.visibility = View.VISIBLE
                rcCategories.visibility = View.VISIBLE
            }
            mNoteViewModel.clearSelectedNotes()
            noteAdapter.notifyDataSetChanged()
            mNoteViewModel.actionMode = null
            isActionMode = false
        }

    }

    private fun initCategory() = with(binding) {
        rcCategories.apply {
            categoryAdapter =
                CategoryFromListFragmentAdapter(requireContext(), this@ListFragment)
            adapter = categoryAdapter
            val linearLayout =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            layoutManager = linearLayout
        }
    }

    private fun initCategoriesLiveDataObserver() {
        mNoteViewModel.readAllCategories.observe(viewLifecycleOwner) { listCategories ->
            categoryAdapter.submitList(listCategories)
        }
    }

    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listAllNotes ->
            val listOfCurrentNotes = arrayListOf<Note>()
            listAllNotes.forEach { note ->
                if (!note.isDeleted && !note.isArchive) {
                    listOfCurrentNotes.add(note)
                }
            }
            noteAdapter.submitList(listOfCurrentNotes)
            binding.rcView.smoothScrollToPosition(noteAdapter.itemCount)
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteAdapter.themeChanged(currentTheme)
            categoryAdapter.themeChanged(currentTheme)
        }
    }

    private fun showPopupMenuSort(view: View) {
        val popupMenu = CustomPopupMenu(wrapper!!, view)
        popupMenu.showPopupMenuSort(mNoteViewModel)
    }

    private fun showPopupMenuToolbarMenu(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        popupMenu.showPopupMenuToolbar(isLineOfList)
        popupMenu.onChangeItemToolbar = {
            mNoteViewModel.setCurrentKindOfList(it)
        }
        popupMenu.onItemPas = {
            mNoteViewModel.actionMode = (activity as MainActivity).startSupportActionMode(actionModeCallback)
            noteAdapter.mViewModel = mNoteViewModel
        }
    }

    private fun showPopupMenuLongClickNoteItem(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        val currentNote = mNoteViewModel.selectedNote.value
        popupMenu.showPopupMenuNoteItem(currentNote!!, false)

        popupMenu.onChangeItemNote = {
            mNoteViewModel.updateNote(it)
        }

        popupMenu.onItemCrypt = {

        }

        popupMenu.onChangeItemNoteCategory = { selectedNote ->
            mNoteViewModel.setSelectedNote(selectedNote)
            DialogMoveCategory().show(childFragmentManager, DialogMoveCategory.TAG)
            popupMenu.dismiss()
        }

        popupMenu.onChangeItemNoteArchive = { selectedNote ->
            mNoteViewModel.setSelectedNote(selectedNote)
            DialogIsArchive().show(childFragmentManager, DialogIsArchive.TAG)
            popupMenu.dismiss()
        }
        popupMenu.onChangeItemNoteDelete = { selectedNote ->
            mNoteViewModel.setSelectedNote(selectedNote)
            DialogDeleteNote().show(childFragmentManager, DialogDeleteNote.TAG)
            popupMenu.dismiss()
        }
    }

    override fun onClickAddNewCategory() {
        DialogAddNewCategory().show(childFragmentManager, DialogAddNewCategory.TAG)
    }

    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        mNoteViewModel.updateStatusNote(isDelete, isArchive)
    }

}