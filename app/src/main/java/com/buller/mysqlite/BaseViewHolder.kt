package com.buller.mysqlite

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.easynote.domain.models.CurrentTheme

abstract class BaseViewHolder<T>(itemView: View):RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T)
    abstract fun changeItemTheme(item: T, currentTheme: CurrentTheme)
}