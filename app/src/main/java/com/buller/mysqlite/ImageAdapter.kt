package com.buller.mysqlite


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.constans.ContentConstants
import com.buller.mysqlite.db.ImageItem


class ImageAdapter(listMain: MutableList<ImageItem>, var context: Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var itemList = listMain


    class ImageViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imView)

        fun setImage(s: ImageItem) {
            imageView.setImageURI(Uri.parse(s.uri))

            itemView.setOnClickListener {
                val activity = context as AppCompatActivity
                val bundle = Bundle()
                val fragment = ImageFragment()
                bundle.putString(fragment.KEY,s.uri)
                fragment.arguments = bundle
                activity
                    .supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fmContent,fragment, ContentConstants.FRAGMENT_IMAGE)
                    .addToBackStack("edit")
                    .commit()
            }

//            itemView.setOnLongClickListener {
//
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater =LayoutInflater.from(context)
        val imageItem = inflater.inflate(R.layout.rc_item_image, parent, false)
        return ImageViewHolder(imageItem,context)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.setImage(itemList.get(position))
    }

    override fun getItemCount(): Int = itemList.size


//    fun removeItem(position: Int, dbManager: MyDbManager) {
//        dbManager.removeItemImageDb(itemList[position].id.toString())
//        itemList.removeAt(position)
//        notifyItemRangeChanged(0, itemList.size)
//        notifyItemRemoved(position)
//    }

}