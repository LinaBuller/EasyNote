package com.buller.mysqlite.fragments.categories

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.fragments.add.ImageAdapter
import com.buller.mysqlite.fragments.add.multiadapter.MultiItemAdapter


class ItemMoveCallback(private val mAdapter: ItemTouchHelperContract) : ItemTouchHelper.Callback() {

    private var mDraggable: Boolean = true

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        return if (mDraggable){
            val dragFlags = UP or DOWN or END or START
            makeMovementFlags(dragFlags, 0)
        }else{
            0
        }
    }

    fun setDraggable(value: Boolean) {
        mDraggable = value
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

        if (viewHolder is CategoryAdapter.CategoryHolder) {
            val myViewHolder: CategoryAdapter.CategoryHolder = viewHolder
            mAdapter.onRowSelected(myViewHolder)
        }
        if (viewHolder is MultiItemAdapter.MultiHolder) {
            val myViewHolder: MultiItemAdapter.MultiHolder = viewHolder
            mAdapter.onRowSelected(myViewHolder)
        }

        if (viewHolder is ImageAdapter.ImageViewHolder) {
            val myViewHolder: ImageAdapter.ImageViewHolder = viewHolder
            mAdapter.onRowSelected(myViewHolder)
        }

        super.onSelectedChanged(viewHolder, actionState)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is CategoryAdapter.CategoryHolder) {
            val myViewHolder: CategoryAdapter.CategoryHolder = viewHolder
            mAdapter.onRowClear(myViewHolder)
        }
        if (viewHolder is MultiItemAdapter.MultiHolder) {
            val myViewHolder: MultiItemAdapter.MultiHolder = viewHolder
            mAdapter.onRowClear(myViewHolder)
        }
        if (viewHolder is ImageAdapter.ImageViewHolder) {
            val myViewHolder: ImageAdapter.ImageViewHolder = viewHolder
            mAdapter.onRowClear(myViewHolder)
        }
    }

    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?)
        fun onRowClear(myViewHolder: RecyclerView.ViewHolder?)
    }

}