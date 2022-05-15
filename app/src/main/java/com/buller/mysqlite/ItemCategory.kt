package com.buller.mysqlite

open class ItemCategory(override val id: Long = 0, var title: String):ItemCategoryBase(id){

    fun isNew():Boolean{
        return id==0L
    }
}
