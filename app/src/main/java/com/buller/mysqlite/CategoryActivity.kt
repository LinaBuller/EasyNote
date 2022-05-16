package com.buller.mysqlite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.databinding.ActivityCategoryBinding
import com.buller.mysqlite.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityCategoryBinding
    protected val myDbManager = MyDbManager(this)
    var list: ArrayList<ItemCategory> = ArrayList()
    var categoryAdapter: CategoryAdapter = CategoryAdapter(list, this@CategoryActivity, myDbManager)

    protected var job: Job? = null
    protected val callback: ItemTouchHelperCallback = ItemTouchHelperCallback(categoryAdapter)
    protected val touchHelper: ItemTouchHelper = ItemTouchHelper(callback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCategoriesAdapter()
        fillAdapter()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    private fun initCategoriesAdapter() = with(binding) {
        rcCategories.layoutManager = LinearLayoutManager(this@CategoryActivity)
        rcCategories.adapter = categoryAdapter
        touchHelper.attachToRecyclerView(binding.rcCategories)
    }

    fun onClickAddNewCategory(view: View) = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title != "") {
            val id = myDbManager.insertDbCategories(etNameNewCategory.text.toString())
            list.add(ItemCategory(id, title))
            categoryAdapter.notifyDataSetChanged()
            etNameNewCategory.text.clear()
        }
    }

    fun onClickDeleteCategory(view: View) = with(binding) {
        etNameNewCategory.text.clear()
        if (imBtSaveCategory.isClickable) {
            imBtSaveCategory.isClickable = false
            imBtDeleteCategory.isClickable = false
        }
    }

    private fun fillAdapter() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val items =  myDbManager.readDbCategories()
            list.addAll(items)
            categoryAdapter.notifyDataSetChanged()
        }
    }
}