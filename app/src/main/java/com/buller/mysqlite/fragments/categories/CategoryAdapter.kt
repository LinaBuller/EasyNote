package com.buller.mysqlite.fragments.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.buller.mysqlite.BaseReorderableAdapter
import com.buller.mysqlite.BaseViewHolder
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.models.Category
import com.easynote.domain.models.CurrentTheme

class CategoryAdapter : BaseReorderableAdapter<Category>() {
    var onItemMove: ((List<Category>) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Category> {
        val inflater = LayoutInflater.from(parent.context)
        return NewCategoryHolder(inflater.inflate(R.layout.rc_item_category_fragment, parent, false))
    }

    inner class NewCategoryHolder(itemView: View):ReorderableHolder<Category>(itemView) {
        val titleCategory: TextView =
            itemView.findViewById(R.id.titleCategory)
        val ibPopupmenuItem: ImageButton = itemView.findViewById(R.id.ibPopupmenuItem)
        val layoutCategory: ConstraintLayout = itemView.findViewById(R.id.layoutCategoryFragment)
        val dragIcon: ImageView = itemView.findViewById(R.id.ivDragIconCategory)

        override fun changeItemRow(currentTheme: CurrentTheme) {
            DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                currentTheme,
                layoutCategory,
                itemView.context
            )
        }

        override fun changeItemClear(currentTheme: CurrentTheme) {
            DecoratorView.changeItemsBackground(
                currentTheme,
                layoutCategory,
                itemView.context
            )
        }

        override fun changeItemTheme(item: Category, currentTheme: CurrentTheme) {
            DecoratorView.changeItemsBackground(
                currentTheme,
                layoutCategory,
                itemView.context
            )
            DecoratorView.changeText(currentTheme, titleCategory, itemView.context)
            DecoratorView.changeIconColor(currentTheme, ibPopupmenuItem, itemView.context)
            DecoratorView.changeImageView(currentTheme,dragIcon, itemView.context)
        }

        override fun bind(item: Category) {
            titleCategory.text = item.titleCategory
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<Category>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.tag = position
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        super.onRowMoved(fromPosition, toPosition)
        onItemMove?.invoke(currentList)
    }

}