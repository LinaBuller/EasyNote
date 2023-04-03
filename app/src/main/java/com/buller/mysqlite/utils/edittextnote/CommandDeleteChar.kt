package com.buller.mysqlite.utils.edittextnote

class CommandDeleteChar(private val insertIndex: Int, val changeChar: String) : OnUndoRedo {
    override fun undo(str: String): String {
        val stringBuilder = StringBuilder().append(str)
        stringBuilder.insert(insertIndex, changeChar)
        return stringBuilder.toString()
    }

    override fun redo(str: String): String {
        val stringBuilder = StringBuilder().append(str)
        stringBuilder.deleteCharAt(insertIndex)
        return stringBuilder.toString()
    }
}