package com.buller.mysqlite

import android.view.View

interface BaseCategoryAdapterCallback<T> {
     fun onItemCategoryClick(model: T, view: View, position: Int)
     fun onItemCategoryLongClick(model: T, view: View): Boolean
}