package com.buller.mysqlite.fragments.add

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.EditTextNoteUtil
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.squareup.picasso.Picasso
import java.io.File


class ImageAdapter(
    val currentImageItem: ImageItem,
    private val dragImage: OnDragImageToAnotherImageItem
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(), ItemMoveCallback.ItemTouchHelperContract {

    val differ = AsyncListDiffer(this, callback)
    var getActionMode: (() -> ActionMode?)? = null
    var onChangeSelectedList: (() -> Unit)? = null
    private var theme: CurrentTheme? = null


    companion object {
        val callback = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.uri == newItem.uri
            }
        }
    }

    inner class ImageViewHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imView)
        val cardView: CardView = itemView.findViewById(R.id.materialCardView)

        fun setData(image: Image) {

            val file = File(image.uri)
            Picasso.get()
                .load(file).fit()
                .placeholder(R.drawable.ic_broken_image_glade)
                .into(this.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(
            inflater.inflate(R.layout.rc_item_image, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.itemView.tag = position
        holder.itemView.setOnClickListener { view ->
            if (getActionMode?.invoke() != null) {
                onChangeSelectedList?.invoke()
            } else {
                val bundle = Bundle()
                bundle.putParcelable(FragmentConstants.IMAGE_TO_VIEW, item)
                view.findNavController()
                    .navigate(R.id.action_addFragment_to_imageFragment, bundle)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (getActionMode?.invoke() != null) {
                return@setOnLongClickListener false
            } else {
                val clipData = ClipData.newHtmlText("text", item.uri, "")
                val shadowBuilder = View.DragShadowBuilder(it)
                it.startDragAndDrop(clipData, shadowBuilder, it, 0)
                return@setOnLongClickListener true
            }
        }
        holder.itemView.setOnDragListener(DragListener(holder.context, dragImage, theme))
        holder.setData(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(listItems: List<Image>?) = differ.submitList(listItems)

    fun themeChanged(currentTheme: CurrentTheme?) {
        theme = currentTheme
        notifyDataSetChanged()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val list = differ.currentList.toMutableList()
        val oldItem = list.removeAt(fromPosition)
        list.add(toPosition, oldItem)

        val max: Int = maxOf(fromPosition, toPosition)
        val min: Int = minOf(fromPosition, toPosition)
        for (i in min..max) {
            list[i].position = i
        }
        differ.submitList(list)
    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {
        //val currentThemeId = theme!!.themeId
//        if (currentThemeId == 0) {
//            if (myViewHolder != null) {
//                changeItemFromCurrentTheme(1, myViewHolder as ImageViewHolder)
//            }
//        } else {
//            if (myViewHolder != null) {
//                changeItemFromCurrentTheme(0, myViewHolder as ImageViewHolder)
//            }
//        }
    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {
//        val currentThemeId = theme!!.themeId
//        if (currentThemeId == 0) {
//            if (myViewHolder != null) {
//                changeItemFromCurrentTheme(0, myViewHolder as ImageViewHolder)
//            }
//        } else {
//            if (myViewHolder != null) {
//                changeItemFromCurrentTheme(1, myViewHolder as ImageViewHolder)
//            }
//        }
    }

    private fun changeItemFromCurTheme(currentThemeId: Int, holder: ImageViewHolder) {
//        if (holder is MultiItemAdapter.TextHolder) {
//            DecoratorView.changeText(currentThemeId, holder.editText, holder.context)
//            DecoratorView.changeImageView(currentThemeId, holder.dragIcon, holder.context)
//            DecoratorView.changeItemsBackground(currentThemeId,holder.layoutTextItem,holder.context)
//
//        }
//        if (holder is MultiItemAdapter.ImageHolder){
//            DecoratorView.changeImageView(currentThemeId, holder.dragIcon, holder.context)
//            DecoratorView.changeItemsBackground(currentThemeId,holder.layoutImageItem,holder.context)
//        }
    }

//    class DragListener(
//        val context: Context,
//        private val drag: OnDragImageToAnotherImageItem
//    ) : View.OnDragListener {
//
//        override fun onDrag(v: View?, event: DragEvent?): Boolean {
//
//            when (event?.action) {
//
//                DragEvent.ACTION_DRAG_STARTED -> {
//                    val viewSource = event.localState as View
//                    viewSource.alpha = 0.5f
//                    Log.d("msg", "Action is DragEvent.ACTION_DRAG_STARTED $event")
//
//                }
//
//                DragEvent.ACTION_DRAG_EXITED -> {
//                    val viewSource = event.localState as View
//                    Log.d("msg", "Action is DragEvent.ACTION_DRAG_EXITED $event")
//                    val drawSource = ContextCompat.getDrawable(
//                        context,
//                        R.drawable.state_list_item_background_light_theme
//                    )
//                    drawSource?.alpha = 80
//                    val sourceBackground = viewSource.parent?.parent?.parent as ConstraintLayout
//                    sourceBackground.background = drawSource
//                    sourceBackground.background.setTint(Color.GREEN)
//
//                    val targetBackground = v?.parent?.parent?.parent as ConstraintLayout
//                    val drawTarget = ContextCompat.getDrawable(
//                        context,
//                        R.drawable.state_list_item_background_light_theme
//                    )
//                    drawTarget?.alpha = 80
//                    targetBackground.background = drawTarget
//                    targetBackground.background.setTint(Color.RED)
//                }
//
//                DragEvent.ACTION_DRAG_ENTERED -> {
//                    val viewSource = event.localState as View
//                    Log.d("msg", "Action is DragEvent.ACTION_DRAG_ENTERED $event")
//                    val drawSource = ContextCompat.getDrawable(
//                        context, R.drawable.state_list_item_background_light_theme
//                    )
//                    drawSource?.alpha = 80
//                    val sourceBackground = viewSource.parent?.parent?.parent as ConstraintLayout
//                    sourceBackground.background = drawSource
//                    sourceBackground.background.setTint(Color.GREEN)
//
//                    val targetBackground = v?.parent?.parent?.parent as ConstraintLayout
//                    val drawTarget = ContextCompat.getDrawable(
//                        context,
//                        R.drawable.state_list_item_background_light_theme
//                    )
//                    drawTarget?.alpha = 150
//                    targetBackground.background = drawTarget
//                    targetBackground.background.setTint(Color.RED)
//                }
//
//                DragEvent.ACTION_DROP -> {
//                    Log.d("msg", "Action is DragEvent.ACTION_DROP")
//                    val viewSource = event.localState as View
//
//
//                    val source = viewSource.parent as RecyclerView
//                    val adapterSource = source.adapter as ImageAdapter
//                    val imageItemSource = adapterSource.currentImageItem
//                    val positionSource = viewSource.tag as Int
//                    val imageSource = adapterSource.differ.currentList[positionSource]
//                    val sourceList = adapterSource.differ.currentList.toMutableList()
//
//
//                    val target = v?.parent as RecyclerView
//                    val adapterTarget = target.adapter as ImageAdapter
//                    val positionTargetForImage = v.tag as Int
//                    val imageItemTarget = adapterTarget.currentImageItem
//
//
//                    if (imageItemSource != imageItemTarget) {
//                        drag.removeSourceImage(imageSource, imageItemSource)
//                        drag.setImageFromTarget(
//                            imageSource,
//                            positionTargetForImage,
//                            imageItemTarget
//                        )
//                        return true
//                    } else {
//                        return false
//                    }
//                }
//
//                DragEvent.ACTION_DRAG_ENDED -> {
//                    Log.d("msg", "Action is DragEvent.ACTION_DRAG_ENDED $event")
//                    when (event.result) {
//                        true -> {
//
//                            Toast.makeText(context, "The drop was handled", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//
//                        else -> {
//                            val viewSource = event.localState as View
//                            viewSource.alpha = 1.0f
//                            Toast.makeText(context, "The drop didn't work", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }
//
//            }
//            return true
//        }
//
//    }
}