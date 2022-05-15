package com.buller.mysqlite.utils

import android.content.Context
import android.content.Intent
import android.widget.EditText

object ShareNoteAsSimpleText {

    fun sendSimpleText(etTitle: EditText,etContent: EditText,context: Context) {
        val sendingText = "Title: ${etTitle.text}\nNote: ${etContent.text}"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sendingText)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}