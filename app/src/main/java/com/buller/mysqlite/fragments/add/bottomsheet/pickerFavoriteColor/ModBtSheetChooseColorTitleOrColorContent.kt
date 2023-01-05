package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.buller.mysqlite.databinding.DialogModalBottomSheetLobsterpickerBinding
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ModBtSheetChooseColorTitleOrColorContent : BottomSheetDialogFragment() {
    private lateinit var binding: DialogModalBottomSheetLobsterpickerBinding
    private lateinit var mNoteViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogModalBottomSheetLobsterpickerBinding.inflate(inflater, container, false)

        initTabLayout()

        binding.apply {
            saveSelectedColors.setOnClickListener {
                mNoteViewModel.updateCurrentFieldColor()
                dismiss()
            }
//            refreshSelectedColors.setOnClickListener {
//                mNoteViewModel.updateEditedFieldColor()
//            }
        }

        return binding.root
    }

    fun initTabLayout()= with(binding)  {
        val tabLayout = tabLayout
        val viewPager = viewPager
        //tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = ColorPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.isUserInputEnabled = false
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Title"

            } else if (position == 1) {
                tab.text = "Text"
            }
        }.attach()
    }
    companion object {
        const val TAG = "ModalBottomSheetLobster-picker"
    }

}