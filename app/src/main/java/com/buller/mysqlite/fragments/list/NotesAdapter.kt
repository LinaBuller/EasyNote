package com.buller.mysqlite.fragments.list

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.Note

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteHolder>() {
    private var currentThemeAdapter: CurrentTheme? = null
    var onItemClick: ((Note, View, Int) -> Unit)? = null
    var onItemLongClick: ((View, Note, Int) -> Unit)? = null
    val differ = AsyncListDiffer(this, callback)
    var onItemActionMode: ((NoteHolder, Note) -> Unit)? = null

    companion object {
        val callback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                val content = oldItem.content == newItem.content
                val title = oldItem.title == newItem.title
                val colorTitle = oldItem.gradientColorFirst == newItem.gradientColorFirst
                val colorContent = oldItem.gradientColorSecond == newItem.gradientColorSecond
                val time = oldItem.time == newItem.time
                val isArchive = oldItem.isArchive == newItem.isArchive
                val isDelete = oldItem.isDeleted == newItem.isDeleted
                val isFavorite = oldItem.isFavorite == newItem.isFavorite
                val isPin = oldItem.isPin == newItem.isPin

                return content &&
                        title &&
                        colorTitle &&
                        colorContent &&
                        time &&
                        isArchive &&
                        isDelete &&
                        isFavorite &&
                        isPin
            }
        }
    }

    inner class NoteHolder(
        itemView: View,
        var context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val layoutMin: CardView? = itemView.findViewById(R.id.titleCardView)
        val layoutBig: CardView? = itemView.findViewById(R.id.rcItem)
        val iconPin: ImageView = itemView.findViewById(R.id.imVIconPin)
        val iconFavorite: ImageView = itemView.findViewById(R.id.imVIconFavorite)
        val layoutNoteItem: ConstraintLayout = itemView.findViewById(R.id.layoutNoteItem)

        init {

            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[adapterPosition], itemView, adapterPosition)
            }
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(
                    itemView,
                    differ.currentList[adapterPosition],
                    adapterPosition
                )
                return@setOnLongClickListener true
            }
        }

        fun setData(item: Note) {
            tvTitle.text = item.title

            if (item.content == "") {
                tvContent.visibility = View.GONE
            }

            tvContent.text =
                Html.fromHtml(item.content, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            tvContent.text.trimEnd('\n')

            tvTime.text = item.time

            if (item.isPin) {
                iconPin.visibility = View.VISIBLE
            } else {
                iconPin.visibility = View.GONE
            }

            if (item.isFavorite) {
                iconFavorite.visibility = View.VISIBLE
            } else {
                iconFavorite.visibility = View.GONE
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteHolder(
            inflater.inflate(R.layout.rc_item_note, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentNote = differ.currentList[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        holder.apply {
            setData(currentNote)
            changeItemFromCurrentTheme(currentNote, currentThemeId, holder)
        }
        onItemActionMode?.invoke(holder, currentNote)
        setAnimation(holder.itemView, position, holder.context)
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(newDataList: List<Note>) {
        differ.submitList(newDataList)
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
        holder: NoteHolder
    ) {
        if (holder.layoutMin != null) {
            if (currentNote.gradientColorFirst == 0) {
                DecoratorView.changeBackgroundCardView(
                    currentThemeId,
                    holder.layoutMin,
                    holder.context
                )
                DecoratorView.changeBackgroundText(currentThemeId, holder.tvTitle, holder.context)
            } else {
                DecoratorView.changeBackgroundToCurrentNoteTitleCardView(
                    currentThemeId,
                    currentNote,
                    holder.layoutMin,
                    holder.context
                )
                DecoratorView.changeBackgroundToCurrentNoteTextView(
                    currentNote.gradientColorFirst,
                    holder.tvTitle
                )
            }
        }

        if (holder.layoutBig != null) {
            if (currentNote.gradientColorSecond == 0) {
                DecoratorView.changeBackgroundCardView(
                    currentThemeId,
                    holder.layoutBig,
                    holder.context
                )
                DecoratorView.changeBackgroundText(currentThemeId, holder.tvContent, holder.context)
            } else {
                DecoratorView.changeBackgroundToCurrentNoteContentCardView(
                    currentThemeId,
                    currentNote,
                    holder.layoutBig,
                    holder.context
                )
                DecoratorView.changeBackgroundToCurrentNoteTextView(
                    currentNote.gradientColorSecond,
                    holder.tvContent
                )
            }
        }

        DecoratorView.changeText(currentThemeId, holder.tvTitle, holder.context)
        DecoratorView.changeText(currentThemeId, holder.tvContent, holder.context)
        DecoratorView.changeCommentText(currentThemeId, holder.tvTime, holder.context)
        DecoratorView.changeItemsBackground(currentThemeId, holder.layoutNoteItem, holder.context)
    }
}