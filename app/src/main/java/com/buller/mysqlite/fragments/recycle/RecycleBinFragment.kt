package com.buller.mysqlite.fragments.recycle

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentRecycleBinBinding
import com.buller.mysqlite.dialogs.DialogDeleteImage
import com.buller.mysqlite.fragments.list.ItemTouchHelperCallbackNotes
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment
import com.google.android.material.snackbar.Snackbar

class RecycleBinFragment :ThemeFragment() {
    private lateinit var binding: FragmentRecycleBinBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private  val noteAdapter: NotesAdapter by lazy { NotesAdapter() }
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelper: ItemTouchHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar_recyclerbin_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.deleteAll -> {
                        mNoteViewModel.readAllNotes.value?.forEach { note ->
                            if (note.isDeleted) {
                                mNoteViewModel.deleteNote(note)
                            }
                        }
                        return true
                    }
                    else -> return false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
    }
    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            noteAdapter.themeChanged(currentTheme)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]

        binding.apply {
            rcViewDeletedNote.apply {
                adapter = noteAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        initThemeObserver()
        initTouchHelper()
        touchHelper.attachToRecyclerView(binding.rcViewDeletedNote)
        undoEvent()
        initNotesLiveDataObserver()
        return binding.root
    }

    private fun undoEvent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mNoteViewModel.noteEvent.collect { event ->
                when (event) {
                    is NotesViewModel.NoteEvent.ShowUndoRestoreNoteMessage -> {
                        Snackbar.make(requireView(), "Restore this note", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                mNoteViewModel.onUndoClickNote(event.note)
                            }.show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun initTouchHelper() {

        val swipeBackground = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                resources.getColor(R.color.green_undelete, null),
                resources.getColor(R.color.green_undelete, null)
            )
        )
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_restore)!!
        callbackNotes =
            ItemTouchHelperCallbackNotes(noteAdapter, swipeBackground, deleteIcon, mNoteViewModel)
        touchHelper = ItemTouchHelper(callbackNotes)
    }

    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            val listOfNoteDelete = arrayListOf<Note>()
            listNotes.forEach { note ->
                if (note.isDeleted) {
                    listOfNoteDelete.add(note)
                }
            }
            noteAdapter.submitList(listOfNoteDelete)
        }
    }
}