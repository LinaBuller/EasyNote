package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ColorPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    lateinit var fragment1: Fragment
    lateinit var fragment2: Fragment

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                fragment1 = ColorPikerBackgroundFragment(0)
                return fragment1
            }

            1 -> {
                fragment2 = ColorPikerBackgroundFragment(1)
                return fragment2
            }

            else -> {
                fragment1 = ColorPikerBackgroundFragment(0)
                return fragment1
            }
        }
    }

}