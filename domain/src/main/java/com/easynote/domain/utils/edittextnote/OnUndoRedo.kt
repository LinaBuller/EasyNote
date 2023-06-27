package com.easynote.domain.utils.edittextnote

interface OnUndoRedo {
    fun undo(str: String): String
    fun redo(str: String): String
}