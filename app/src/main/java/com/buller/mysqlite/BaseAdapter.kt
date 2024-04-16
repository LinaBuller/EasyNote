package com.buller.mysqlite

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ListAdapter
import com.easynote.domain.models.CurrentTheme

abstract class BaseAdapter<T : Any> : ListAdapter<T, BaseViewHolder<T>>(BaseItemCallback<T>()) {
    private var mCallback: BaseAdapterCallback<T>? = null
    private var lastPosition = -1
    private var currentThemeAdapter: CurrentTheme? = null

    fun attachCallback(callback: BaseAdapterCallback<T>) {
        this.mCallback = callback
    }

    fun detachCallback() {
        this.mCallback = null
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(currentList[position])

        holder.itemView.setOnClickListener {
            mCallback?.onItemClick(currentList[position], holder.itemView,position)
        }

        holder.itemView.setOnLongClickListener {
            if (mCallback == null) {
                false
            } else {
                mCallback!!.onLongClick(currentList[position], holder.itemView)
            }
        }
        setAnimation(holder.itemView, position, holder.itemView.context)

        currentThemeAdapter?.let {
            holder.changeItemTheme(currentList[position], it)
        }
    }

    override fun getItemCount(): Int = currentList.size

    private fun setAnimation(view: View, position: Int, context: Context) {
        if (position < lastPosition) {
            val anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            view.startAnimation(anim)
            lastPosition = position
        }
    }

    fun setTheme(currentTheme: CurrentTheme) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }
}

