package com.buller.mysqlite.fragments.add.bottomsheet.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView

class BtSheetCategoryAdapter(
    private val listener: OnItemClickListener,
    private val existCategories: List<Category>
) : RecyclerView.Adapter<BtSheetCategoryAdapter.CategoryHolder>() {
    var listArray = ArrayList<Category>()
    private var currentThemeAdapter: CurrentTheme? = null

    inner class CategoryHolder(
        itemView: View,
        val existCategories: List<Category>,
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
                    val selectedCategory = listArray[position]
                    listener.onCheckBoxClick(selectedCategory, checkBox.isChecked)
                }
            }
        }

        fun setData(item: Category) {
            textView.text = item.titleCategory
            synchronized(existCategories) {
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
        return CategoryHolder(
            inflater.inflate(
                R.layout.rc_item_category_add_fragment_bottom_sheet,
                parent,
                false
            ), existCategories, parent.context
        )
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = listArray[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        changeItemFromCurrentTheme(currentThemeId, holder.context, holder)
        holder.setData(category)
    }

    override fun getItemCount(): Int = listArray.size

    fun submitList(listItems: List<Category>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onCheckBoxClick(category: Category, isChecked: Boolean)
    }

    private fun changeItemFromCurrentTheme(
        currentThemeId: Int,
        context: Context,
        holder: CategoryHolder
    ) {

        DecoratorView.changeBackgroundCardView(
            currentThemeId,
            holder.itemLayout,
            context
        )
        DecoratorView.changeText(currentThemeId, holder.textView, context)
        DecoratorView.changeCheckBox(currentThemeId, holder.checkBox, context)
    }
}