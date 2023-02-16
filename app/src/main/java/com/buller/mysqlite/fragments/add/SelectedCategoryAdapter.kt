package com.buller.mysqlite.fragments.add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView

class SelectedCategoryAdapter :
    RecyclerView.Adapter<SelectedCategoryAdapter.SelectedCategoryHolder>() {
    private val listSelectedCategory = ArrayList<Category>()
    private var currentThemeAdapter: CurrentTheme? = null

    class SelectedCategoryHolder(itemView: View,val context: Context) : RecyclerView.ViewHolder(itemView) {
        val textCategory: TextView = itemView.findViewById(R.id.tvTitleCategoryAddFragment)
        val cardViewCategory: CardView = itemView.findViewById(R.id.cardViewCategory)
        fun setData(item: Category) {
            textCategory.text = item.titleCategory
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedCategoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SelectedCategoryHolder(
            inflater.inflate(
                R.layout.rc_item_category_add_fragment,
                parent,
                false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: SelectedCategoryHolder, position: Int) {
        val item = listSelectedCategory[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        changeItemFromCurrentTheme(currentThemeId,holder.context,holder)
        holder.setData(item)

    }

    override fun getItemCount(): Int = listSelectedCategory.size

    fun submitList(listSelectedCategories: List<Category>) {
        listSelectedCategory.clear()
        listSelectedCategory.addAll(listSelectedCategories)
        notifyDataSetChanged()
    }

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    private fun changeItemFromCurrentTheme(
        currentThemeId: Int,
        context: Context,
        holder: SelectedCategoryHolder
    ) {
        DecoratorView.changeText(currentThemeId,holder.textCategory,context)
        DecoratorView.changeBackgroundCardView(currentThemeId,holder.cardViewCategory,context)
    }

}
