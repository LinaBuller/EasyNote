package com.buller.mysqlite.utils.edittextnote

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.buller.mysqlite.R

object EditTextNoteUtil {
    private const val color1 = -16777216
    private const val color2 = -8825529
    private const val color3 = -12693067
    private const val color4 = -10012233


    fun editText(etContent: EditText, view: View): SpannableStringBuilder {
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
        return sb
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

    //check input fields
    fun inputCheck(title: String, content: String): Boolean {
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(content))
    }

    private fun contrastTextToFields(context: Context, color: Int, view: View) {
        if (view is EditText) {
            if (color == color1 || color == color2 || color == color3 || color == color4) {
                view.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                view.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        } else if (view is TextView) {
            if (color == color1 || color == color2 || color == color3 || color == color4) {
                view.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                view.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    fun updateFieldsFromColors(
        colorTitle: Int, colorContent: Int, titleCardViewAddFragment: CardView?, contentCardViewAddFragment: CardView?,
        layoutMin: CardView?, layoutBig: CardView?, context: Context
    ) {
        if (colorTitle != 0 && colorContent != 0) {
            titleCardViewAddFragment?.setCardBackgroundColor(colorTitle)
            contentCardViewAddFragment?.setCardBackgroundColor(colorContent)
            layoutMin?.setCardBackgroundColor(colorTitle)
            layoutBig?.setCardBackgroundColor(colorContent)

        } else {
            if (colorTitle != 0) {
                titleCardViewAddFragment?.setCardBackgroundColor(colorTitle)
                layoutMin?.setCardBackgroundColor(colorTitle)
            } else {
                layoutMin?.setCardBackgroundColor(context.getColor(R.color.white))
                titleCardViewAddFragment?.setCardBackgroundColor(context.getColor(R.color.white))
            }
            if (colorContent != 0) {
                contentCardViewAddFragment?.setCardBackgroundColor(colorContent)
                layoutBig?.setCardBackgroundColor(colorContent)
            } else {
                layoutBig?.setCardBackgroundColor(context.getColor(R.color.white))
                contentCardViewAddFragment?.setCardBackgroundColor(context.getColor(R.color.white))
            }
        }


    }


}