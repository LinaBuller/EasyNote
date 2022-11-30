package com.buller.mysqlite.fragments.add.bottomsheet.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.model.Category

class BtSheetCategoryAdapter(
    private val listener: OnItemClickListener,
    private val existCategories: List<Category>
) : RecyclerView.Adapter<BtSheetCategoryAdapter.CategoryHolder>() {
    var listArray = ArrayList<Category>()

    inner class CategoryHolder(itemView: View,val existCategories: List<Category>) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.etTitleCategoryText)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbCategory)


        init {
            checkBox.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedCategory = listArray[position]
                    listener.onCheckBoxClick(selectedCategory, checkBox.isChecked)
                }
            }
        }

        fun setData(item: Category) {
            textView.text = item.titleCategory
            synchronized(existCategories){
                if (existCategories.isNotEmpty()) {
                    existCategories.forEach { category ->
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
        return CategoryHolder(inflater.inflate(R.layout.rc_item_category_add_fragment_bottom_sheet, parent, false),existCategories)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = listArray[position]
        holder.setData(category)
    }

    override fun getItemCount(): Int = listArray.size

    fun submitList(listItems: List<Category>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onCheckBoxClick(category: Category, isChecked: Boolean)
    }
}