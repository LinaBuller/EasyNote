package com.buller.mysqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.db.MyDbManager
import com.buller.mysqlite.utils.EditTextChangeToTextAndBackToEditText
import java.util.*
import kotlin.collections.ArrayList


class CategoryAdapter(
    val listCategories: ArrayList<out ItemCategoryBase>,
    contextActivity: AppCompatActivity,
    val myDbManager: MyDbManager,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {
    val context = contextActivity
    var passIdAndTitleCategory: ((Int, Boolean) -> (Unit))? = null

    class CategoryHolderRead(
        itemView: View,
        val myDbManager: MyDbManager
    ) : RecyclerView.ViewHolder(itemView) {
        private val etTitleCategory: EditText = itemView.findViewById(R.id.etTitleCategory)
        private val ivEditCategory: ImageView = itemView.findViewById(R.id.ivEditCategory)
        private val ivLabelCategory: ImageView = itemView.findViewById(R.id.ivCategoryLebel)
        private val ivSaveCategory: ImageView = itemView.findViewById(R.id.ivSaveCategory)
        private val ivDeleteCategory: ImageView = itemView.findViewById(R.id.ivDeleteCategory)

        fun setData(item: ItemCategoryBase) {
            val itemCategory: ItemCategory = item as ItemCategory
            val text = itemCategory.title
            if (itemCategory.title != "") {
                etTitleCategory.setText(text)
                EditTextChangeToTextAndBackToEditText.editTextToText(etTitleCategory)
            }

            ivEditCategory.setOnClickListener {
                EditTextChangeToTextAndBackToEditText.textToEditText(etTitleCategory)
                ivEditCategory.visibility = View.GONE
                ivLabelCategory.visibility = View.GONE
                ivSaveCategory.visibility = View.VISIBLE
                ivDeleteCategory.visibility = View.VISIBLE
            }

            ivDeleteCategory.setOnClickListener {
                etTitleCategory.text.clear()
            }

            ivSaveCategory.setOnClickListener {
                ivEditCategory.visibility = View.VISIBLE
                ivLabelCategory.visibility = View.VISIBLE
                ivSaveCategory.visibility = View.GONE
                ivDeleteCategory.visibility = View.GONE
                EditTextChangeToTextAndBackToEditText.editTextToText(etTitleCategory)
                itemCategory.title = etTitleCategory.text.toString()
                myDbManager.updateDbCategories(itemCategory.id, itemCategory.title)
            }


        }
    }

    class CategoryHolderSelect(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val etTitleCategory: EditText = itemView.findViewById(R.id.etTitleCategorySelect)
        val ivEditCategory: ImageView = itemView.findViewById(R.id.ivEditCategorySelect)
        val ivSaveCategory: ImageView = itemView.findViewById(R.id.ivSaveCategorySelect)
        val ivDeleteCategory: ImageView = itemView.findViewById(R.id.ivDeleteCategorySelect)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSelect)
    }

    override fun getItemViewType(position: Int): Int {
        val item = listCategories.get(position)
        if (item is ItemCategorySelect) {
            return ContentConstants.EDIT_CATEGORY
        } else if (item is ItemCategory) {
            return ContentConstants.READ_CATEGORY
        }
        return -1
    }

    override fun getItemCount(): Int {
        return listCategories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ContentConstants.EDIT_CATEGORY -> {
                val holderSelect = holder as CategoryHolderSelect
                context as CategoryActivitySelect
                val item = listCategories[position] as ItemCategorySelect
                val text = item.title
                if (text != "") {
                    holderSelect.etTitleCategory.setText(text)
                    EditTextChangeToTextAndBackToEditText.editTextToText(holderSelect.etTitleCategory)
                }

                holderSelect.ivEditCategory.setOnClickListener {
                    EditTextChangeToTextAndBackToEditText.textToEditText(holderSelect.etTitleCategory)
                    holderSelect.ivEditCategory.visibility = View.GONE
                    holderSelect.ivSaveCategory.visibility = View.VISIBLE
                    holderSelect.ivDeleteCategory.visibility = View.VISIBLE
                }

                holderSelect.ivDeleteCategory.setOnClickListener {
                    holderSelect.etTitleCategory.text.clear()
                }

                holderSelect.ivSaveCategory.setOnClickListener {
                    holderSelect.ivEditCategory.visibility = View.VISIBLE
                    holderSelect.ivSaveCategory.visibility = View.GONE
                    holderSelect.ivDeleteCategory.visibility = View.GONE
                    EditTextChangeToTextAndBackToEditText.editTextToText(holderSelect.etTitleCategory)
                    item.title = holderSelect.etTitleCategory.text.toString()
                    context.updateDbCategories(item.id,item.title)
                }

                holderSelect.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    passIdAndTitleCategory?.invoke(position,isChecked)
                }
                holderSelect.checkBox.isChecked = item.check
            }

            ContentConstants.READ_CATEGORY -> {
                val holderRead = holder as CategoryHolderRead
                holderRead.setData(listCategories.get(position))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ContentConstants.EDIT_CATEGORY -> {
                return CategoryHolderSelect(inflater.inflate(R.layout.rc_item_category_select, parent, false))
            }
            ContentConstants.READ_CATEGORY -> {
                return CategoryHolderRead(inflater.inflate(R.layout.rc_item_category, parent, false), myDbManager)
            }
        }
        return CategoryHolderSelect(inflater.inflate(R.layout.rc_item_category_select, parent, false))
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(listCategories, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(listCategories, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }


    override fun onItemDismiss(position: Int) {
        removeItem(position, myDbManager)
        listCategories.removeAt(position);
        notifyItemRemoved(position);
    }

    fun removeItem(position: Int, dbManager: MyDbManager) {
        dbManager.removeDbCategories(listCategories.get(position).id)
        notifyItemRangeChanged(0, listCategories.size)
    }

//    fun updateAdapter(listNew:ArrayList<out ItemCategoryBase>) {
//        listCategories.clear()
//        listCategories.addAll(listNew)
//        notifyDataSetChanged()
//    }
}