package com.buller.mysqlite.utils

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import com.buller.mysqlite.R

object EditTextUtil {

    fun editText(etContent:EditText,view:View){
        val content = etContent.text
        val selectStartContent: Int = etContent.selectionStart
        val selectEndContent: Int = etContent.selectionEnd
        val sb = SpannableStringBuilder(content)

        val firstSpanDef = SpanDefinition()
        val secondSpanDef = SpanDefinition()

        when (view.id) {
            R.id.bBold -> {
                var array =
                    sb.getSpans(selectStartContent, selectEndContent, StyleSpan::class.java)
                array = array.filter { b -> b.style == Typeface.BOLD }.toTypedArray()
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )


                if (firstSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            R.id.bItalic -> {
                var array =
                    sb.getSpans(selectStartContent, selectEndContent, StyleSpan::class.java)
                array = array.filter { b -> b.style == Typeface.ITALIC }.toTypedArray()
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )


                if (firstSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }


            }
            R.id.bUnderline -> {
                val array =
                    sb.getSpans(selectStartContent, selectEndContent, UnderlineSpan::class.java)
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )
                if (firstSpanDef.create) {
                    sb.setSpan(
                        UnderlineSpan(),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        UnderlineSpan(),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            R.id.bListText -> {
                val findChar = sb.lastIndexOfAny("\n".toCharArray(), selectStartContent, false)
                val listNewLine: MutableList<Int> = ArrayList<Int>()
                listNewLine.add(0, findChar + 1)
                var countDot = 0
                var currentPosition = selectStartContent
                while (true) {
                    val nextNewLine = sb.indexOfAny("\n".toCharArray(), currentPosition, false)
                    if (nextNewLine == -1 || nextNewLine > selectEndContent) {
                        break
                    }
                    currentPosition = nextNewLine + 1
                    listNewLine.add(nextNewLine + 1)
                }


                for (i in listNewLine.size until listNewLine.size) {
                    if (i >= sb.length) {
                        break
                    }
                    if (sb[i] == "⦁"[0]) {
                        countDot++
                    }
                }
//                listNewLine.forEach { dot ->
//                    if(dot>=sb.length){
//                        return
//                    }
//                    if (sb[dot] == "⦁"[0]) {
//                        countDot++
//                    }
//                }

                if (countDot == listNewLine.size) {
                    for (index in listNewLine.indices.reversed()) {
                        sb.insert(listNewLine[index], "")
                    }
                } else {
                    for (index in listNewLine.indices.reversed()) {
                        val position = listNewLine[index]
                        if (position >= sb.length || sb[position] != "⦁"[0]) {
                            sb.insert(position, "⦁ ")
                        }
                    }
                }
            }
            R.id.bStrikeline -> {
                val array =
                    sb.getSpans(
                        selectStartContent,
                        selectEndContent,
                        StrikethroughSpan::class.java
                    )
                createSpanDefinitions(
                    array as Array<Any>,
                    sb,
                    selectStartContent,
                    selectEndContent,
                    firstSpanDef,
                    secondSpanDef
                )
                if (firstSpanDef.create) {
                    sb.setSpan(
                        StrikethroughSpan(),
                        firstSpanDef.start,
                        firstSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (secondSpanDef.create) {
                    sb.setSpan(
                        StrikethroughSpan(),
                        secondSpanDef.start,
                        secondSpanDef.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            R.id.bCleanText -> {
                val arrayAllSpans = sb.getSpans(0, sb.length, Any::class.java)
                if (arrayAllSpans.isNotEmpty()) {
                    arrayAllSpans.forEach { ss ->
                        sb.removeSpan(ss)
                    }
                }
            }
        }
        etContent.text = sb
        etContent.setSelection(selectStartContent, selectEndContent)
    }

    private fun createSpanDefinitions(
        arraySpansAllText: Array<Any>,
        spannableStrBuilder: SpannableStringBuilder,
        selectStartContent: Int,
        selectEndContent: Int,
        firstSpan: SpanDefinition,
        secondSpan: SpanDefinition
    ) {
        firstSpan.create = true
        firstSpan.start = selectStartContent
        firstSpan.end = selectEndContent

        arraySpansAllText.forEach { b ->
            val startSpan = spannableStrBuilder.getSpanStart(b)
            val endSpan = spannableStrBuilder.getSpanEnd(b)
            spannableStrBuilder.removeSpan(b)

            if (selectStartContent in startSpan..endSpan && selectEndContent in startSpan..endSpan) {
                if (selectStartContent != startSpan) {
                    firstSpan.create = true
                    firstSpan.start = startSpan
                    firstSpan.end = selectStartContent
                } else {
                    firstSpan.create = false
                }

                if (selectEndContent != endSpan) {
                    secondSpan.create = true
                    secondSpan.start = selectEndContent
                    secondSpan.end = endSpan
                } else {
                    secondSpan.create = false
                }

            } else {
                firstSpan.create = true
                secondSpan.create = false
                if (startSpan < firstSpan.start) {
                    firstSpan.start = startSpan

                }
                if (firstSpan.end < endSpan) {
                    firstSpan.end = endSpan
                }
            }
        }
    }
}