package com.buller.mysqlite.fragments.add

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.DragEvent
import androidx.appcompat.view.ActionMode
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.BaseReorderableAdapter
import com.buller.mysqlite.BaseViewHolder
import com.buller.mysqlite.CustomUnderlineSpan
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.TextItem


class MultiAdapter(private val onDragImage: OnDragImageToAnotherImageItem) :
    BaseReorderableAdapter<MultiItem>() {
    private var actionMode: ActionMode? = null
    var getTextWatcher: ((TextItem) -> TextWatcher?)? = null
    val listTouchHelper = arrayListOf<ItemTouchHelper>()
//    var getIsUserChangeText: (() -> Boolean)? = null
//    var setCommandReplaceText: ((CommandReplaceText) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<MultiItem> {
        when (viewType) {
            TYPE_TEXT -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_from_bord, parent, false)
                return TextHolder(textView)
            }

            TYPE_IMAGE -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_from_bord, parent, false)
                return ImageHolder(textView)
            }

            else -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_from_bord, parent, false)
                return TextHolder(textView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (currentList[position]) {
            is TextItem -> {
                return TYPE_TEXT
            }

            is ImageItem -> {
                return TYPE_IMAGE
            }

            else -> {
                return 0
            }
        }
    }

    inner class TextHolder(itemView: View) : ReorderableHolder<MultiItem>(itemView) {

        private var watcher: TextWatcher? = null
        val editText: EditText = itemView.findViewById(R.id.etTextItem)
        val dragIcon: ImageView = itemView.findViewById(R.id.ivDrag)
        val layoutTextItem: ConstraintLayout = itemView.findViewById(R.id.layoutTextItem)

        override fun bind(item: MultiItem) {
            editText.removeTextChangedListener(watcher)
            setSavedTextToTextItem((item as TextItem).text, editText)

            if (actionMode != null) {
                editText.disableInput()
                dragIcon.visibility = View.VISIBLE
            } else {
                editText.enableInput()
                setDragFromTextItem(editText)
                dragIcon.visibility = View.GONE
                itemView.isActivated = false
                watcher = getTextWatcher?.invoke(item)
                editText.addTextChangedListener(watcher)
            }
        }

        override fun changeItemTheme(item: MultiItem, currentTheme: CurrentTheme) {
            DecoratorView.changeText(currentTheme, editText, itemView.context)
            DecoratorView.changeImageView(currentTheme, dragIcon, itemView.context)
            DecoratorView.changeItemsBackground(
                currentTheme,
                layoutTextItem,
                itemView.context
            )
        }

        override fun changeItemRow(currentTheme: CurrentTheme) {
            DecoratorView.changeText(currentTheme, editText, itemView.context)
            DecoratorView.changeImageView(currentTheme, dragIcon, itemView.context)
            DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                currentTheme,
                layoutTextItem,
                itemView.context
            )
        }

        override fun changeItemClear(currentTheme: CurrentTheme) {
            DecoratorView.changeItemsBackground(
                currentTheme,
                layoutTextItem,
                itemView.context
            )
        }

    }

    inner class ImageHolder(itemView: View) : ReorderableHolder<MultiItem>(itemView) {
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.rcImageView)
        private val layoutImageItem: ConstraintLayout = itemView.findViewById(R.id.layoutImageItem)
        private val dragIcon: ImageView = itemView.findViewById(R.id.ivDrag)

        @SuppressLint("ClickableViewAccessibility")
        override fun bind(item: MultiItem) {
            val adapterImage = ImageAdapter(item as ImageItem, onDragImage)
            childRecyclerView.layoutManager = AutoFitGridLayoutManager(itemView.context, 400)
            childRecyclerView.adapter = adapterImage
            adapterImage.submitList(item.listImageItems)

            if (actionMode != null) {

                listTouchHelper.forEach {
                    it.attachToRecyclerView(null)
                }

                dragIcon.visibility = View.VISIBLE


                childRecyclerView.setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.performClick()
                    } else {
                        false
                    }
                }
                adapterImage.getActionMode = { actionMode }

                adapterImage.onChangeSelectedList = {
                    this.itemView.performClick()
                }

            } else {
                itemView.isActivated = false
                dragIcon.visibility = View.GONE
                childRecyclerView.setOnDragListener(
                    ImageItemDragListener(
                        itemView.context,
                        onDragImage,
                        getTheme(),
                        true
                    )
                )
            }
        }

        override fun changeItemTheme(item: MultiItem, currentTheme: CurrentTheme) {
            DecoratorView.changeImageView(currentTheme, dragIcon, itemView.context)
            DecoratorView.changeItemsBackground(
                currentTheme,
                layoutImageItem,
                itemView.context
            )
        }

        override fun changeItemRow(currentTheme: CurrentTheme) {
            DecoratorView.changeImageView(currentTheme, dragIcon, itemView.context)
            DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                currentTheme,
                layoutImageItem,
                itemView.context
            )
        }

        override fun changeItemClear(currentTheme: CurrentTheme) {
            DecoratorView.changeImageView(currentTheme, dragIcon, itemView.context)
            DecoratorView.changeItemsBackground(
                currentTheme,
                layoutImageItem,
                itemView.context
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<MultiItem>, position: Int) {
        val item = currentList[position]
        holder.bind(item)
        holder.itemView.tag = position
        super.onBindViewHolder(holder, position)
    }

    fun setSavedTextToTextItem(text: String, editText: EditText) {
        val savedTextWithSpan =
            Html.fromHtml(text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
                .trimEnd('\n').toSpannable()
        val underlineSpans =
            savedTextWithSpan.getSpans(0, savedTextWithSpan.length, UnderlineSpan::class.java)

        for (defSpan in underlineSpans) {
            val start = savedTextWithSpan.getSpanStart(defSpan)
            val end = savedTextWithSpan.getSpanEnd(defSpan)
            savedTextWithSpan.removeSpan(defSpan)
            val customUnderline = CustomUnderlineSpan()
            savedTextWithSpan.setSpan(customUnderline, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        editText.setText(savedTextWithSpan)
        editText.setSelection(savedTextWithSpan.length)
    }

    fun setActionMode(isActionMode: ActionMode?) {
        actionMode = isActionMode
    }

    private fun EditText.disableInput() {
        isFocusable = false
        isCursorVisible = false
        isFocusableInTouchMode = false
    }

    private fun EditText.enableInput() {
        isFocusable = true
        isCursorVisible = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_IMAGE = 2
    }

    fun setDragFromTextItem(editText: EditText) {
        editText.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    editText.disableInput()
                    return@setOnDragListener true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    editText.enableInput()
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    editText.disableInput()
                }

                DragEvent.ACTION_DROP -> {
                    return@setOnDragListener false
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    when (event.result) {
                        true -> {
                        }

                        else -> {
                            editText.enableInput()
                        }
                    }
                    notifyDataSetChanged()
                }
            }
            return@setOnDragListener true
        }
    }
}