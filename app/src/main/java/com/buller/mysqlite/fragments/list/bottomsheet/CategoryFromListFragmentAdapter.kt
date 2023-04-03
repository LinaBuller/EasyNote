package com.buller.mysqlite.fragments.list.bottomsheet

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel


class CategoryFromListFragmentAdapter(
    val contextT: Context?,
    val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listArray = ArrayList<Category>()

    private val mViewModel: NotesViewModel =
        ViewModelProvider((contextT as MainActivity))[NotesViewModel::class.java]
    private var currentThemeAdapter: CurrentTheme? = null

    inner class CategoryFromListHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val pin: CheckBox = itemView.findViewById(R.id.checkBoxCategoryList)
        val cardView: CardView = itemView.findViewById(R.id.cardViewCategory)

        fun setData(item: Category) {
            pin.text = item.titleCategory
        }
    }

    inner class AddCategoryFromListHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val cardViewAddCategoryFromListHolder: CardView =
            itemView.findViewById(R.id.cardViewAddCategory)
        val openCategoryFragment:ImageButton = itemView.findViewById(R.id.imageButtonOpenCategoryFragment)
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
    ): CategoryFromListFragmentAdapter.CategoryFromListHolder {
        return CategoryFromListHolder(
            inflater.inflate(R.layout.rc_item_category_list_fragment, parent, false), parent.context
        )
    }

    private fun addCategory(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): CategoryFromListFragmentAdapter.AddCategoryFromListHolder {
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
        if (position != listArray.size) {
            val item = listArray[position]
            val currentThemeId = currentThemeAdapter!!.themeId
            (holder as CategoryFromListHolder).setData(item)
            changeItemFromCurrentTheme(currentThemeId, holder.context, holder)
            holder.apply {
                pin.setOnClickListener(null)

                pin.setOnClickListener {
                    if (mViewModel.filterCategoryId.value == item.idCategory) {
                        mViewModel.resetFilterCategoryId()
                    } else {
                        mViewModel.setFilterCategoryId(item.idCategory)
                    }
                }

                mViewModel.filterCategoryId.observe(viewLifecycleOwner) { idCategory ->
                    pin.isChecked = item.idCategory == idCategory
                }
            }

        } else {
            (holder as AddCategoryFromListHolder).openCategoryFragment.setOnClickListener {
                Toast.makeText(contextT, "hello", Toast.LENGTH_SHORT).show()
            }
        }
//            btEditTitleText.setOnClickListener {
//                btEditTitleText.visibility = View.GONE
//                btSaveChangeTitleCategory.visibility = View.VISIBLE
//                titleCategory.isFocusable = true
//                titleCategory.isFocusableInTouchMode = true
//                titleCategory.focus()
//                SystemUtils.showSoftKeyboard(titleCategory, context)
//            }
//            btSaveChangeTitleCategory.setOnClickListener {
//                btSaveChangeTitleCategory.visibility = View.GONE
//                btEditTitleText.visibility = View.VISIBLE
//                titleCategory.isFocusable = false
//                item.titleCategory = holder.titleCategory.text.toString()
//                mViewModel.updateCategory(item)
//                SystemUtils.hideSoftKeyboard(titleCategory, context)
//            }
//            btDeleteCategory.setOnClickListener {
//                //Add alert dialog
//                mViewModel.deleteCategory(item)
//            }

    }


    private fun EditText.focus() {
        requestFocus()
        setSelection(length())
    }

    override fun getItemCount(): Int = listArray.size + 1


    fun submitList(listCategories: List<Category>) {
        listArray.clear()
        listArray.addAll(listCategories)
        notifyDataSetChanged()
    }

    fun themeChanged(currentTheme: CurrentTheme) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    private fun changeItemFromCurrentTheme(
        currentThemeId: Int,
        context: Context,
        holder: CategoryFromListHolder
    ) {
        DecoratorView.changeBackgroundCardView(currentThemeId, holder.cardView, holder.context)
//        DecoratorView.changeIconColor(currentThemeId,holder.btDeleteCategory,holder.context)
//        DecoratorView.changeIconColor(currentThemeId,holder.btEditTitleText,holder.context)
//        DecoratorView.changeIconColor(currentThemeId,holder.btSaveChangeTitleCategory,holder.context)
//        DecoratorView.changeText(currentThemeId, holder.titleCategory, context)
    }
}