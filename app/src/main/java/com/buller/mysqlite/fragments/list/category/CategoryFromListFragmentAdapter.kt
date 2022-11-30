package com.buller.mysqlite.fragments.list.category

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.constans.SortedConstants
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.utils.SystemUtils
import com.buller.mysqlite.viewmodel.NotesViewModel
import kotlin.properties.Delegates


class CategoryFromListFragmentAdapter(val mViewModel: NotesViewModel, val context: Context?) :
    RecyclerView.Adapter<CategoryFromListFragmentAdapter.CategoryFromListHolder>() {
    var listArray = ArrayList<Category>()
    var selectedItemPos = -1


    inner class CategoryFromListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleCategory: EditText = itemView.findViewById(R.id.etTitleCategory)
        val btEditTitleText: ImageButton = itemView.findViewById(R.id.ivEditCategory)
        val btDeleteCategory: ImageButton = itemView.findViewById(R.id.ivDeleteCategory)
        val btSaveChangeTitleCategory: ImageButton = itemView.findViewById(R.id.ivSaveCategory)
        val view: View = itemView.findViewById(R.id.rcItemCategory)

        val pin: CheckBox = itemView.findViewById(R.id.checkBoxCategoryList)

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
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: CategoryFromListHolder, position: Int) {
        val item = listArray[position]
        holder.setData(item)

        holder.apply {
            pin.setOnCheckedChangeListener(null)

            pin.isChecked = position==selectedItemPos

            pin.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    mViewModel.sort(idCategory = item.idCategory)
                    selectedItemPos = position
                    notifyDataSetChanged()
                }else{
                    mViewModel.sort(isAcs = SortedConstants.NO_SORT)
                }
            }


            btEditTitleText.setOnClickListener {
                btEditTitleText.visibility = View.GONE
                btSaveChangeTitleCategory.visibility = View.VISIBLE
                titleCategory.isFocusable = true
                titleCategory.isFocusableInTouchMode = true
                titleCategory.focus()
                SystemUtils.showSoftKeyboard(titleCategory, context!!)
            }
            btSaveChangeTitleCategory.setOnClickListener {
                btSaveChangeTitleCategory.visibility = View.GONE
                btEditTitleText.visibility = View.VISIBLE
                titleCategory.isFocusable = false
                item.titleCategory = holder.titleCategory.text.toString()
                mViewModel.updateCategory(item)
                SystemUtils.hideSoftKeyboard(titleCategory, context!!)
            }
            btDeleteCategory.setOnClickListener {
                //Add alert dialog
                mViewModel.deleteCategory(item)
                selectedItemPos = -1
                pin.isChecked = false
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
}