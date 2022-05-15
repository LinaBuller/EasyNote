package com.buller.mysqlite.utils

import android.widget.EditText
import android.widget.TextView

object EditTextChangeToTextAndBackToEditText{

    fun editTextToText(tv: TextView) {
        tv.isCursorVisible = false
        tv.isFocusableInTouchMode = false
        tv.isEnabled = false
    }

    fun textToEditText(tv: EditText){
        tv.isEnabled = true
        tv.isFocusableInTouchMode = true
        tv.isCursorVisible = true
        tv.requestFocus()
        tv.setSelection(tv.text.length)
    }

}