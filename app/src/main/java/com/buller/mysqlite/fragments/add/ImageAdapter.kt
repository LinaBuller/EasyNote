package com.buller.mysqlite.fragments.add

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.ActionMode
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem
import com.squareup.picasso.Picasso
import java.io.File

class ImageAdapter(
    val currentImageItem: ImageItem,
    private val dragImage: OnDragImageToAnotherImageItem
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(){

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

        fun setData(image: Image) {
            var file = File(context.filesDir, "Multimedia")
            if (!file.exists()) file.mkdir()
            file = File(file, "img_${image.id}.jpg")
            Picasso.get().isLoggingEnabled = true
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
                startDrag(it,item)
                return@setOnLongClickListener true
            }
        }

        holder.itemView.setOnDragListener(
            ImageItemDragListener(
                holder.context,
                dragImage,
                theme,
                false
            )
        )

        holder.setData(item)
    }

    private fun startDrag(v: View, item: Image) {
        val clipData = ClipData.newHtmlText("text", item.uri, "")
        val shadowBuilder = CustomDragShadowBuilder(v)
        v.startDragAndDrop(clipData, shadowBuilder, v, 0)
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(listItems: List<Image>?) = differ.submitList(listItems)

    fun themeChanged(currentTheme: CurrentTheme?) {
        theme = currentTheme
        notifyDataSetChanged()
    }
}