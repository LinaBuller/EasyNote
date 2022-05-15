package com.buller.mysqlite

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.ActivityCategorySelectBinding
import com.buller.mysqlite.db.MyDbManager
import com.buller.mysqlite.utils.CollectionsToBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList

class CategoryActivitySelect() : AppCompatActivity() {
    protected lateinit var binding: ActivityCategorySelectBinding
    protected val myDbManager = MyDbManager(this)
    protected var job: Job? = null
    var categoryAdapter: CategoryAdapter =
        CategoryAdapter(mutableListOf(), this, myDbManager)
    var list: MutableList<ItemCategoryBase> = mutableListOf()

    val listCheckedIdCategoryItem = mutableMapOf<Int,String>()
    val arrayID = ArrayList<Int>()
    val arrayTitle= ArrayList<String>()
    //protected val callback: ItemTouchHelperCallback = ItemTouchHelperCallback(categoryAdapterSelect)
    //protected val touchHelper: ItemTouchHelper = ItemTouchHelper(callback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCategoriesAdapter()
        fillAdapter()
        initToolbar()
        categoryAdapter.passIdAndTitleCategory = { i: Int,str:String, b: Boolean ->
            if (b) {
                listCheckedIdCategoryItem[i] = str
            } else {
                listCheckedIdCategoryItem.replace(i,str)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    fun updateDbCategories(id:Long,title:String){
        myDbManager.updateDbCategories(id,title)
    }

    private fun initCategoriesAdapter() = with(binding) {
        rcCategories.layoutManager = LinearLayoutManager(this@CategoryActivitySelect)
        rcCategories.adapter = categoryAdapter
        // touchHelper.attachToRecyclerView(binding.rcCategories)
    }

    fun onClickSaveSelectedCategory(view: View) = with(binding) {
        setIntentCategory()
        finish()
    }

    fun checkedItems():ArrayList<Int>{
        return ArrayList(listCheckedIdCategoryItem.keys)
    }

    private fun setIntentCategory() {
        val i = Intent(this, ModBtSheetChooseExport::class.java)
        i.putExtra()
        arrayID.addAll(listCheckedIdCategoryItem.keys)
        arrayTitle.addAll(listCheckedIdCategoryItem.values)
        i.putIntegerArrayListExtra(ContentConstants.ID_CATEGORY,arrayID)
        i.putStringArrayListExtra(ContentConstants.NAME_CATEGORY, arrayTitle)
        setResult(RESULT_OK, i);
    }

    private fun fillAdapter() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            list = myDbManager.readDbCategoriesSelect()
            categoryAdapter.updateAdapter(list)
        }
    }

    fun onClickAddNewCategory(view: View) = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title != "") {
            val id = myDbManager.insertDbCategories(etNameNewCategory.text.toString())
            list.add(ItemCategorySelect(id, title))
            categoryAdapter.notifyDataSetChanged()
            etNameNewCategory.text.clear()
        }
    }

    fun onClickDeleteCategory(view: View) = with(binding) {
        etNameNewCategory.text.clear()
        if (imBtSaveCategory.isClickable) {
            imBtSaveCategory.isClickable = false
        }
    }

    fun initToolbar() = with(binding) {
        setSupportActionBar(toolbarCategorySelect)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.add_category_to_note);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}