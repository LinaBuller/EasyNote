package com.buller.mysqlite.db

class ImageItem( val uri:String , val foreignId: Int =0, val id:Int = 0){

    fun isNew():Boolean{
        return id==0
    }
}