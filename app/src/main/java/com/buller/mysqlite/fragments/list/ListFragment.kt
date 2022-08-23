package com.buller.mysqlite.fragments.list

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.R
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.buller.mysqlite.databinding.FragmentListBinding
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.google.android.material.snackbar.Snackbar


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var noteAdapter: NotesAdapter
    private lateinit var callbackNotes: ItemTouchHelperCallbackNotes
    private lateinit var touchHelper: ItemTouchHelper

    companion object{
        const val TAG = "MyLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"ListFragment onCreate")
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"ListFragment onAttach")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"ListFragment onDestroyView")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,"ListFragment onPause")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG,"ListFragment onResume")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG,"ListFragment onStop")
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"ListFragment onStart")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"ListFragment onDestroy")
    }
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"ListFragment onDetach")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"ListFragment onCreateView")
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
            bundle.putBoolean(FragmentConstants.OPEN_NEW_OR_UPDATE_NOTE, true)
            findNavController().navigate(R.id.action_listFragment_to_addFragment, bundle)
        }

        return binding.root
    }

    private fun initNotesLiveDataObserver() {
        mNoteViewModel.readAllNotes.observe(viewLifecycleOwner) { listNotes ->
            noteAdapter.submitList(listNotes)
        }
    }

    private fun initTouchHelper() {
        val swipeBackground = ColorDrawable(resources.getColor(R.color.akcient2, null))
        val deleteIcon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
        callbackNotes = ItemTouchHelperCallbackNotes(noteAdapter, swipeBackground, deleteIcon,mNoteViewModel)
        touchHelper = ItemTouchHelper(callbackNotes)
    }

    private fun undoEvent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mNoteViewModel.noteEvent.collect { event ->
                when (event) {
                    is NotesViewModel.NoteEvent.ShowUndoDeleteNoteMessage -> {
                        Snackbar.make(requireView(), "Note deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                mNoteViewModel.onUndoDeleteClick(event.note)
                            }.show()
                    }
                    else -> {}
                }

            }
        }
    }
}