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
import com.buller.mysqlite.utils.SystemUtils
import com.buller.mysqlite.utils.theme.CurrentTheme
import com.buller.mysqlite.utils.theme.DecoratorView
import com.buller.mysqlite.viewmodel.NotesViewModel


class CategoryFromListFragmentAdapter(
    val contextT: Context?,
    val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<CategoryFromListFragmentAdapter.CategoryFromListHolder>() {
    var listArray = ArrayList<Category>()
    private val mViewModel: NotesViewModel = ViewModelProvider((contextT as MainActivity))[NotesViewModel::class.java]
    private var currentThemeAdapter: CurrentTheme? = null

    inner class CategoryFromListHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
        val titleCategory: EditText = itemView.findViewById(R.id.etTitleCategory)
        val btEditTitleText: ImageButton = itemView.findViewById(R.id.ivEditCategory)
        val btDeleteCategory: ImageButton = itemView.findViewById(R.id.ivDeleteCategory)
        val btSaveChangeTitleCategory: ImageButton = itemView.findViewById(R.id.ivSaveCategory)
        val view: View = itemView.findViewById(R.id.rcItemCategory)
        val pin: CheckBox = itemView.findViewById(R.id.checkBoxCategoryList)
        val cardView:CardView = itemView.findViewById(R.id.rcItemCategory)

        fun setData(item: Category) {
            titleCategory.setText(item.titleCategory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFromListHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryFromListHolder(
            inflater.inflate(
                R.layout.rc_item_category_list_fragment_bottom_sheet,
                parent,
                false
            ),parent.context
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: CategoryFromListHolder, position: Int) {
        val item = listArray[position]
        val currentThemeId = currentThemeAdapter!!.themeId

        holder.setData(item)
        changeItemFromCurrentTheme(currentThemeId,holder.context,holder)
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

            btEditTitleText.setOnClickListener {
                btEditTitleText.visibility = View.GONE
                btSaveChangeTitleCategory.visibility = View.VISIBLE
                titleCategory.isFocusable = true
                titleCategory.isFocusableInTouchMode = true
                titleCategory.focus()
                SystemUtils.showSoftKeyboard(titleCategory, context)
            }
            btSaveChangeTitleCategory.setOnClickListener {
                btSaveChangeTitleCategory.visibility = View.GONE
                btEditTitleText.visibility = View.VISIBLE
                titleCategory.isFocusable = false
                item.titleCategory = holder.titleCategory.text.toString()
                mViewModel.updateCategory(item)
                SystemUtils.hideSoftKeyboard(titleCategory, context)
            }
            btDeleteCategory.setOnClickListener {
                //Add alert dialog
                mViewModel.deleteCategory(item)
            }
        }
    }


    private fun EditText.focus() {
        requestFocus()
        setSelection(length())
    }

    override fun getItemCount(): Int = listArray.size


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
        DecoratorView.changeBackgroundCardView(currentThemeId,holder.cardView,holder.context)
        DecoratorView.changeIconColor(currentThemeId,holder.btDeleteCategory,holder.context)
        DecoratorView.changeIconColor(currentThemeId,holder.btEditTitleText,holder.context)
        DecoratorView.changeIconColor(currentThemeId,holder.btSaveChangeTitleCategory,holder.context)
        DecoratorView.changeText(currentThemeId, holder.titleCategory, context)
    }
}