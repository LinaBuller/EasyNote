package com.buller.mysqlite.utils.theme

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ActionBarContextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.buller.mysqlite.MainActivity
import com.buller.mysqlite.R
import com.buller.mysqlite.model.Note

object DecoratorView {

    fun changeBackgroundCardView(themeId: Int, cardView: CardView, context: Context) {
        when (themeId) {
            0 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.element_light))
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
            }
            1 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.element_dark))
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
            }
        }
    }

    fun changeBackgroundToCurrentNoteTitleCardView(
        themeId: Int,
        currentNote: Note,
        cardView: CardView,
        context: Context
    ) {
        when (themeId) {
            0 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList =
                    ColorStateList.valueOf(currentNote.colorFrameTitle)
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
            }
            1 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList =
                    ColorStateList.valueOf(currentNote.colorFrameTitle)
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
            }
        }
    }

    fun changeBackgroundToCurrentNoteContentCardView(
        themeId: Int,
        currentNote: Note,
        cardView: CardView,
        context: Context
    ) {
        when (themeId) {
            0 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList =
                    ColorStateList.valueOf(currentNote.colorFrameContent)
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
            }
            1 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList =
                    ColorStateList.valueOf(currentNote.colorFrameContent)
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
            }
        }
    }

    fun changeColorElevationCardView(themeId: Int, cardView: CardView, context: Context) {
        when (themeId) {
            0 -> {
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_light)
            }
            1 -> {
                cardView.outlineAmbientShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
                cardView.outlineSpotShadowColor =
                    ContextCompat.getColor(context, R.color.akcient_dark)
            }
        }
    }

    //R.color.dark_gray
    //R.color.grey
    fun changeText(themeId: Int, textView: TextView, context: Context) {
        when (themeId) {
            0 -> textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
            1 -> textView.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    fun changeCommentText(themeId: Int, textView: TextView, context: Context) {
        when (themeId) {
            0 -> textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
            1 -> textView.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    fun changeIconColor(themeId: Int, imageButton: ImageButton, context: Context) {
        when (themeId) {
            0 -> {
                imageButton.setColorFilter(ContextCompat.getColor(context, R.color.akcient_light))
                imageButton.background.mutate()
                imageButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.element_light))
            }
            1 -> {
                imageButton.setColorFilter(ContextCompat.getColor(context, R.color.akcient_dark))
                imageButton.background.mutate()
                imageButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.element_dark))
            }
        }
    }

    fun changeImageView(themeId: Int, imageView: ImageView, context: Context) {
        when (themeId) {
            0 -> {
                imageView.setColorFilter(ContextCompat.getColor(context, R.color.akcient_light))
            }
            1 -> {
                imageView.setColorFilter(ContextCompat.getColor(context, R.color.akcient_dark))
            }
        }
    }

    fun changeCheckBox(themeId: Int, checkBox: AppCompatCheckBox, context: Context) {
        when (themeId) {
            0 -> {
                val colorStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ), intArrayOf(
                        ContextCompat.getColor(context, R.color.grey),
                        ContextCompat.getColor(context, R.color.akcient_light)

                    )
                )
                checkBox.buttonTintList = colorStateList
            }
            1 -> {

                val colorStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ), intArrayOf(
                        ContextCompat.getColor(context, R.color.grey),
                        ContextCompat.getColor(context, R.color.akcient_dark)
                    )
                )
                checkBox.buttonTintList = colorStateList
            }
        }
    }

    fun changeBackgroundText(themeId: Int, textView: TextView, context: Context) {
        when (themeId) {
            0 -> {
                textView.setBackgroundColor(ContextCompat.getColor(context, R.color.element_light))
            }
            1 -> {
                textView.setBackgroundColor(ContextCompat.getColor(context, R.color.element_dark))
            }
        }
    }

    fun changeBackgroundToCurrentNoteTextView(
        color: Int,
        textView: TextView
    ) {
        textView.setBackgroundColor(color)
    }

    fun updateFieldsFromColors(
        colorTitle: Int,
        colorContent: Int,
        titleCardViewAddFragment: CardView?,
        contentCardViewAddFragment: CardView?,
        editTextTitle: View?,
        editTextContent: View?
    ) {
        if (colorTitle != 0 && colorContent != 0) {
            titleCardViewAddFragment?.setCardBackgroundColor(colorTitle)
            contentCardViewAddFragment?.setCardBackgroundColor(colorContent)
            editTextTitle?.setBackgroundColor(colorTitle)
            editTextContent?.setBackgroundColor(colorContent)

        } else {
            if (colorTitle != 0) {
                titleCardViewAddFragment?.setCardBackgroundColor(colorTitle)
                editTextTitle?.setBackgroundColor(colorTitle)
            }
            if (colorContent != 0) {
                contentCardViewAddFragment?.setCardBackgroundColor(colorContent)
                editTextContent?.setBackgroundColor(colorContent)
            }
        }
    }

    fun setIcon(context: Context, themeId: Int,iconId:Int): Drawable? {
        val checkableDrawable =
            ResourcesCompat.getDrawable(
                context.resources,
                iconId,
                null
            )

        when (themeId) {
            0 -> {
                checkableDrawable?.setTint(ResourcesCompat.getColor(context.resources,R.color.akcient_light,null))
            }
            1 -> {
                checkableDrawable?.setTint(ResourcesCompat.getColor(context.resources,R.color.akcient_dark,null))
            }
        }
        return checkableDrawable
    }
    fun setColorBackgroundFromActionModeToolbar(activity:MainActivity,currentTheme: CurrentTheme){
        val actionBar =
            activity.window?.decorView?.findViewById<ActionBarContextView>(R.id.action_mode_bar)

        if (currentTheme.themeId == 0) {
            actionBar?.setBackgroundColor(activity.resources.getColor(R.color.akcient_light, null))
            activity.window?.navigationBarColor  = activity.resources.getColor(R.color.akcient_light, null)
        } else {
            actionBar?.setBackgroundColor(activity.resources.getColor(R.color.akcient_dark, null))
            activity.window?.navigationBarColor  = activity.resources.getColor(R.color.akcient_dark, null)
        }
    }

    fun setThemeColorBackgroundNavigationBar(activity:MainActivity,currentTheme: CurrentTheme){
        if (currentTheme.themeId == 0) {
            activity.window?.navigationBarColor  = activity.resources.getColor(R.color.background_light, null)
        } else {
            activity.window?.navigationBarColor  = activity.resources.getColor(R.color.background_dark, null)
        }
    }
}


