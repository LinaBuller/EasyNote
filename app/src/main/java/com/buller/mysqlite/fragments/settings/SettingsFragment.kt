package com.buller.mysqlite.fragments.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.list.ListFragment

class SettingsFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        Log.d(ListFragment.TAG, "DeveloperFragment onResume")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}