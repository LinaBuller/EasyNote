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

import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.easynote.domain.models.Category

class DialogCategoryAdapter(
    private val listener: OnItemClickListener?
) : RecyclerView.Adapter<DialogCategoryAdapter.CategoryHolder>() {
    private var currentThemeAdapter: CurrentTheme? = null
    private var existCategories: ArrayList<com.easynote.domain.models.Category> = arrayListOf()
    val differ = AsyncListDiffer(this, diffUtilCallback)

    companion object {
        val diffUtilCallback = object : DiffUtil.ItemCallback<com.easynote.domain.models.Category>() {
            override fun areItemsTheSame(oldItem: com.easynote.domain.models.Category, newItem: com.easynote.domain.models.Category): Boolean {
                return oldItem.idCategory == newItem.idCategory
            }

            override fun areContentsTheSame(oldItem: com.easynote.domain.models.Category, newItem: com.easynote.domain.models.Category): Boolean {
                return oldItem.idCategory == newItem.idCategory && oldItem.titleCategory == newItem.titleCategory
            }

        }
    }

    inner class CategoryHolder(
        itemView: View,
        val existCategories: List<com.easynote.domain.models.Category>?,
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
//                    val selectedCategory = listArray[position]
                    val selectedCategory = differ.currentList[position]
                    listener?.onCheckBoxClick(selectedCategory, checkBox.isChecked)
                }
            }
        }

        fun setData(item: com.easynote.domain.models.Category) {
            textView.text = item.titleCategory
            if (existCategories != null) {
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
        //val category = listArray[position]
        val category = differ.currentList[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        changeItemFromCurrentTheme(currentThemeId, holder.context, holder)
        holder.setData(category)
    }

    override fun getItemCount(): Int = differ.currentList.size//listArray.size

    fun submitList(listItems: List<com.easynote.domain.models.Category>) {
//        listArray.clear()
//        listArray.addAll(listItems)
//        notifyDataSetChanged()
        differ.submitList(listItems)
    }

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
    }

    interface OnItemClickListener {
        fun onCheckBoxClick(category: com.easynote.domain.models.Category, isChecked: Boolean)
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

    fun updateList(it: List<com.easynote.domain.models.Category>?) {
        existCategories.clear()
        if (it != null) {
            existCategories.addAll(it)
        }
        notifyDataSetChanged()
    }
}