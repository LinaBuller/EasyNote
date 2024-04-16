package com.buller.mysqlite

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.easynote.domain.utils.DiffUtilEquality


class BaseItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem is DiffUtilEquality) {
            (oldItem as DiffUtilEquality).realEquals(newItem)
        } else oldItem.equals(newItem)
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem is DiffUtilEquality) {
            (oldItem as DiffUtilEquality).realEquals(newItem)
        } else oldItem.equals(newItem)
    }
}