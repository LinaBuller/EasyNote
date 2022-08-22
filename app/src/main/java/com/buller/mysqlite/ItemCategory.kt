package com.buller.mysqlite

open class ItemCategory(
    override val id: Long = 0,
    override var title: String
    ): ItemCategoryBase(id,title){

    fun isNew():Boolean{
        return id==0L
    }
}
