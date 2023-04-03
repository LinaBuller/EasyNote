package com.buller.mysqlite.utils.edittextnote

interface OnUndoRedo {
    fun undo(str: String): String
    fun redo(str: String): String
}