package com.buller.mysqlite.fragments.list

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Note
import com.buller.mysqlite.utils.edittextnote.EditTextNoteUtil
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.MyHolder>() {
    var listArray = ArrayList<Note>()
    private var currentThemeAdapter: CurrentTheme? = null

    inner class MyHolder(
        itemView: View,
        var context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val layoutMin: CardView? = itemView.findViewById(R.id.titleCardView)
        val layoutBig: CardView? = itemView.findViewById(R.id.rcItem)

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
                null,
                null,
                layoutMin,
                layoutBig, context
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(
            inflater.inflate(R.layout.rc_item_note, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val currentNote = listArray[position]
        val currentThemeId = currentThemeAdapter!!.themeId

        holder.apply {
            setData(listArray[position])
            changeItemFromCurrentTheme(currentNote, currentThemeId, holder.context, holder)
            itemView.setOnClickListener { view ->
                if (!currentNote.isDeleted) {
                    val bundle = Bundle()
                    bundle.putBoolean(FragmentConstants.NEW_NOTE_OR_UPDATE, false)
                    bundle.putParcelable(FragmentConstants.UPDATE_NOTE, currentNote)
                    view.findNavController()
                        .navigate(R.id.action_listFragment_to_addFragment, bundle)
                } else {
                    val bundle = Bundle()
                    bundle.putBoolean(FragmentConstants.IMAGE_IS_DELETE, true)
                    bundle.putParcelable(FragmentConstants.UPDATE_NOTE, currentNote)
                    view.findNavController()
                        .navigate(R.id.action_recycleBinFragment_to_addFragment, bundle)
                }
            }
        }

        setAnimation(holder.itemView, position, holder.context)
    }

    override fun getItemCount(): Int = listArray.size

    //обновляет список
    fun submitList(listItems: List<Note>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    private var lastPosition = -1

    private fun setAnimation(view: View, position: Int, context: Context) {
        if (position < lastPosition) {
            val anim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            view.startAnimation(anim)
            lastPosition = position
        }
    }


    fun themeChanged(currentTheme: CurrentTheme) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    private fun changeItemFromCurrentTheme(
        currentNote: Note,
        currentThemeId: Int,
        context: Context,
        holder: MyHolder
    ) {
        if (holder.layoutMin != null) {
            if (currentNote.colorFrameTitle == 0) {
                DecoratorView.changeBackgroundCardView(currentThemeId, holder.layoutMin, context)
            } else {
                DecoratorView.changeBackgroundToCurrentNoteTitleCardView(currentThemeId,currentNote, holder.layoutMin, context)
            }
        }

        if (holder.layoutBig != null) {
            if (currentNote.colorFrameContent == 0) {
                DecoratorView.changeBackgroundCardView(currentThemeId, holder.layoutBig, context)
            } else {
                DecoratorView.changeBackgroundToCurrentNoteContentCardView(currentThemeId,currentNote, holder.layoutBig, context)
            }
        }

        DecoratorView.changeText(currentThemeId, holder.tvTitle, context)
        DecoratorView.changeText(currentThemeId, holder.tvContent, context)
        DecoratorView.changeCommentText(currentThemeId, holder.tvTime, context)
    }

}