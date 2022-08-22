package com.buller.mysqlite

import com.buller.mysqlite.ItemCategoryBase

open class ItemCategorySelect(

    override var id: Long,

    override var title: String,

    var check: Boolean = false

) : ItemCategoryBase(id, title)