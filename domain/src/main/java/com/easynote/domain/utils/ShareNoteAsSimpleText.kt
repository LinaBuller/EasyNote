package com.easynote.domain.utils

import android.content.Context
import android.content.Intent
import android.widget.EditText
import com.easynote.domain.models.Note

object ShareNoteAsSimpleText {

    fun sendSimpleText(note: Note, context: Context) {
        val sendingText = "Title: ${note.title}\nNote: ${note.text}"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sendingText)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun sendSimpleText(etTitle: EditText, etContent: EditText, context: Context) {


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