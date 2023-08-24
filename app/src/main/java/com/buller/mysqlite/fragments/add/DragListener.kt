package com.buller.mysqlite.fragments.add

import android.content.ClipDescription
import android.content.Context
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.DecoratorView
import com.easynote.domain.models.CurrentTheme

class DragListener(
    val context: Context,
    private val drag: OnDragImageToAnotherImageItem,
    val theme: CurrentTheme?
) :
    View.OnDragListener {
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        val viewSource = event?.localState as View
        val source = viewSource.parent as RecyclerView
        val adapterSource = source.adapter as ImageAdapter
        val imageItemSource = adapterSource.currentImageItem
        val positionSource = viewSource.tag as Int

        val target = v?.parent as RecyclerView
        val adapterTarget = target.adapter as ImageAdapter
        val imageItemTarget = adapterTarget.currentImageItem

        when (event.action) {

            DragEvent.ACTION_DRAG_STARTED -> {
                Log.d("msg", "Action is DragEvent.ACTION_DRAG_STARTED")
                viewSource.alpha = 0.5f
                return if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                    val targetBackground = v.parent?.parent?.parent as ConstraintLayout
                    DecoratorView.changeColorBackgroundItemsWhichReadyToAcceptDragAndDropEventTarget(
                        theme,
                        targetBackground,
                        context
                    )
                    true
                } else {
                    false
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("msg", "Action is DragEvent.ACTION_DRAG_EXITED $event")

                val sourceBackground = viewSource.parent?.parent?.parent as ConstraintLayout
                DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                    theme,
                    sourceBackground,
                    context
                )

                val targetBackground = v.parent?.parent?.parent as ConstraintLayout
                DecoratorView.changeColorBackgroundItemsWhichReadyToAcceptDragAndDropEventTarget(
                    theme,
                    targetBackground,
                    context
                )
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                Log.d("msg", "Action is DragEvent.ACTION_DRAG_ENTERED $event")

                val sourceBackground = viewSource.parent?.parent?.parent as ConstraintLayout
                DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                    theme,
                    sourceBackground,
                    context
                )

                val targetBackground = v.parent?.parent?.parent as ConstraintLayout
                DecoratorView.changeColorBackgroundItemsWhichReadyToEnteredDragAndDropEventTarget(
                    theme,
                    targetBackground,
                    context
                )
            }

            DragEvent.ACTION_DROP -> {
                Log.d("msg", "Action is DragEvent.ACTION_DROP")

                val imageSource = adapterSource.differ.currentList[positionSource]
                val positionTargetForImage = v.tag as Int

                return if (imageItemSource != imageItemTarget) {
                    drag.removeSourceImage(imageSource, imageItemSource)
                    drag.setImageFromTarget(
                        imageSource,
                        positionTargetForImage,
                        imageItemTarget
                    )
                    true
                } else {
                    false
                }

            }

            DragEvent.ACTION_DRAG_ENDED -> {
                Log.d("msg", "Action is DragEvent.ACTION_DRAG_ENDED $event")
                when (event.result) {
                    true -> {
                        Toast.makeText(context, "The drop was handled", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        viewSource.alpha = 1.0f
                        Toast.makeText(context, "The drop didn't work", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                val sourceBackground = viewSource.parent?.parent?.parent as ConstraintLayout
                DecoratorView.changeItemsBackgroundClear(theme, sourceBackground, context)
                val targetBackground = v.parent?.parent?.parent as ConstraintLayout
                DecoratorView.changeItemsBackgroundClear(theme, targetBackground, context)

            }

        }
        return true
    }
}