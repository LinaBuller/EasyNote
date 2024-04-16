package com.buller.mysqlite.fragments.list

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.buller.mysqlite.BaseAdapter
import com.buller.mysqlite.BaseViewHolder
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.Note

class NoteAdapter : BaseAdapter<Note>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Note> {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(inflater.inflate(R.layout.rc_item_note, parent, false))
    }

    class NoteViewHolder(itemView: View) : BaseViewHolder<Note>(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        private val layoutMin: CardView = itemView.findViewById(R.id.titleCardView)
        private val layoutBig: CardView = itemView.findViewById(R.id.rcItem)
        private val iconPin: ImageView = itemView.findViewById(R.id.imVIconPin)
        private val iconFavorite: ImageView = itemView.findViewById(R.id.imVIconFavorite)
        private val layoutNoteItem: ConstraintLayout = itemView.findViewById(R.id.layoutNoteItem)

        override fun bind(item: Note) {

            if (item.title.isNotBlank()) {
                tvTitle.text = item.title
            } else {
                tvTitle.text = "Not title"
            }

            if (item.content == "") {
                tvContent.visibility = View.GONE
            }

            tvContent.text =
                Html.fromHtml(item.content, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            tvContent.text.trimEnd('\n')

            tvTime.text = item.lastChangedTime

            if (item.isPin) {
                iconPin.visibility = View.VISIBLE
            } else {
                iconPin.visibility = View.INVISIBLE
            }

            if (item.isFavorite) {
                iconFavorite.visibility = View.VISIBLE
            } else {
                iconFavorite.visibility = View.INVISIBLE
            }
        }

        override fun changeItemTheme(
            item: Note,
            currentTheme: CurrentTheme
        ) {
            if (item.gradientColorFirst == 0) {
                DecoratorView.changeBackgroundCardView(
                    currentTheme,
                    layoutMin,
                    itemView.context
                )
                DecoratorView.changeBackgroundText(currentTheme, tvTitle, itemView.context)
            } else {
                DecoratorView.changeBackgroundToCurrentNoteTitleCardView(
                    currentTheme, item, layoutMin, itemView.context
                )
                DecoratorView.changeBackgroundToCurrentNoteTextView(item.gradientColorFirst, tvTitle)
            }

            if (item.gradientColorSecond == 0) {
                DecoratorView.changeBackgroundCardView(
                    currentTheme,
                    layoutBig,
                    itemView.context
                )
                DecoratorView.changeBackgroundText(currentTheme, tvContent, itemView.context)
            } else {
                DecoratorView.changeBackgroundToCurrentNoteContentCardView(
                    currentTheme, item, layoutBig, itemView.context
                )
                DecoratorView.changeBackgroundToCurrentNoteTextView(item.gradientColorSecond, tvContent)
            }

            DecoratorView.changeText(currentTheme, tvTitle, itemView.context)
            DecoratorView.changeText(currentTheme, tvContent, itemView.context)
            DecoratorView.changeCommentText(currentTheme, tvTime, itemView.context)
            DecoratorView.changeItemsBackground(currentTheme, layoutNoteItem, itemView.context)
        }
    }
}