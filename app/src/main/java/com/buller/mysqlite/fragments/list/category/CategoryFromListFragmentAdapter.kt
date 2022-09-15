package com.buller.mysqlite.fragments.list.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.list.NotesAdapter
import com.buller.mysqlite.model.Category

class CategoryFromListFragmentAdapter: RecyclerView.Adapter<CategoryFromListFragmentAdapter.CategoryFromListHolder>()  {
    var listArray = ArrayList<Category>()

    class CategoryFromListHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    fun submitList(listCategories: List<Category>?) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFromListHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryFromListHolder(inflater.inflate(R.layout.rc_item_category, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryFromListHolder, position: Int) {

    }

    override fun getItemCount(): Int =listArray.size
}