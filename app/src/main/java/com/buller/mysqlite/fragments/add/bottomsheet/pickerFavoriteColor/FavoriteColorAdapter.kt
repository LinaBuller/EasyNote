package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.buller.mysqlite.R

import com.buller.mysqlite.model.FavoriteColor

class FavoriteColorAdapter(
    private val colorType: Int,
    private val colorPikerBackgroundFragment: ColorPikerBackgroundFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val list = ArrayList<FavoriteColor>()

    inner class FavoriteColorHolder(val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val radioButton: CheckBox = itemView.findViewById(R.id.rb)
        private val cardView: CardView = itemView.findViewById(R.id.itemRcColor)
        fun setData(favoriteColor: FavoriteColor) {
            cardView.setCardBackgroundColor(favoriteColor.number)
        }
    }

    inner class AddFavoriteColorHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.addImage)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            return createFavColor(inflater, parent)
        } else {
            return addNewFavColor(inflater, parent)
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
            )
        )
    }

    private fun createFavColor(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): FavoriteColorHolder {
        return FavoriteColorHolder(
            inflater.inflate(R.layout.rc_item_favorite_color, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (position != list.size) {
            val colorToField = list[position]

            (holder as FavoriteColorHolder).setData(colorToField)


            holder.radioButton.setOnClickListener {

                val list = colorPikerBackgroundFragment.mNoteViewModel.editedColorsFields.value
                val arrayList = arrayListOf<Int>()
                if (list != null) {
                    arrayList.addAll(list)
                    arrayList[colorType] = colorToField.number
                    colorPikerBackgroundFragment.mNoteViewModel.editedColorsFields.value = arrayList
                }
            }

            colorPikerBackgroundFragment.mNoteViewModel.editedColorsFields.observe(
                colorPikerBackgroundFragment.viewLifecycleOwner
            ) { listEditedColor ->
                val colorEditedFields = listEditedColor[colorType]
                holder.radioButton.isChecked = colorToField.number == colorEditedFields
            }


            holder.radioButton.isLongClickable = true
            holder.radioButton.setOnLongClickListener {
                AlertDialog.Builder(colorPikerBackgroundFragment.requireContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure to delete?")
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        colorPikerBackgroundFragment.mNoteViewModel.deleteFavColor(list[position])
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, list.size)
                        dialog.dismiss()
                    }.setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
                true
            }
        } else {
            (holder as AddFavoriteColorHolder).itemView.setOnClickListener {
                Toast.makeText(
                    colorPikerBackgroundFragment.requireContext(),
                    "You add favorite color!",
                    Toast.LENGTH_SHORT
                ).show()
                addNewFavColor()

            }
        }
    }

    private fun addNewFavColor() {

        val selectedColor =
            colorPikerBackgroundFragment.mNoteViewModel.editedColorsFields.value!![colorType]

        if (selectedColor == 0) {
            Toast.makeText(
                colorPikerBackgroundFragment.requireContext(),
                "You haven't selected a color",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        var found = false
        val favList = colorPikerBackgroundFragment.mNoteViewModel.favColor.value
        favList!!.forEach { favColor ->
            if (favColor.number == selectedColor) {
                found = true
            }
        }
        if (found) {
            Toast.makeText(
                colorPikerBackgroundFragment.requireContext(),
                "It's a favorite color already",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        colorPikerBackgroundFragment.mNoteViewModel.addFavoritesColors(
            listOf(
                FavoriteColor(
                    0,
                    selectedColor
                )
            )
        )

        //rcFavColor.layoutManager?.scrollToPosition(favColorAdapter.list.size - 1)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) 1 else 0
    }


    override fun getItemCount(): Int = list.size + 1

    fun submitList(listFavoritesColor: List<FavoriteColor>?) {
        if (listFavoritesColor != null) {
            list.clear()
            list.addAll(listFavoritesColor)
            notifyDataSetChanged()
        }
    }

}
