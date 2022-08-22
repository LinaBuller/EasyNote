package com.buller.mysqlite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityCategoryBinding

    var list: ArrayList<ItemCategory> = ArrayList()
    var categoryAdapter: CategoryAdapter = CategoryAdapter(list, this@CategoryActivity)
    protected val callbackCategories: ItemTouchHelperCallbackCategories = ItemTouchHelperCallbackCategories(categoryAdapter)
    protected val touchHelper: ItemTouchHelper = ItemTouchHelper(callbackCategories)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initCategoriesAdapter()
        fillAdapter()
        categoryAdapter.passIdCategoryToCA = { idCategory:Int->
            setIntentToMainActivity(idCategory)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initCategoriesAdapter() = with(binding) {
        rcCategories.layoutManager = LinearLayoutManager(this@CategoryActivity)
        rcCategories.adapter = categoryAdapter
        touchHelper.attachToRecyclerView(binding.rcCategories)
    }

    fun onClickAddNewCategory(view: View) = with(binding) {
//        val title = etNameNewCategory.text.toString()
//        if (title != "") {
//            val id = notesViewModel.insertCategory(ItemCategoryBase(etNameNewCategory.text.toString()))
//            list.add(ItemCategory(id, title))
//            categoryAdapter.notifyDataSetChanged()
//            etNameNewCategory.text.clear()
//        }
    }

    fun onClickDeleteCategory(view: View) = with(binding) {
        etNameNewCategory.text.clear()
        if (imBtSaveCategory.isClickable) {
            imBtSaveCategory.isClickable = false
            imBtDeleteCategory.isClickable = false
        }
    }

    private fun fillAdapter() {
//        job?.cancel()
//        job = CoroutineScope(Dispatchers.Main).launch {
//            val items =  notesViewModel.readCategoriesDbForItemCategory()
//            list.addAll(items)
//            categoryAdapter.notifyDataSetChanged()
//        }
    }

    fun removeItemDb(id:Long){
//       notesViewModel.removeCategory(id)
    }

    fun initToolbar() = with(binding) {
        setSupportActionBar(tbCategoryActivity)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.category);
    }

    fun setIntentToMainActivity(idCategory:Int){
        val i = Intent(this, MainActivity::class.java)
        i.putExtra(ContentConstants.ID_CATEGORY_FROM_SELECTED,idCategory)
        startActivity(i)
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}