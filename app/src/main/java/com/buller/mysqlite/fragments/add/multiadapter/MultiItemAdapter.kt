package com.buller.mysqlite.fragments.add.multiadapter

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.add.ImageAdapter
import com.buller.mysqlite.fragments.add.OnUserChangeText
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.buller.mysqlite.utils.edittextnote.CommandReplaceText
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel


class MultiItemAdapter(private val changeTextManager: OnUserChangeText) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {
    private var currentThemeAdapter: CurrentTheme? = null
    private val differ = AsyncListDiffer(this, callback)
    var onTextChanged: ((CommandReplaceText) -> Unit)? = null
    var mViewModel: NotesViewModel? = null
    var onItemClick: ((MultiItem, View, Int) -> Unit)? = null

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
                    //position
                    return oldItem.listImageItems.size == newItem.listImageItems.size
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
                return TextHolder(textView, parent.context, changeTextManager)
            }

            TYPE_IMAGE -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image_from_bord, parent, false)
                return ImageHolder(textView)
            }

            else -> {
                val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_from_bord, parent, false)
                return TextHolder(textView, parent.context, changeTextManager)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        val selectedItems = mViewModel?.selectedItemsNoteFromActionMode?.value
        val actionMode = mViewModel?.actionMode

        when (getItemViewType(position)) {
            TYPE_TEXT -> {
                (holder as TextHolder).setData(item as TextItem)
                changeItemFromCurrentTheme(currentThemeId, holder)
                if (actionMode != null) {
                    if (selectedItems != null) {
                        holder.itemView.isActivated = selectedItems.contains(item)
                    }
                    holder.editText.disableInput()
                    holder.dragIcon.visibility = View.VISIBLE
                } else {
                    holder.itemView.isActivated = false
                    holder.dragIcon.visibility = View.GONE
                    holder.editText.enableInput()
                }
            }

            TYPE_IMAGE -> {
                (holder as ImageHolder).setData(item as ImageItem)
                changeItemFromCurrentTheme(currentThemeId, holder)
                if(actionMode!=null){
                    if (selectedItems!=null){
                        holder.itemView.isActivated = selectedItems.contains(item)
                    }
                    holder.dragIcon.visibility = View.VISIBLE
                }else{
                    holder.itemView.isActivated = false
                    holder.dragIcon.visibility = View.GONE
                }
            }
        }

    }

    fun EditText.disableInput() {
        isFocusable = false
        isFocusableInTouchMode = false
        isCursorVisible = false
    }

    fun EditText.enableInput() {
        isFocusable = true
        isCursorVisible = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    abstract inner class MultiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[adapterPosition], itemView, adapterPosition)
            }
        }
    }

    inner class TextHolder(
        itemView: View,
        val context: Context,
        val changeTextManager: OnUserChangeText
    ) : MultiHolder(itemView) {

        var watcher: TextWatcher? = null
        val editText: EditText = itemView.findViewById(R.id.etTextItem)
        val dragIcon: ImageView = itemView.findViewById(R.id.ivDrag)
        private val actionMode = mViewModel?.actionMode

        fun setData(item: TextItem) {
            editText.removeTextChangedListener(this.watcher)
            editText.setText(item.text)
            if (actionMode == null) {
                var oldText = ""
                watcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        if (!changeTextManager.setUserChange()) return
                        if (s == null) return
                        oldText = s.substring(start, start + count)
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (!changeTextManager.setUserChange()) return
                        if (s == null) return
                        val newText = s.substring(start, start + count)
                        onTextChanged?.invoke(
                            CommandReplaceText(
                                item.itemTextId,
                                item.position,
                                start,
                                oldText,
                                newText
                            )
                        )
                    }

                    override fun afterTextChanged(s: Editable?) {
                        item.text = s.toString()
                    }
                }
                editText.addTextChangedListener(watcher)
            }else{
                editText.setOnClickListener {
                    onItemClick?.invoke(differ.currentList[adapterPosition], itemView, adapterPosition)
                }
            }
        }
    }

    inner class ImageHolder(itemView: View) : MultiHolder(itemView) {
        private val adapterImage: ImageAdapter by lazy { ImageAdapter() }
        private val rcList: RecyclerView = itemView.findViewById(R.id.rcImageView)
        val dragIcon: ImageView = itemView.findViewById(R.id.ivDrag)
        private val actionMode = mViewModel?.actionMode

        fun setData(imageItem: ImageItem) {
            adapterImage.submitList(imageItem.listImageItems)
            rcList.adapter = adapterImage
            if (actionMode == null) {
                rcList.suppressLayout(false)
            }else{
                rcList.suppressLayout(true)
            }

            rcList.layoutManager = StaggeredGridLayoutManager(2, 1)
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

    fun submitListItems(listItems: List<MultiItem>) {
        differ.submitList(listItems)
    }

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
        }
    }
}