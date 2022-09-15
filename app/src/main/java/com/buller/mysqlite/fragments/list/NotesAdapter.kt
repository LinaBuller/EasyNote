package com.buller.mysqlite.fragments.list

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.edittextnote.EditTextNoteUtil
import kotlin.collections.ArrayList


class NotesAdapter : RecyclerView.Adapter<NotesAdapter.MyHolder>() {
    var listArray = ArrayList<Note>()

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val layoutMin: View? = itemView.findViewById(R.id.lTitle)
        private val layoutBig: View? = itemView.findViewById(R.id.rcItem)

        fun setData(item: Note) {
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

            EditTextNoteUtil.updateFieldsFromColors(
                colorTitle, colorContent,
                tvTitle,
                tvContent,
                layoutMin,
                layoutBig
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item_note, parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val currentNote = listArray[position]
        holder.setData(listArray[position])

        holder.itemView.setOnClickListener { view ->
            if (!currentNote.isDeleted) {
                val bundle = Bundle()
                bundle.putBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE, false)
                bundle.putParcelable(FragmentConstants.UPDATE_NOTE, currentNote)
                view.findNavController().navigate(R.id.action_listFragment_to_addFragment, bundle)
            } else {
                val bundle = Bundle()
                bundle.putBoolean(FragmentConstants.IMAGE_IS_DELETE, true)
                bundle.putParcelable(FragmentConstants.UPDATE_NOTE, currentNote)
                view.findNavController().navigate(R.id.action_recycleBinFragment_to_addFragment, bundle)
            }
        }

    }

    override fun getItemCount(): Int = listArray.size

    //обновляет список
    fun submitList(listItems: List<Note>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }
}