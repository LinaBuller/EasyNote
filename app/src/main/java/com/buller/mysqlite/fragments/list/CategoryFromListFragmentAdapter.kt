package com.buller.mysqlite.fragments.list

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R

import com.easynote.domain.models.CurrentTheme


class CategoryFromListFragmentAdapter(private val listener: OnClickAddNewCategory
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listArray = ArrayList<com.easynote.domain.models.Category>()

    var onClickCheckBox:((Long)->Unit)? = null
    var onChangeThemeItem:((CurrentTheme?, RecyclerView.ViewHolder)->Unit)? = null
    var onSetPinItem:((CheckBox)->Unit)? = null
    private var currentTheme: CurrentTheme? = null

    inner class CategoryFromListHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val pin: CheckBox = itemView.findViewById(R.id.checkBoxCategoryList)
        val cardView: CardView = itemView.findViewById(R.id.cardViewCategory)
        fun setData(item: com.easynote.domain.models.Category) {
            pin.text = item.titleCategory
        }
    }

    inner class AddCategoryFromListHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val cardViewAddCategoryFromListHolder: CardView =
            itemView.findViewById(R.id.cardViewAddCategory)
        val imageViewAddNewCategory: ImageView =
            itemView.findViewById(R.id.ibAddNewCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            createExistCategory(inflater, parent)
        } else {
            addCategory(inflater, parent)
        }

    }

    private fun createExistCategory(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): CategoryFromListHolder {
        return CategoryFromListHolder(
            inflater.inflate(R.layout.rc_item_category_list_fragment, parent, false), parent.context
        )
    }

    private fun addCategory(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AddCategoryFromListHolder {
        return AddCategoryFromListHolder(
            inflater.inflate(
                R.layout.rc_add_category_list_fragment,
                parent,
                false
            ), parent.context
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == listArray.size) 1 else 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        changeItemFromCurrentTheme(currentTheme, holder)
        if (position != listArray.size) {
            val item = listArray[position]

            (holder as CategoryFromListHolder).setData(item)
            holder.apply {
                pin.setOnClickListener(null)

                pin.setOnClickListener {
                    onClickCheckBox?.invoke(item.idCategory)
//                    if (mViewModel.filterCategoryId.value == item.idCategory) {
//                        mViewModel.resetFilterCategoryId()
//                    } else {
//                        mViewModel.setFilterCategoryId(item.idCategory)
//                    }
                }
                onSetPinItem?.invoke(pin)
//                mViewModel.filterCategoryId.observe(context as MainActivity) { idCategory ->
//                    pin.isChecked = item.idCategory == idCategory
//                }
            }
        } else {
            (holder as AddCategoryFromListHolder).imageViewAddNewCategory.setOnClickListener {
                listener.onClickAddNewCategory()
            }
        }
    }

    interface OnClickAddNewCategory {
        fun onClickAddNewCategory()
    }

    override fun getItemCount(): Int = listArray.size + 1

    fun submitList(listCategories: List<com.easynote.domain.models.Category>) {
        listArray.clear()
        listArray.addAll(listCategories)
        notifyDataSetChanged()
    }

    fun themeChanged(currentTheme: CurrentTheme) {
        this.currentTheme = currentTheme
        notifyDataSetChanged()
    }

    private fun changeItemFromCurrentTheme(
        currentTheme: CurrentTheme?,
        holder: RecyclerView.ViewHolder
    ) {
        onChangeThemeItem?.invoke(currentTheme,holder)
    }
}