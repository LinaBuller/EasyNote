package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.FavoriteColor

class FavoriteColorAdapter(
    changeColorsFields: OnChangeColorsFields
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentThemeAdapter: CurrentTheme? = null
    private var onChangeColorsFields: OnChangeColorsFields = changeColorsFields
    private val differ = AsyncListDiffer(this, callback)

    companion object {
        val callback = object : DiffUtil.ItemCallback<FavoriteColor>() {
            override fun areItemsTheSame(oldItem: FavoriteColor, newItem: FavoriteColor): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavoriteColor, newItem: FavoriteColor): Boolean {
                val color = oldItem.number == newItem.number
                val highlighter = oldItem.h == newItem.h
                val saturation = oldItem.s == newItem.s
                val lightness = oldItem.l == newItem.l


                return color &&
                        highlighter &&
                        saturation &&
                        lightness
            }
        }
    }

    class FavoriteColorHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.rb)
        val cardViewFavoriteColorHolder: CardView = itemView.findViewById(R.id.itemRcColor)
        fun setData(favoriteColor: FavoriteColor) {
            button.backgroundTintList = ColorStateList.valueOf(favoriteColor.number)
            //cardViewFavoriteColorHolder.setCardBackgroundColor(favoriteColor.number)
        }
    }

    class AddFavoriteColorHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.addImage)
        val cardViewAddFavoriteColorHolder: CardView = itemView.findViewById(R.id.itemRcColor)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            createFavColor(inflater, parent)
        } else {
            addNewFavColor(inflater, parent)
        }
    }

    private fun addNewFavColor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AddFavoriteColorHolder {
        return AddFavoriteColorHolder(
            inflater.inflate(
                R.layout.rc_item_add_favorite_color,
                parent,
                false
            ), parent.context
        )
    }

    private fun createFavColor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): FavoriteColorHolder {
        return FavoriteColorHolder(
            inflater.inflate(R.layout.rc_item_favorite_color, parent, false), parent.context
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (position !=  differ.currentList.size) {
            val colorToField =  differ.currentList[position]

            (holder as FavoriteColorHolder).setData(colorToField)
            changeItemFromCurrentTheme(currentThemeAdapter, holder.context, holder)



            holder.button.setOnClickListener {
                onChangeColorsFields.onChangeEditedColorFromCheckbox(colorToField,holder)
            }

            holder.button.isLongClickable = true
            holder.button.setOnLongClickListener {
                onChangeColorsFields.onDeleteFavColor(colorToField)
                true
            }
        } else {
            holder as AddFavoriteColorHolder
            changeItemFromCurrentTheme(currentThemeAdapter, holder.context, holder)
            holder.itemView.setOnClickListener {
                onChangeColorsFields.onAddNewFavColor()
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == differ.currentList.size) 1 else 0
    }


    override fun getItemCount(): Int = differ.currentList.size + 1

    fun submitList(listFavoritesColor: List<FavoriteColor>?) {
        if (listFavoritesColor != null) {
            differ.submitList(listFavoritesColor)
        }
    }

    fun themeChanged(currentTheme: CurrentTheme?) {
        currentThemeAdapter = currentTheme
        notifyDataSetChanged()
    }

    private fun changeItemFromCurrentTheme(
        currentTheme: CurrentTheme?,
        context: Context,
        holder: RecyclerView.ViewHolder
    ) {

        if (holder is AddFavoriteColorHolder) {
            DecoratorView.changeBackgroundCardView(
                currentTheme,
                holder.cardViewAddFavoriteColorHolder,
                context
            )
            DecoratorView.changeImageView(currentTheme, holder.imageView, context)
        }
        if (holder is FavoriteColorHolder) {
            DecoratorView.changeColorElevationCardView(
                currentTheme,
                holder.cardViewFavoriteColorHolder,
                context
            )
        }
    }
}
