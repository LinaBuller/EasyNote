package com.buller.mysqlite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.FragmentScrollingBinding
import com.buller.mysqlite.model.Note

class ScrollingFragment() : Fragment() {
    private var binding: FragmentScrollingBinding? = null
    val KEY_FRAGMENT = "filter_screen"
    var listNoteItemsFromFilter = arrayListOf<Note>()

    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScrollingBinding.inflate(inflater, container, false)
        listNoteItemsFromFilter = requireArguments().getParcelableArrayList(
            ContentConstants.LIST_SELECTED_NOTE_FROM_DATE)!!
        return binding!!.root
    }

    companion object {
        const val TAG = "FilterFragment"
    }
}