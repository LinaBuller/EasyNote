package com.buller.mysqlite.utils.theme

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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

    fun changeBackgroundToCurrentNoteTitleCardView(themeId: Int,currentNote: Note, cardView:CardView, context: Context) {
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
    fun changeBackgroundToCurrentNoteContentCardView(themeId: Int,currentNote: Note, cardView:CardView, context: Context) {
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
            0 -> textView.setTextColor( ContextCompat.getColor(context,R.color.dark_gray))
            1 -> textView.setTextColor(ContextCompat.getColor(context,R.color.grey))
        }
    }

    fun changeCommentText(themeId: Int, textView: TextView, context: Context) {
        when (themeId) {
            0 -> textView.setTextColor(ContextCompat.getColor(context,R.color.dark_gray))
            1 -> textView.setTextColor(ContextCompat.getColor(context,R.color.grey))
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
}


