package com.buller.mysqlite.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.fragments.constans.FragmentConstants
import com.buller.mysqlite.model.Image
import com.squareup.picasso.Picasso


class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var itemList = ArrayList<Image>()

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(inflater.inflate(R.layout.rc_item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = itemList[position]
        val height = if (position == 1 || position == (itemList.size - 1)) {
            150
        } else {
            300
        }

// Picasso.get().load(item.uri).resize((sizeScreen/2.5).toInt(), height).centerCrop().into(holder.imageView)
        Picasso.get()
            .load(item.uri)
            .placeholder(R.drawable.ic_broken_image_glade)
            .into(holder.imageView)


        holder.itemView.setOnClickListener { view ->
            val bundle = Bundle()
            bundle.putParcelable(FragmentConstants.IMAGE_TO_VIEW, item)
            view.findNavController().navigate(R.id.action_addFragment_to_imageFragment, bundle)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(listItems: List<Image>?) {
        if (listItems != null) {
            itemList.clear()
            itemList.addAll(listItems)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        itemList.clear()
        notifyDataSetChanged()
    }
}