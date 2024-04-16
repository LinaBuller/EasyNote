package com.buller.mysqlite.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.buller.mysqlite.BaseCategoryAdapter
import com.buller.mysqlite.BaseViewHolder
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.models.Category
import com.easynote.domain.models.CurrentTheme

class CategoryCheckBoxListFragmentAdapter : BaseCategoryAdapter<Category>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Category> {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryCheckBoxViewHolder(
            inflater.inflate(
                R.layout.rc_item_category_list_fragment,
                parent,
                false
            )
        )
    }

    inner class CategoryCheckBoxViewHolder(itemView: View) : BaseViewHolder<Category>(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardViewCategory)
        private val titleCategory:TextView = itemView.findViewById(R.id.titleCategory)
        private val backgroundCategory: ConstraintLayout = itemView.findViewById(R.id.backgroundCategory)
        override fun bind(item: Category) {
            titleCategory.text = item.titleCategory
        }

        override fun changeItemTheme(item: Category, currentTheme: CurrentTheme) {
            DecoratorView.changeColorElevationCardView(
                currentTheme,
                cardView,
                itemView.context
            )
            DecoratorView.changeBackgroundCardView(
                currentTheme,
                cardView,
                itemView.context
            )
            DecoratorView.changeText(currentTheme, titleCategory, itemView.context)
            DecoratorView.changeItemsBackground(currentTheme,backgroundCategory,itemView.context)
        }

    }
}