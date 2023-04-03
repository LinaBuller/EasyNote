package com.buller.mysqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.model.Category


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {
    var list = ArrayList<Category>()

    class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val etTitleCategory: TextView =
            itemView.findViewById(R.id.tvTitleCategorySearchFragment)

        fun setData(item: Category) {
            etTitleCategory.text = item.titleCategory
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryHolder, position: Int) {
        holder.setData(list[position])
    }

    //
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryHolder(
            inflater.inflate(
                R.layout.rc_item_category_search_fragment,
                parent,
                false
            )
        )

    }

}
