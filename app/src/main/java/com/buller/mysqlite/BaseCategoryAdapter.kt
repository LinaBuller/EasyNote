package com.buller.mysqlite

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ListAdapter
import com.easynote.domain.models.CurrentTheme

abstract class BaseCategoryAdapter<T : Any> :
    ListAdapter<T, BaseViewHolder<T>>(BaseItemCallback<T>()) {
    private var mCallback: BaseCategoryAdapterCallback<T>? = null
    private var currentThemeAdapter: CurrentTheme? = null
    private var lastPosition = -1


    fun attachCallback(callback: BaseCategoryAdapterCallback<T>) {
        this.mCallback = callback
    }

    fun detachCallback() {
        this.mCallback = null
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(currentList[position])

        holder.itemView.setOnClickListener {
            mCallback?.onItemCategoryClick(
                currentList[position],
                holder.itemView,
                position
            )
        }

        holder.itemView.setOnLongClickListener {
            if (mCallback == null) {
                false
            } else {
                mCallback!!.onItemCategoryLongClick(currentList[position], holder.itemView)
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