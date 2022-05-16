package com.buller.mysqlite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.ActivityCategorySelectBinding
import com.buller.mysqlite.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CategoryActivitySelect() : AppCompatActivity() {
    protected lateinit var binding: ActivityCategorySelectBinding
    protected val myDbManager = MyDbManager(this)
    protected var job: Job? = null
    var listItemCategorySelected = ArrayList<ItemCategorySelect>()
    var categoryAdapter: CategoryAdapter =
        CategoryAdapter(listItemCategorySelected, this, myDbManager)
    val list = ArrayList<ItemCategorySelect>()

    protected val callback: ItemTouchHelperCallback = ItemTouchHelperCallback(categoryAdapter)
    protected val touchHelper: ItemTouchHelper = ItemTouchHelper(callback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCategoriesAdapter()
        fillAdapter()
        initToolbar()
        categoryAdapter.passIdAndTitleCategory = { position: Int, isCheck: Boolean ->
            listItemCategorySelected.get(position).check = isCheck
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    fun updateDbCategories(id: Long, title: String) {
        myDbManager.updateDbCategories(id, title)
    }

    private fun getCheckedCategories() {
        val intent = intent
        val list =
            intent.getParcelableArrayListExtra<NoteCategory>(ContentConstants.ID_CATEGORY_TO_CATEGORY_ACTIVITY_SELECT)
        list?.forEach { noteCategory->
            listItemCategorySelected.forEach{ itemCategorySelect->
                if (itemCategorySelect.id==noteCategory.id){
                    itemCategorySelect.check = true
                }
            }
        }
    }

    private fun initCategoriesAdapter() = with(binding) {
        rcCategories.layoutManager = LinearLayoutManager(this@CategoryActivitySelect)
        rcCategories.adapter = categoryAdapter
        touchHelper.attachToRecyclerView(binding.rcCategories)
    }

    fun onClickSaveSelectedCategory(view: View) = with(binding) {
        setIntentCategory()
        finish()
    }

    private fun setIntentCategory() {
        val items = ArrayList<NoteCategory>()
        listItemCategorySelected.forEach {
            if (it.check){
                items.add(NoteCategory(it.id, it.title))
            }
        }
        val i = Intent(this, EditActivity::class.java)
        i.putParcelableArrayListExtra(ContentConstants.ID_CATEGORY, items)
        setResult(RESULT_OK, i);
    }

    private fun fillAdapter() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val items = myDbManager.readDbCategoriesSelect()
            listItemCategorySelected.addAll(items)
            getCheckedCategories()
            categoryAdapter.notifyDataSetChanged()
        }
    }

    fun onClickAddNewCategoryFromCAS(view: View) = with(binding) {
        val title = etNameNewCategory.text.toString()
        if (title != "") {
            val id = myDbManager.insertDbCategories(etNameNewCategory.text.toString())
            listItemCategorySelected.add(ItemCategorySelect(id, title))
            categoryAdapter.notifyItemInserted(listItemCategorySelected.size - 1)
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