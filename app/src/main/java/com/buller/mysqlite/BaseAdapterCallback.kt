package com.buller.mysqlite

import android.view.View

interface BaseAdapterCallback<T> {
    fun onItemClick(model: T, view: View, position: Int)
    fun onLongClick(model: T, view: View): Boolean
}