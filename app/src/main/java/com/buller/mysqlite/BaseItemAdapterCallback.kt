package com.buller.mysqlite

import android.view.View

interface BaseItemAdapterCallback<T> {
    fun onMultiItemClick(model: T, view: View, position: Int, holder:BaseViewHolder<T>)
    fun onMultiItemLongClick(model: T, view: View): Boolean
}