package com.buller.mysqlite

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.allViews
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.ReorderableEntity

abstract class BaseReorderableAdapter<TItem : ReorderableEntity> :
    ListAdapter<TItem, BaseViewHolder<TItem>>(BaseItemCallback<TItem>()),
    ItemMoveCallback.ItemTouchHelperContract {
    private var mCallback: BaseItemAdapterCallback<TItem>? = null
    private var currentThemeAdapter: CurrentTheme? = null
    private var lastPosition = -1

    fun attachCallback(callback: BaseItemAdapterCallback<TItem>) {
        this.mCallback = callback
    }

    fun detachCallback() {
        this.mCallback = null
    }

    override fun onBindViewHolder(holder: BaseViewHolder<TItem>, position: Int) {
        holder.bind(currentList[position])

        holder.itemView.allViews.forEach {
            it.setOnClickListener {
                mCallback?.onMultiItemClick(currentList[position], holder.itemView, position, holder)
            }
        }


        holder.itemView.setOnLongClickListener {
            if (mCallback == null) {
                false
            } else {
                mCallback!!.onMultiItemLongClick(currentList[position], holder.itemView)
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

    fun getTheme(): CurrentTheme? {
        return currentThemeAdapter
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val list = currentList.toMutableList()
        val oldItem = list.removeAt(fromPosition)
        list.add(toPosition, oldItem)

        val max: Int = maxOf(fromPosition, toPosition)
        val min: Int = minOf(fromPosition, toPosition)
        for (i in min..max) {
            list[i].position = i
        }
        submitList(list)
    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {
        currentThemeAdapter?.let {
            (myViewHolder as ReorderableHolder<*>).changeItemRow(it)
        }
    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {
        currentThemeAdapter?.let {
            (myViewHolder as ReorderableHolder<*>).changeItemClear(it)
        }
    }

    abstract class ReorderableHolder<T>(itemView: View) : BaseViewHolder<T>(itemView) {
        abstract fun changeItemRow(currentTheme: CurrentTheme)
        abstract fun changeItemClear(currentTheme: CurrentTheme)
    }
}