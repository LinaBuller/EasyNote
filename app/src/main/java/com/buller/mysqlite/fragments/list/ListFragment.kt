package com.buller.mysqlite.fragments.list

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Note
import com.google.android.material.snackbar.Snackbar


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var noteAdapter: NotesAdapter
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelper: ItemTouchHelper

    companion object {
        const val TAG = "MyLog"
    }

    private val menuItemClickListener = object : Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if (item != null) {
                when (item.itemId) {
                    R.id.sortAZ -> {
                        Toast.makeText(requireContext(), "sortAZ", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.sortZA -> {
                        Toast.makeText(requireContext(), "sortZA", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    R.id.sort_newest_oldest -> {
                        return true
                    }
                    R.id.sort_oldest_newest -> {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "ListFragment onCreateView")
        binding = FragmentListBinding.inflate(inflater, container, false)

        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
        noteAdapter = NotesAdapter()
        binding.apply {
            rcView.apply {
                adapter = noteAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        initTouchHelper()
        touchHelper.attachToRecyclerView(binding.rcView)
        undoEvent()
        initNotesLiveDataObserver()

        binding.btAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE, true)
            findNavController().navigate(R.id.action_listFragment_to_addFragment, bundle)
        }

        return binding.root
    }

    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            val listOfNoteNotDelete = arrayListOf<Note>()
            listNotes.forEach { note ->
                if (!note.isDeleted){
                    listOfNoteNotDelete.add(note)
                }
            }
            noteAdapter.submitList(listOfNoteNotDelete)
        }
    }

    private fun initTouchHelper() {
        val swipeBackground = ColorDrawable(resources.getColor(R.color.akcient2, null))
        val deleteIcon: Drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        callbackNotes =
            ItemTouchHelperCallbackNotes(noteAdapter, swipeBackground, deleteIcon, mNoteViewModel)
        touchHelper = ItemTouchHelper(callbackNotes)
    }

    private fun undoEvent() {
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