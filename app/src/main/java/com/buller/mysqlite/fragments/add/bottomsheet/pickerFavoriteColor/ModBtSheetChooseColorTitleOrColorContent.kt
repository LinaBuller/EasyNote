package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.buller.mysqlite.databinding.DialogModalBottomSheetLobsterpickerBinding
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.utils.theme.ThemeBottomSheetFragment
import com.easynote.domain.viewmodels.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.google.android.material.tabs.TabLayoutMediator


class ModBtSheetChooseColorTitleOrColorContent :ThemeBottomSheetFragment() {
    private lateinit var binding: DialogModalBottomSheetLobsterpickerBinding
    private val mNoteViewModel: NotesViewModel by activityViewModels()

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme

        binding.apply {
            designBottomSheet.background.setTintList(ColorStateList.valueOf(theme.backgroundColor(requireContext())))
            tabLayout.setBackgroundColor(theme.backgroundColor(requireContext()))
            tabLayout.setTabTextColors(theme.textColorTabSelect(requireContext()),theme.textColorTabUnselect(requireActivity()))
            tabLayout.setSelectedTabIndicatorColor(theme.akcColor(requireContext()))
            saveSelectedColors.setColorFilter(theme.akcColor(requireContext()))
            saveSelectedColors.background.setTintList(ColorStateList.valueOf(theme.backgroundDrawer(requireContext())))
        }
        if(dialog!=null){
            dialog!!.window!!.navigationBarColor = theme.setStatusBarColor(requireContext())
        }
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

    fun initTabLayout() = with(binding) {
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