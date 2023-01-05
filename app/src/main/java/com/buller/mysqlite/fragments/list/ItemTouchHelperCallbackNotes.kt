package com.buller.mysqlite.fragments.list


import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.viewmodel.NotesViewModel


class ItemTouchHelperCallbackNotes(
    private val mAdapter: RecyclerView.Adapter<NotesAdapter.MyHolder>,
    val background: GradientDrawable,
    private val deleteIcon: Drawable,
    private val mNoteViewModel: NotesViewModel
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
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

//    override fun getMovementFlags(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder
//    ): Int {
//        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
//        return makeMovementFlags(dragFlags, swipeFlags)
//    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ): Boolean {
//        val fromPosition: Int = viewHolder.adapterPosition
//        val toPosition: Int = target.adapterPosition
//
//        if (fromPosition < toPosition) {
//            for (i in fromPosition until toPosition) {
//                Collections.swap(
//                    (recyclerView.adapter as NotesAdapter).listArray,
//                    i,
//                    i + 1
//                )
//            }
//        } else {
//            for (i in fromPosition downTo toPosition + 1) {
//                Collections.swap((recyclerView.adapter as NotesAdapter).listArray, i, i - 1)
//            }
//        }
//        return true
//    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val note = (mAdapter as NotesAdapter).listArray[position]
        note.isDeleted = !note.isDeleted
        mNoteViewModel.onNoteSwipe(note)
        mNoteViewModel.updateNote(note)
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
        val direction = if (dX > 0) DIRECTION_RIGHT else DIRECTION_LEFT

        when (direction) {
            DIRECTION_RIGHT -> {

                background.setBounds(
                    itemView.left + 20,
                    itemView.top,
                    dX.toInt() + 100,
                    itemView.bottom
                )
                background.cornerRadii = floatArrayOf(50f, 50f, 0f, 0f, 0f, 0f, 50f, 50f)

                deleteIcon.setBounds(
                    itemView.left + iconMargin,
                    itemView.top + iconMargin,
                    itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMargin
                )
            }
            DIRECTION_LEFT -> {

                background.setBounds(
                    itemView.right + dX.toInt() - 100,
                    itemView.top,
                    itemView.right - 20,
                    itemView.bottom
                )
                background.cornerRadii = floatArrayOf(0f, 0f, 50f, 50f, 50f, 50f, 0f, 0f)

                deleteIcon.setBounds(
                    itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                    itemView.top + iconMargin,
                    itemView.right - iconMargin,
                    itemView.bottom - iconMargin
                )
            }
        }



        background.draw(c)

        c.save()

        if (dX > 0) {
            c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            //c.clipRect(itemView.left + 20, itemView.top, dX.toInt() + 100, itemView.bottom)
        } else {
            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            //c.clipRect(
//                itemView.right + dX.toInt() - 100,
//                itemView.top,
//                itemView.right - 20,
//                itemView.bottom
//            )
        }

        deleteIcon.draw(c)
        c.restore()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    companion object {
        const val DIRECTION_RIGHT = 1
        const val DIRECTION_LEFT = 0
    }
}