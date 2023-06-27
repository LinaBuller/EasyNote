package com.easynote.domain.utils

import android.widget.EditText
import android.widget.TextView

object EditTextChangeToTextAndBackToEditText{

    fun editTextToText(tv: TextView) {

        tv.isEnabled = false
        tv.isCursorVisible = false
        tv.isFocusableInTouchMode = false
       // tv.setSelection(tv.text.length)
    }

    fun textToEditText(tv: TextView){
        tv.isEnabled = true
        tv.isFocusableInTouchMode = true
        tv.isCursorVisible = true
        tv.requestFocus()
    }

}