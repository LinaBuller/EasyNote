package com.buller.mysqlite.fragments.archive

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.buller.mysqlite.databinding.FragmentArchiveBinding
import com.buller.mysqlite.dialogs.DialogDeleteNote
import com.buller.mysqlite.dialogs.DialogIsArchive
import com.buller.mysqlite.dialogs.OnCloseDialogListener
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.CustomPopupMenu
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment


class ArchiveFragment : ThemeFragment(), View.OnCreateContextMenuListener, OnCloseDialogListener {
    private lateinit var binding: FragmentArchiveBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private val noteArchiveAdapter: NotesAdapter by lazy { NotesAdapter() }
    private var actionMode: ActionMode? = null
    private var wrapper: Context? = null
    var isActionMode = false

    companion object {
        const val ACTION_MODE_KEY_ARCHIVE = "ACTION_MODE_KEY_ARCHIVE"
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        if (state != null && state.getBoolean(ACTION_MODE_KEY_ARCHIVE, false)) {
            actionMode = (activity as MainActivity).startSupportActionMode(actionModeCallback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(ACTION_MODE_KEY_ARCHIVE, isActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_archive_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.archive_item_toolbar_multiselect -> {
                        actionMode =
                            (activity as MainActivity).startSupportActionMode(actionModeCallback)
                        noteArchiveAdapter.mViewModel = mNoteViewModel
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(inflater, container, false)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        binding.apply {
            rcViewArchiveNote.apply {
                adapter = noteArchiveAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            registerForContextMenu(rcViewArchiveNote)
            noteArchiveAdapter.onItemClick = { note, _, position ->
                if (actionMode != null) {
                    mNoteViewModel.changeSelectedItem(note)
                    noteArchiveAdapter.notifyItemChanged(position)
                } else {
                    mNoteViewModel.setSelectedNote(note)
                    findNavController().navigate(R.id.action_archiveFragment_to_addFragment)
                }

            }
            noteArchiveAdapter.onItemLongClick = { view, note, _ ->
                mNoteViewModel.setSelectedNote(note)
                showPopupMenuArchiveItem(view)
            }
        }

        mNoteViewModel.selectedItemsFromActionMode.observe(viewLifecycleOwner) { list ->
            actionMode?.title = getString(R.string.selected_items, list.size)
        }
        initThemeObserver()
        initNotesLiveDataObserver()
        return binding.root
    }

    private fun showPopupMenuArchiveItem(view: View) {
        val currentTheme = mNoteViewModel.currentTheme.value
        val popupMenu = CustomPopupMenu(wrapper!!, view, currentTheme)
        val currentNote = mNoteViewModel.selectedNote.value
        popupMenu.showPopupMenuArchive(currentNote!!)

        popupMenu.onChangeItemNoteArchive = {
            mNoteViewModel.setSelectedNote(it)
            DialogIsArchive().show(childFragmentManager, DialogIsArchive.TAG)
            popupMenu.dismiss()
        }
        popupMenu.onChangeItemNoteDelete = {
            DialogDeleteNote().show(childFragmentManager, DialogDeleteNote.TAG)
            popupMenu.dismiss()
        }

    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val currentTheme = mNoteViewModel.currentTheme.value
            if (currentTheme != null) {
                DecoratorView.setColorBackgroundFromActionModeToolbar(requireActivity() as MainActivity,currentTheme)
            }
            isActionMode = true
            if (mode != null) {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.menu_archive_action_mode, menu)
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
                        mNoteViewModel.deleteOrUpdateSelectionItems()
                        dialog.dismiss()
                        mode?.finish()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                    true
                }
                R.id.action_restore_archive -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Do you want delete from archive selected items?")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mNoteViewModel.unarchiveSelectedItems()
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
            mNoteViewModel.clearSelectedItems()
            noteArchiveAdapter.notifyDataSetChanged()
            actionMode = null
            isActionMode = false
        }

    }


    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            val listOfNotesArchive = arrayListOf<Note>()
            listNotes.forEach { note ->
                if (note.isArchive) {
                    listOfNotesArchive.add(note)
                }
            }
            noteArchiveAdapter.submitList(listOfNotesArchive)
        }
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteArchiveAdapter.themeChanged(currentTheme)
        }
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapper = ContextThemeWrapper(context, theme.stylePopupTheme())
    }

    override fun onCloseDialog(isDelete: Boolean, isArchive: Boolean) {
        mNoteViewModel.updateStatusNote(isDelete, isArchive)
    }
}