package com.buller.mysqlite.fragments.add

import android.content.ClipDescription
import android.content.Context
import android.view.DragEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.DecoratorView
import com.easynote.domain.models.CurrentTheme

class ImageItemDragListener(
    val context: Context, private val drag: OnDragImageToAnotherImageItem,
    val theme: CurrentTheme?, private val isParent: Boolean
) :
    View.OnDragListener {
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        val viewSource = event?.localState as View

        when (event.action) {

            DragEvent.ACTION_DRAG_STARTED -> {
                viewSource.alpha = 0.5f
                return if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                    val targetBackground = if (isParent) {
                        v?.parent?.parent as ConstraintLayout
                    } else {
                        v?.parent?.parent?.parent as ConstraintLayout
                    }
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
                val sourceBackground = viewSource.parent?.parent?.parent
                if (sourceBackground != null && sourceBackground is ConstraintLayout) {
                    DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                        theme,
                        sourceBackground,
                        context
                    )
                }
                val targetBackground: ConstraintLayout = if (isParent) {
                    v?.parent?.parent as ConstraintLayout
                } else {
                    v?.parent?.parent?.parent as ConstraintLayout
                }
                DecoratorView.changeColorBackgroundItemsWhichReadyToAcceptDragAndDropEventTarget(
                    theme,
                    targetBackground,
                    context
                )
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                val sourceBackground = viewSource.parent?.parent?.parent
                if (sourceBackground != null && sourceBackground is ConstraintLayout) {
                    DecoratorView.changeColorBackgroundItemsWhichReadyToExitedDragAndDropEventSource(
                        theme,
                        sourceBackground,
                        context
                    )
                }

                val targetBackground = if (isParent) {
                    v?.parent?.parent as ConstraintLayout
                } else {
                    v?.parent?.parent?.parent as ConstraintLayout
                }
                DecoratorView.changeColorBackgroundItemsWhichReadyToEnteredDragAndDropEventTarget(
                    theme,
                    targetBackground,
                    context
                )
            }

            DragEvent.ACTION_DROP -> {
                val source = viewSource.parent as RecyclerView
                val adapterSource = source.adapter as ImageAdapter
                val imageItemSource = adapterSource.currentImageItem
                val positionSource = viewSource.tag as Int
                val imageSource = adapterSource.differ.currentList[positionSource]

                val target = if (isParent) {
                    v as RecyclerView
                } else {
                    v?.parent as RecyclerView
                }

                val adapterTarget = target.adapter as ImageAdapter
                val imageItemTarget = adapterTarget.currentImageItem

                val positionTargetForImage = if (isParent) {
                    imageItemTarget.listImageItems.size
                } else {
                    v.tag as Int
                }

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

            DragEvent.ACTION_DRAG_LOCATION -> {
                return false
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                when (event.result) {
                    true -> {}

                    else -> {
                        viewSource.alpha = 1.0f
                    }
                }

                val sourceBackground = viewSource.parent?.parent?.parent
                if (sourceBackground != null && sourceBackground is ConstraintLayout) {
                    DecoratorView.changeItemsBackgroundClear(theme, sourceBackground, context)
                }

                val targetBackground: ConstraintLayout = if (isParent) {
                    v?.parent?.parent as ConstraintLayout
                } else {
                    v?.parent?.parent?.parent as ConstraintLayout
                }
                DecoratorView.changeItemsBackgroundClear(theme, targetBackground, context)

            }
        }
        return true
    }
}