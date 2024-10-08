package com.buller.mysqlite.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.easynote.domain.models.Category
import com.easynote.domain.models.CurrentTheme

class DialogCategoryAdapter(private val listener: OnItemClickListener?) :
    RecyclerView.Adapter<DialogCategoryAdapter.CategoryHolder>() {
    private var currentThemeAdapter: CurrentTheme? = null
    private var currentCategories: ArrayList<Category> = arrayListOf()
    var onChangeTheme: ((CurrentTheme?, CategoryHolder) -> Unit)? = null
    val differ = AsyncListDiffer(this, diffUtilCallback)

    companion object {
        val diffUtilCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.idCategory == newItem.idCategory
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.idCategory == newItem.idCategory && oldItem.titleCategory == newItem.titleCategory
            }

        }
    }

    inner class CategoryHolder(
        itemView: View,
        val currentCategories: List<Category>?,
        val context: Context
    ) :
        RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.etTitleCategoryText)
        val checkBox: AppCompatCheckBox = itemView.findViewById(R.id.cbCategory)
        val itemLayout: CardView = itemView.findViewById(R.id.cardView)

        init {
            checkBox.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedCategory = differ.currentList[position]
                    listener?.onCheckBoxClick(selectedCategory, checkBox.isChecked)
                }
            }
        }

        fun setData(item: Category) {
            textView.text = item.titleCategory
            if (currentCategories != null) {
                if (currentCategories.isNotEmpty()) {
                    currentCategories.forEach { category ->
                        if (item.idCategory == category.idCategory) {
                            checkBox.isChecked = true
                            return
                        } else {
                            checkBox.isChecked = false
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryHolder(
            inflater.inflate(
                R.layout.rc_item_category_add_fragment_bottom_sheet,
                parent,
                false
            ), currentCategories, parent.context
        )
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        //val category = listArray[position]
        val category = differ.currentList[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        changeItemFromCurrentTheme(currentThemeId, holder)
        holder.setData(category)
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(listItems: List<Category>) {
//        listArray.clear()
//        listArray.addAll(listItems)
//        notifyDataSetChanged()
        differ.submitList(listItems)
    }

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
    }

    interface OnItemClickListener {
        fun onCheckBoxClick(category: Category, isChecked: Boolean)
    }

    private fun changeItemFromCurrentTheme(
        currentThemeId: Int,
        holder: CategoryHolder
    ) {
        onChangeTheme?.invoke(currentThemeAdapter, holder)
    }

    fun updateCurrentCategories(it: List<Category>?) {
        currentCategories.clear()
        if (it != null) {
            currentCategories.addAll(it)
        }
        notifyDataSetChanged()
    }
}