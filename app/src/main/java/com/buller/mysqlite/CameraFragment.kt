package com.buller.mysqlite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class CameraFragment : Fragment() {
    companion object{
        const val TAG = "MyLog"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"Create camera view")
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }
}