package com.buller.mysqlite

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ActionBarContextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.Note

object DecoratorView {

    fun changeBackgroundCardView(theme: CurrentTheme?, cardView: CardView, context: Context) {
        when (theme?.themeId) {
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

    fun setBackgroundColorToCard(theme: CurrentTheme?, cardView: CardView, context: Context) {
        when (theme?.themeId) {
            0 -> {
                cardView.setCardBackgroundColor(
                    context.resources.getColor(
                        R.color.element_light,
                        null
                    )
                )
            }

            1 -> {
                cardView.setCardBackgroundColor(
                    context.resources.getColor(
                        R.color.element_dark,
                        null
                    )
                )
            }
        }
    }

    fun changeBackgroundToCurrentNoteTitleCardView(
        theme: CurrentTheme?,
        currentNote: Note,
        cardView: CardView,
        context: Context
    ) {
        when (theme?.themeId) {
            0 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList = ColorStateList.valueOf(currentNote.gradientColorFirst)
            }

            1 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList = ColorStateList.valueOf(currentNote.gradientColorFirst)
            }
        }
    }

    fun changeBackgroundToCurrentNoteContentCardView(
        theme: CurrentTheme?,
        currentNote: Note,
        cardView: CardView,
        context: Context
    ) {
        when (theme?.themeId) {
            0 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList = ColorStateList.valueOf(currentNote.gradientColorSecond)
            }

            1 -> {
                cardView.background?.mutate()
                cardView.backgroundTintList = ColorStateList.valueOf(currentNote.gradientColorSecond)
            }
        }
    }

    fun changeColorElevationCardView(theme: CurrentTheme?, cardView: CardView, context: Context) {
        when (theme?.themeId) {
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
    fun changeText(theme: CurrentTheme?, textView: TextView, context: Context) {
        when (theme?.themeId) {
            0 -> textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
            1 -> textView.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    fun changeCommentText(theme: CurrentTheme?, textView: TextView, context: Context) {
        when (theme?.themeId) {
            0 -> textView.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
            1 -> textView.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    fun changeIconColor(theme: CurrentTheme?, imageButton: ImageButton, context: Context) {
        when (theme?.themeId) {
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

    fun changeImageView(theme: CurrentTheme?, imageView: ImageView, context: Context) {
        when (theme?.themeId) {
            0 -> {
                imageView.imageTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.akcient_light,
                        null
                    )
                )
                //imageView.setColorFilter(ContextCompat.getColor(context, R.color.akcient_light))
            }

            1 -> {
                imageView.imageTintList = ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.akcient_dark,
                        null
                    )
                )
                //imageView.setColorFilter(ContextCompat.getColor(context, R.color.akcient_dark))
            }
        }
    }

    fun changeCheckBox(theme: CurrentTheme?, checkBox: AppCompatCheckBox, context: Context) {
        when (theme?.themeId) {
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

    fun changeBackgroundText(theme: CurrentTheme?, textView: TextView, context: Context) {
        when (theme?.themeId) {
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
        layoutBackground: ConstraintLayout,
        editTextTitle: View?
    ) {
        if (colorTitle != 0 && colorContent != 0) {
            titleCardViewAddFragment?.setCardBackgroundColor(colorTitle)
            layoutBackground.setBackgroundColor(colorContent)
            editTextTitle?.setBackgroundColor(colorTitle)

        } else {
            if (colorTitle != 0) {
                titleCardViewAddFragment?.setCardBackgroundColor(colorTitle)
                editTextTitle?.setBackgroundColor(colorTitle)
            }
            if (colorContent != 0) {
                layoutBackground.setBackgroundColor(colorContent)
            }
        }
    }

    fun setIcon(context: Context, themeId: Int, iconId: Int): Drawable? {
        val checkableDrawable =
            ResourcesCompat.getDrawable(
                context.resources,
                iconId,
                null
            )

        when (themeId) {
            0 -> {
                checkableDrawable?.setTint(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.akcient_light,
                        null
                    )
                )
            }

            1 -> {
                checkableDrawable?.setTint(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.akcient_dark,
                        null
                    )
                )
            }
        }
        return checkableDrawable
    }

    fun changeColorIcon(context: Context, themeId: Int): ColorStateList {
        when (themeId) {
            0 -> {
                return ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.akcient_light,
                        null
                    )
                )
            }

            1 -> {
                return ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.akcient_dark,
                        null
                    )
                )
            }

            else -> return ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.akcient_light,
                    null
                )
            )
        }
    }


    fun setColorBackgroundFromActionModeToolbar(
        activity: MainActivity,
        currentTheme: CurrentTheme
    ) {
        val actionBar =
            activity.window?.decorView?.findViewById<ActionBarContextView>(R.id.action_mode_bar)

        if (currentTheme.themeId == 0) {
            actionBar?.setBackgroundColor(activity.resources.getColor(R.color.akcient_light, null))
            activity.window?.navigationBarColor =
                activity.resources.getColor(R.color.akcient_light, null)
        } else {
            actionBar?.setBackgroundColor(activity.resources.getColor(R.color.akcient_dark, null))
            activity.window?.navigationBarColor =
                activity.resources.getColor(R.color.akcient_dark, null)
        }
    }

    fun setThemeColorBackgroundNavigationBar(activity: MainActivity, currentTheme: CurrentTheme) {
        if (currentTheme.themeId == 0) {
            activity.window?.navigationBarColor =
                activity.resources.getColor(R.color.background_light, null)
        } else {
            activity.window?.navigationBarColor =
                activity.resources.getColor(R.color.background_dark, null)
        }
    }

    fun changeItemsBackground(
        currentTheme: CurrentTheme?,
        layoutBackground: ConstraintLayout,
        context: Context
    ) {
        if (currentTheme?.themeId == 0) {
            val draw = ContextCompat.getDrawable(context, R.drawable.state_list_item_background_light_theme)
//            if (draw != null) {
//                draw.alpha = 80
//            }
            layoutBackground.background = draw
        } else {
            val draw =
                ContextCompat.getDrawable(context, R.drawable.state_list_item_background_dark_theme)
//            if (draw != null) {
//                draw.alpha = 80
//            }
            layoutBackground.background = draw
        }
    }

    fun changeItemsBackgroundClear(
        currentTheme: CurrentTheme?,
        layoutBackground: ConstraintLayout,
        context: Context
    ) {
        if (currentTheme?.themeId == 0) {
            val draw = ContextCompat.getDrawable(
                context,
                R.drawable.state_list_item_background_light_theme
            )
            if (draw != null) {
                draw.alpha = 0
            }
            layoutBackground.background = draw
        } else {
            val draw =
                ContextCompat.getDrawable(context, R.drawable.state_list_item_background_dark_theme)
            if (draw != null) {
                draw.alpha = 80
            }
            layoutBackground.background = draw
        }
    }

    fun changeColorBackgroundItemsWhichReadyToAcceptDragAndDropEventTarget(
        currentTheme: CurrentTheme?,
        layoutBackground: ConstraintLayout,
        context: Context
    ) {
        if (currentTheme?.themeId == 0) {
            val draw = ContextCompat.getDrawable(
                context,
                R.drawable.state_list_item_background_light_theme
            )
            if (draw != null) {
                draw.alpha = 80
            }
            layoutBackground.background = draw
            layoutBackground.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.akcient_light
                )
            )
        } else {
            val draw =
                ContextCompat.getDrawable(context, R.drawable.state_list_item_background_dark_theme)
            if (draw != null) {
                draw.alpha = 80
            }
            layoutBackground.background = draw
            layoutBackground.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.akcient_dark
                )
            )
        }
    }

    fun changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
        currentTheme: CurrentTheme?,
        layoutBackground: ConstraintLayout,
        context: Context
    ) {
        if (currentTheme?.themeId == 0) {
            val draw = ContextCompat.getDrawable(
                context,
                R.drawable.state_list_item_background_light_theme
            )
            if (draw != null) {
                draw.alpha = 40
            }
            layoutBackground.background = draw
            layoutBackground.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.akcient_light
                )
            )
        } else {
            val draw =
                ContextCompat.getDrawable(context, R.drawable.state_list_item_background_dark_theme)
            if (draw != null) {
                draw.alpha = 40
            }
            layoutBackground.background = draw
            layoutBackground.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.akcient_dark
                )
            )
        }
    }

    fun changeColorBackgroundItemsWhichReadyToEnteredDragAndDropEventTarget(
        currentTheme: CurrentTheme?,
        layoutBackground: ConstraintLayout,
        context: Context
    ) {
        if (currentTheme?.themeId == 0) {
            val draw = ContextCompat.getDrawable(
                context,
                R.drawable.state_list_item_background_light_theme
            )
            layoutBackground.background = draw
            layoutBackground.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.akcient_light
                )
            )
        } else {
            val draw =
                ContextCompat.getDrawable(context, R.drawable.state_list_item_background_dark_theme)
            layoutBackground.background = draw
            layoutBackground.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.akcient_dark
                )
            )
        }
    }

}


