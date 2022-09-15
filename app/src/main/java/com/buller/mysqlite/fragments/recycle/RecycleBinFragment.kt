package com.buller.mysqlite.fragments.recycle

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentRecycleBinBinding
import com.buller.mysqlite.fragments.list.ItemTouchHelperCallbackNotes
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.google.android.material.snackbar.Snackbar

class RecycleBinFragment : Fragment() {
    private lateinit var binding: FragmentRecycleBinBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var noteAdapter: NotesAdapter
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelper: ItemTouchHelper

    private val menuItemClickListener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (item != null) {
                when (item.itemId) {
                    //сортировать по дате удаления
                    //сортировать по дате создания
                    //сортировать по дате последнего редактирования
                    //сортировать по алфавиту
                    R.id.deleteAll -> {
                        mNoteViewModel.readAllNotes.value?.forEach { note ->
                            if (note.isDeleted) {
                                mNoteViewModel.deleteNote(note)
                            }
                        }
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
        //поменять меню в фрагменте
        (requireActivity() as MainActivity).setToolbarMenu(
            R.menu.menu_toolbar_recyclerbin_fragment,
            menuItemClickListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        noteAdapter = NotesAdapter()
        binding.apply {
            rcViewDeletedNote.apply {
                adapter = noteAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
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
        val swipeBackground = ColorDrawable(resources.getColor(R.color.restoreNote, null))
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