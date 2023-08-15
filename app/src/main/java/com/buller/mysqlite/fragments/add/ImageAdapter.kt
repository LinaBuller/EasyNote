package com.buller.mysqlite.fragments.add

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
import com.buller.mysqlite.fragments.categories.ItemMoveCallback
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.easynote.domain.models.Image
import com.squareup.picasso.Picasso
import java.io.File


class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {
    private val differ = AsyncListDiffer(this, callback)
    var getActionMode: (() -> ActionMode?)? = null
    var onChangeSelectedList: (() -> Unit)? = null

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
        private val imageView: ImageView = itemView.findViewById(R.id.imView)

        fun setData(image: Image) {

            val file = File(image.uri)

            Picasso.get()
                .load(file).fit()
                .placeholder(R.drawable.ic_broken_image_glade)
                .into(this.imageView)

            imageView.setOnClickListener { view ->
                if (getActionMode?.invoke() != null) {
                    onChangeSelectedList?.invoke()
                } else {
                    val bundle = Bundle()
                    bundle.putParcelable(FragmentConstants.IMAGE_TO_VIEW, image)
                    view.findNavController()
                        .navigate(R.id.action_addFragment_to_imageFragment, bundle)
                }
            }
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
        holder.setData(item)
    }

    override fun getItemCount(): Int = differ.currentList.size


    fun submitList(listItems: List<Image>?) {
        differ.submitList(listItems)
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {
        TODO("Not yet implemented")
    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {
        TODO("Not yet implemented")
    }

}