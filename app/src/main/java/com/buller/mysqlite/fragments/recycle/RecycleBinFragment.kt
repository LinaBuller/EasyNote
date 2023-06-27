package com.buller.mysqlite.fragments.recycle

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentRecycleBinBinding
import com.buller.mysqlite.dialogs.DialogDeleteNote
import com.buller.mysqlite.dialogs.OnCloseDialogListener
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.utils.CustomPopupMenu
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment

class RecycleBinFragment : ThemeFragment(), OnCloseDialogListener {
    private lateinit var binding: FragmentRecycleBinBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private val noteDeleteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private var actionMode: ActionMode? = null
    private var wrapper: Context? = null
    var isActionMode = false

    companion object {
        const val ACTION_MODE_KEY_BIN = "ACTION_MODE_KEY_BIN"
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null && state.getBoolean(ACTION_MODE_KEY_BIN, false)) {
            actionMode = (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(ACTION_MODE_KEY_BIN, isActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_recyclerbin_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.bin_item_toolbar_multiselect -> {
//                        mNoteViewModel.readAllNotes.value?.forEach { note ->
//                            if (note.isDeleted) {
//                                mNoteViewModel.deleteNote(note)
//                            }
//                        }
                        actionMode =
                            (activity as MainActivity).startSupportActionMode(actionModeCallback)
                        noteDeleteAdapter.mViewModel = mNoteViewModel
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        binding.apply {
            rcViewDeletedNote.apply {
                adapter = noteDeleteAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            noteDeleteAdapter.onItemClick = { note, _, position ->
                if (actionMode != null) {
                    mNoteViewModel.changeSelectedNotesFromActionMode(note)
                    noteDeleteAdapter.notifyItemChanged(position)
                } else {
                    mNoteViewModel.setSelectedNote(note)
                    findNavController().navigate(R.id.action_recycleBinFragment_to_addFragment)
                }
            }
            noteDeleteAdapter.onItemLongClick = { view, note, i ->
                mNoteViewModel.setSelectedNote(note)
                showPopupMenuBinItem(view)
            }
        }

        initThemeObserver()
        initNotesLiveDataObserver()
        mNoteViewModel.selectedNotesFromActionMode.observe(viewLifecycleOwner) { list ->
            actionMode?.title = getString(R.string.selected_items, list.size)
        }
        return binding.root
    }

    private fun showPopupMenuBinItem(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        val currentNote = mNoteViewModel.selectedNote.value
        popupMenu.showPopupMenuBinItem(currentNote!!)

        popupMenu.onChangeItemNoteArchive = {
            mNoteViewModel.setSelectedNote(it)
            //DialogIsArchive().show(childFragmentManager, DialogIsArchive.TAG)
            popupMenu.dismiss()
        }
        popupMenu.onChangeItemNoteDelete = {
            DialogDeleteNote().show(childFragmentManager, DialogDeleteNote.TAG)
            popupMenu.dismiss()
        }
    }


    val actionModeCallback = object :ActionMode.Callback{
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val currentTheme = mNoteViewModel.currentTheme.value
            if (currentTheme != null) {
                DecoratorView.setColorBackgroundFromActionModeToolbar(requireActivity() as MainActivity,currentTheme)
            }
            isActionMode = true
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
                    builder.setTitle("Do you want permanent delete selected items?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mNoteViewModel.deleteOrUpdateSelectionNotesFromActionMode()
                        dialog.dismiss()
                        mode?.finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                    true
                }
                R.id.action_restore -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Do you want restore selected items?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mNoteViewModel.restoreSelectedNotesFromActionMode()
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
            mNoteViewModel.clearSelectedNotesFromActionMode()
            noteDeleteAdapter.notifyDataSetChanged()
            actionMode = null
            isActionMode = false
        }

    }
    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteDeleteAdapter.themeChanged(currentTheme)
        }
    }

    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            val listOfNoteDelete = arrayListOf<com.easynote.domain.models.Note>()
            listNotes.forEach { note ->
                if (note.isDeleted) {
                    listOfNoteDelete.add(note)
                }
            }
            noteDeleteAdapter.submitList(listOfNoteDelete)
        }
    }

    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        mNoteViewModel.updateStatusNote(isDelete, isArchive)
    }

}