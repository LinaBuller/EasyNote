package com.buller.mysqlite.dialogs

interface OnCloseDialogListener {
    fun onCloseDialog(isDelete: Boolean = false, isArchive: Boolean = false)
}