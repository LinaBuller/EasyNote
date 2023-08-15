package com.easynote.domain.utils.edittextnote

class CommandReplaceText(
    val idItems: String, val positionItem:Int,
    private val startIndex: Int,
    private val oldText: String,
    private val newText: String
) :
    OnUndoRedo {

    override fun undo(str: String): String {
        val stringBuilder = StringBuilder().append(str)
        stringBuilder.delete(startIndex, startIndex + newText.length)
        stringBuilder.insert(startIndex, oldText)
        return stringBuilder.toString()
    }

    override fun redo(str: String): String {
        val stringBuilder = StringBuilder().append(str)
        stringBuilder.delete(startIndex, startIndex + oldText.length)
        stringBuilder.insert(startIndex, newText)
        return stringBuilder.toString()
    }
}