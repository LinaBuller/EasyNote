package com.buller.mysqlite.fragments.add.multiadapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.view.ActionMode
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.add.AutoFitGridLayoutManager
import com.buller.mysqlite.fragments.add.ImageAdapter
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.ImageItem
import com.easynote.domain.models.MultiItem
import com.easynote.domain.models.TextItem
import com.easynote.domain.utils.edittextnote.CommandReplaceText


class MultiItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {
    private var currentThemeAdapter: CurrentTheme? = null
    private val differ = AsyncListDiffer(this, callback)
    var getActionMode: (() -> ActionMode?)? = null
    var getSelectedList: (() -> MutableSet<MultiItem>?)? = null
    var onChangeSelectedList: ((MultiItem) -> Unit)? = null
    var getIsUserChangeText: (() -> Boolean)? = null
    var setCommandReplaceText: ((CommandReplaceText) -> Unit)? = null

    companion object {

        private const val TYPE_TEXT = 1
        private const val TYPE_IMAGE = 2

        val callback = object : DiffUtil.ItemCallback<MultiItem>() {
            override fun areItemsTheSame(oldItem: MultiItem, newItem: MultiItem): Boolean {
                if (oldItem is TextItem && newItem is TextItem) {
                    return oldItem.itemTextId == newItem.itemTextId
                }
                if (oldItem is ImageItem && newItem is ImageItem) {
                    return oldItem.imageItemId == newItem.imageItemId
                }
                return false
            }

            override fun areContentsTheSame(oldItem: MultiItem, newItem: MultiItem): Boolean {
                if (oldItem is TextItem && newItem is TextItem) {
                    return oldItem.text == newItem.text
                }

                if (oldItem is ImageItem && newItem is ImageItem) {
                    return oldItem.listImageItems.containsAll(newItem.listImageItems)
                }
                return false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_TEXT -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_from_bord, parent, false)
                return TextHolder(textView, parent.context)
            }

            TYPE_IMAGE -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_from_bord, parent, false)
                return ImageHolder(textView, parent.context)
            }

            else -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_from_bord, parent, false)
                return TextHolder(textView, parent.context)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val currentThemeId = currentThemeAdapter!!.themeId

        when (getItemViewType(position)) {
            TYPE_TEXT -> {
                (holder as TextHolder).setData(item as TextItem)
            }

            TYPE_IMAGE -> {
                (holder as ImageHolder).setData(item as ImageItem)
            }
        }
        changeItemFromCurrentTheme(currentThemeId, holder as MultiHolder)
    }

    abstract inner class MultiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }


    inner class TextHolder(itemView: View, val context: Context) : MultiHolder(itemView) {

        var watcher: TextWatcher? = null
        val editText: EditText = itemView.findViewById(R.id.etTextItem)
        val dragIcon: ImageView = itemView.findViewById(R.id.ivDrag)
        val layoutTextItem: ConstraintLayout = itemView.findViewById(R.id.layoutTextItem)

        fun setData(textItem: TextItem) {
            editText.removeTextChangedListener(watcher)
            editText.setText(textItem.text)

            if (getActionMode?.invoke() != null) {

                editText.disableInput()
                dragIcon.visibility = View.VISIBLE

                val selectedItems = getSelectedList?.invoke()
                if (selectedItems != null) {
                    itemView.isActivated = selectedItems.contains(textItem)
                }
                itemView.setOnClickListener {
                    onChangeSelectedList?.invoke(textItem)
                    notifyItemChanged(textItem.position)
                }
                editText.setOnClickListener {
                    onChangeSelectedList?.invoke(textItem)
                    notifyItemChanged(textItem.position)
                }

            } else {
                editText.enableInput()
                dragIcon.visibility = View.GONE
                itemView.isActivated = false

                var oldText = ""
                watcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        if (!getIsUserChangeText?.invoke()!!) return
                        if (s == null) return
                        oldText = s.substring(start, start + count)
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (!getIsUserChangeText?.invoke()!!) return
                        if (s == null) return
                        val newText = s.substring(start, start + count)
                        val command = CommandReplaceText(
                            textItem.itemTextId,
                            textItem.position,
                            start,
                            oldText,
                            newText
                        )
                        setCommandReplaceText?.invoke(command)
                    }

                    override fun afterTextChanged(s: Editable?) {
                        textItem.text = s.toString()
                    }
                }
                editText.addTextChangedListener(watcher)
            }
        }
    }

    inner class ImageHolder(itemView: View, val context: Context) : MultiHolder(itemView) {
        private val rcList: RecyclerView = itemView.findViewById(R.id.rcImageView)
        val layoutImageItem: ConstraintLayout = itemView.findViewById(R.id.layoutImageItem)
        val dragIcon: ImageView = itemView.findViewById(R.id.ivDrag)

        fun setData(imageItem: ImageItem) {
            val adapterImage = ImageAdapter()
            adapterImage.submitList(imageItem.listImageItems)
            rcList.layoutManager = AutoFitGridLayoutManager(context, 400)
            rcList.adapter = adapterImage

            if (getActionMode?.invoke() != null) {

                dragIcon.visibility = View.VISIBLE

                layoutImageItem.setOnClickListener {
                    onChangeSelectedList?.invoke(imageItem)
                    notifyItemChanged(imageItem.position)
                }
                rcList.setOnTouchListener { view, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.performClick()
                    } else {
                        false
                    }
                }

                rcList.setOnClickListener {
                    onChangeSelectedList?.invoke(imageItem)
                    notifyItemChanged(imageItem.position)
                }


                if (getSelectedList?.invoke() != null) {
                    itemView.isActivated = getSelectedList?.invoke()!!.contains(imageItem)
                }

                adapterImage.getActionMode = {
                    getActionMode?.invoke()
                }

                adapterImage.onChangeSelectedList = {
                    onChangeSelectedList?.invoke(imageItem)
                    notifyItemChanged(imageItem.position)
                }
            } else {
                itemView.isActivated = false
                dragIcon.visibility = View.GONE
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        when (differ.currentList[position]) {
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

    override fun getItemCount(): Int = differ.currentList.size

    fun submitListItems(listItems: List<MultiItem>) = differ.submitList(listItems)

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val list = differ.currentList.toMutableList()
        val oldItem = list.removeAt(fromPosition)
        list.add(toPosition, oldItem)

        val max: Int = maxOf(fromPosition, toPosition)
        val min: Int = minOf(fromPosition, toPosition)
        for (i in min..max) {
            list[i].position = i
        }
        differ.submitList(list)

    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {
        val currentThemeId = currentThemeAdapter!!.themeId
        if (currentThemeId == 0) {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(1, myViewHolder as MultiHolder)
            }
        } else {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(0, myViewHolder as MultiHolder)
            }
        }
    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {
        val currentThemeId = currentThemeAdapter!!.themeId
        if (currentThemeId == 0) {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(0, myViewHolder as MultiHolder)
            }
        } else {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(1, myViewHolder as MultiHolder)
            }
        }

    }

    private fun changeItemFromCurrentTheme(
        currentThemeId: Int,
        holder: MultiItemAdapter.MultiHolder
    ) {
        if (holder is TextHolder) {
            DecoratorView.changeText(currentThemeId, holder.editText, holder.context)
            DecoratorView.changeImageView(currentThemeId, holder.dragIcon, holder.context)
            DecoratorView.changeItemsBackground(currentThemeId,holder.layoutTextItem,holder.context)

        }
        if (holder is ImageHolder){
            DecoratorView.changeImageView(currentThemeId, holder.dragIcon, holder.context)
            DecoratorView.changeItemsBackground(currentThemeId,holder.layoutImageItem,holder.context)
        }
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
}