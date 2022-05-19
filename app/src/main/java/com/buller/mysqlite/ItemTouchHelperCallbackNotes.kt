package com.buller.mysqlite


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallbackNotes(private val mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?,val background:ColorDrawable,val deleteIcon: Drawable) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags,swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val adapter = mAdapter as NotesAdapter
        adapter.onItemMove(viewHolder.adapterPosition,target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val adapter = mAdapter as NotesAdapter
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight)/2
        if (dX>0){
            background.setBounds(itemView.left,itemView.top,dX.toInt(),itemView.bottom)
            deleteIcon.setBounds(
                itemView.left+iconMargin,
            itemView.top+iconMargin,
            itemView.left+iconMargin+deleteIcon.intrinsicWidth,
            itemView.bottom-iconMargin)
        }else{
            background.setBounds(itemView.right+dX.toInt(),itemView.top,itemView.right,itemView.bottom)
            deleteIcon.setBounds(
                itemView.right-iconMargin-deleteIcon.intrinsicWidth,
                itemView.top+iconMargin,
                itemView.right-iconMargin,
                itemView.bottom-iconMargin)
        }

        background.draw(c)
        c.save()

        if (dX>0){
            c.clipRect(itemView.left,itemView.top,dX.toInt(),itemView.bottom)
        }else{
            c.clipRect(itemView.right+dX.toInt(),itemView.top,itemView.right,itemView.bottom)
        }

        deleteIcon.draw(c)
        c.restore()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}