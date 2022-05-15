package com.buller.mysqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CategoryAdapterEditActivity(val noteCategoryList: ArrayList<NoteCategory>): RecyclerView.Adapter<CategoryAdapterEditActivity.CategoryHolderEditActivity>() {

    class CategoryHolderEditActivity(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val labelItem = itemView.findViewById<TextView>(R.id.tvTitleCategoryEditActivity)
        fun setData(itemView:NoteCategory){
            labelItem.text = itemView.title
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolderEditActivity {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryHolderEditActivity(inflater.inflate(R.layout.item_category_content_activity,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryHolderEditActivity, position: Int) {
       holder.setData(noteCategoryList.get(position))
    }

    override fun getItemCount(): Int {
        return noteCategoryList.size
    }

    fun updateAdapter(list:ArrayList<NoteCategory>) {
        noteCategoryList.clear()
        noteCategoryList.addAll(list)
        notifyDataSetChanged()
    }

}
