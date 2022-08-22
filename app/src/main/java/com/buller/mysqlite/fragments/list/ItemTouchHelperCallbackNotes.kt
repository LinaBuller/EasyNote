package com.buller.mysqlite.fragments.list


import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.viewmodel.NotesViewModel
import java.util.*

class ItemTouchHelperCallbackNotes(
    private val mAdapter: RecyclerView.Adapter<NotesAdapter.MyHolder>,
    val background: ColorDrawable,
    private val deleteIcon: Drawable,
    private val mNoteViewModel:NotesViewModel
) : ItemTouchHelper.Callback() {

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
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition: Int = viewHolder.adapterPosition
        val toPosition: Int = target.adapterPosition

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(
                    (recyclerView.adapter as NotesAdapter).listArray,
                    i,
                    i + 1
                )
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap((recyclerView.adapter as NotesAdapter).listArray, i, i - 1)
            }
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val note = (mAdapter as NotesAdapter).listArray[viewHolder.adapterPosition]
        mNoteViewModel.onNoteSwipe(note)
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
        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
        if (dX > 0) {
            background.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            deleteIcon.setBounds(
                itemView.left + iconMargin,
                itemView.top + iconMargin,
                itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                itemView.bottom - iconMargin
            )
        } else {
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            deleteIcon.setBounds(
                itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                itemView.top + iconMargin,
                itemView.right - iconMargin,
                itemView.bottom - iconMargin
            )
        }

        background.draw(c)
        c.save()

        if (dX > 0) {
            c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
        } else {
            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }

        deleteIcon.draw(c)
        c.restore()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}