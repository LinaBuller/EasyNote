package com.buller.mysqlite.fragments.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R

import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.easynote.domain.models.Category
import java.util.Collections


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {
    var onItemClickCrypto: ((com.easynote.domain.models.Category) -> Unit)? = null
    var onItemClickPopupMenu: ((com.easynote.domain.models.Category, View) -> Unit)? = null
    private val differ = AsyncListDiffer(this, callback)
    private var currentThemeAdapter: CurrentTheme? = null

    companion object {
        val callback = object : DiffUtil.ItemCallback<com.easynote.domain.models.Category>() {
            override fun areItemsTheSame(oldItem: com.easynote.domain.models.Category, newItem: com.easynote.domain.models.Category): Boolean {
                return oldItem.idCategory == newItem.idCategory
            }

            override fun areContentsTheSame(oldItem: com.easynote.domain.models.Category, newItem: com.easynote.domain.models.Category): Boolean {
                return oldItem.titleCategory == newItem.titleCategory
            }
        }
    }

    inner class CategoryHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val titleCategory: TextView =
            itemView.findViewById(R.id.titleCategory)
        val ibCrypto: ImageButton = itemView.findViewById(R.id.ibCryptoCategory)
        val ibPopupmenuItem: ImageButton = itemView.findViewById(R.id.ibPopupmenuItem)
        val cardItem: CardView = itemView.findViewById(R.id.cwItemCategory)

        fun setData(item: com.easynote.domain.models.Category) {
            titleCategory.text = item.titleCategory

            ibCrypto.setOnClickListener {
                onItemClickCrypto?.invoke(item)
            }

            ibPopupmenuItem.setOnClickListener {
                onItemClickPopupMenu?.invoke(item, itemView)
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val currentCategory = differ.currentList[position]
        val currentThemeId = currentThemeAdapter!!.themeId
        holder.apply {
            setData(currentCategory)
            changeItemFromCurrentTheme(currentThemeId, holder)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryHolder(
            inflater.inflate(R.layout.rc_item_category_fragment, parent, false),
            parent.context
        )
    }

    fun submitList(listCategories: List<com.easynote.domain.models.Category>?) {
        differ.submitList(listCategories)
    }

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val list = arrayListOf<com.easynote.domain.models.Category>()
        list.addAll(differ.currentList)
        Collections.swap(list, fromPosition, toPosition)
        differ.submitList(list)
    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {
        val currentThemeId = currentThemeAdapter!!.themeId
        if (currentThemeId == 0) {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(1, myViewHolder as CategoryHolder)
            }
        } else {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(0, myViewHolder  as CategoryHolder)
            }
        }

    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {
        val currentThemeId = currentThemeAdapter!!.themeId
        if (currentThemeId == 0) {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(0, myViewHolder as CategoryHolder)
            }
        } else {
            if (myViewHolder != null) {
                changeItemFromCurrentTheme(1, myViewHolder as CategoryHolder)
            }
        }
    }

    private fun changeItemFromCurrentTheme(
        currentThemeId: Int,
        holder: CategoryHolder
    ) {
        DecoratorView.changeBackgroundCardView(currentThemeId, holder.cardItem, holder.context)
        DecoratorView.changeBackgroundText(currentThemeId, holder.titleCategory, holder.context)
        DecoratorView.changeIconColor(currentThemeId, holder.ibCrypto, holder.context)
        DecoratorView.changeIconColor(currentThemeId, holder.ibPopupmenuItem, holder.context)
    }
}
