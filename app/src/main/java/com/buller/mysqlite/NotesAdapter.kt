package com.buller.mysqlite

import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.db.NoteItem
import com.buller.mysqlite.db.MyDbManager


class NotesAdapter(listMain: ArrayList<NoteItem>, contextMainActivity: Context) :
    RecyclerView.Adapter<NotesAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextMainActivity

    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        private val layoutBig: View? = itemView.findViewById(R.id.rcItem)
        private val layoutMin: View? = itemView.findViewById(R.id.lTitle)

        private val context = contextV

        fun setData(item: NoteItem) {
            val colorTitle = item.colorFrameTitle
            val colorContent = item.colorFrameContent

            tvTitle.text = item.title

            if (item.content == "") {
                tvContent.visibility = View.GONE
            }

            tvContent.text =
                Html.fromHtml(item.content, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            tvContent.text.trimEnd('\n')

            tvTime.text = item.time

            if (colorTitle != 0) {
                layoutMin!!.background.mutate()
                layoutMin!!.background.setTint(colorTitle)
            } else {
                layoutMin!!.setBackgroundResource( R.drawable.rounded_border_rcview_item)
            }

            if (colorContent != 0) {
                layoutBig!!.background.mutate()
                layoutBig!!.background.setTint(colorContent)
            } else {

                layoutBig!!.setBackgroundResource(R.drawable.rounded_border_rcview_item)
            }

            tvContent.setOnClickListener {
                if (tvContent.maxLines != Int.MAX_VALUE) {
                    tvContent.maxLines = Int.MAX_VALUE
                } else {
                    tvContent.maxLines = 3
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(ContentConstants.I_TITLE_KEY, item.title)
                    putExtra(ContentConstants.I_CONTENT_KEY, item.content)
                    putExtra(ContentConstants.I_ID_KEY, item.id)
                    putExtra(ContentConstants.I_TIME, item.time)
                    putExtra(ContentConstants.COLOR_TITLE_FRAME, item.colorFrameTitle)
                    putExtra(ContentConstants.COLOR_CONTENT_FRAME, item.colorFrameContent)
                    putExtra(ContentConstants.EDIT_CHOOSE, true)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item_note, parent, false), context)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray.get(position))
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    //обновляет список
    fun updateAdapter(listItems: List<NoteItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    //Удаляет элемент
    fun removeItem(position: Int, dbManager: MyDbManager) {
        dbManager.removeItemDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(position)
    }
}