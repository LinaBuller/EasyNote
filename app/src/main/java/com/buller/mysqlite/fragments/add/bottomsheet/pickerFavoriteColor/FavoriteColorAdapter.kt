package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R
import com.buller.mysqlite.model.FavoriteColor

class FavoriteColorAdapter(val context: Context) : RecyclerView.Adapter<FavoriteColorAdapter.FavoriteColorHolder>() {
    val list = ArrayList<FavoriteColor>()

    class FavoriteColorHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val cardFavColor: CardView = itemView.findViewById(R.id.cardFavColor)
        fun setData(favoriteColor: FavoriteColor) {
            cardFavColor.setCardBackgroundColor(favoriteColor.number)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteColorHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavoriteColorHolder(inflater.inflate(R.layout.rc_item_favorite_color, parent, false),context)
    }

    override fun onBindViewHolder(holder: FavoriteColorHolder, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun submitList(listFavoritesColor: List<FavoriteColor>?) {
        if (listFavoritesColor != null) {
            list.clear()
            list.addAll(listFavoritesColor)
            notifyDataSetChanged()
        }
    }

}
