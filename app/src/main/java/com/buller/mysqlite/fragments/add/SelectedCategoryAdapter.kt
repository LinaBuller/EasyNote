package com.buller.mysqlite.fragments.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.add.bottomsheet.categories.BtSheetCategoryAdapter
import com.buller.mysqlite.model.Category

class SelectedCategoryAdapter :
    RecyclerView.Adapter<SelectedCategoryAdapter.SelectedCategoryHolder>() {
    private val listSelectedCategory = ArrayList<Category>()

    class SelectedCategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvTitleCategoryAddFragment)
        fun setData(item: Category) {
            textView.text = item.titleCategory
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedCategoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SelectedCategoryHolder(
            inflater.inflate(
                R.layout.rc_item_category_add_fragment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SelectedCategoryHolder, position: Int) {
        holder.setData(listSelectedCategory[position])
    }

    override fun getItemCount(): Int = listSelectedCategory.size

    fun submitList(listSelectedCategories: List<Category>) {
        listSelectedCategory.clear()
        listSelectedCategory.addAll(listSelectedCategories)
        notifyDataSetChanged()
    }

}
