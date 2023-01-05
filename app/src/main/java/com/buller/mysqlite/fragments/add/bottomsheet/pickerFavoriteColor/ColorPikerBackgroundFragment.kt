package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentColorPikerBackgroundBinding
import com.buller.mysqlite.model.FavoriteColor
import com.buller.mysqlite.viewmodel.NotesViewModel

class ColorPikerBackgroundFragment(private val colorType: Int) : Fragment() {
    private lateinit var binding: FragmentColorPikerBackgroundBinding
    lateinit var mNoteViewModel: NotesViewModel
    private lateinit var favColorAdapter: FavoriteColorAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPikerBackgroundBinding.inflate(inflater, container, false)

        initCurrentColorFieldObserver()
        initFavColorAdapter()
        initFavoriteColorsObserver()
        initColorPicker()

        binding.apply {

            clearBackgroundField.setOnClickListener {
               mNoteViewModel.changeColorField(colorType, 0)
                //viewChangeBackgroundColor.setCardBackgroundColor(resources.getColor(R.color.cardview_light_background,null))
            }
//            favoriteColors.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    rcFavColor.slideAnimation(SlideDirection.LEFT, SlideType.SHOW, 400)
//                } else {
//                    rcFavColor.slideAnimation(SlideDirection.RIGHT, SlideType.HIDE, 400)
//                }
//            }
//            createNewColor.setOnCheckedChangeListener { _, isChecked ->
//
//                if (isChecked) {
//                        layoutSliders.visibility = View.VISIBLE
//                } else {
//                        layoutSliders.visibility = View.GONE
//                    }
//                }

//            addColorToFavorite.setOnClickListener {
//                addFavorites()
//            }
        }
        return binding.root
    }

    private fun initFavColorAdapter() = with(binding) {
        favColorAdapter = FavoriteColorAdapter(
            colorType,
            this@ColorPikerBackgroundFragment
        )
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        layoutManager.stackFromEnd = true
        rcFavColor.layoutManager = layoutManager
        rcFavColor.adapter = favColorAdapter
    }

    private fun initFavoriteColorsObserver() {
        mNoteViewModel.favColor.observe(viewLifecycleOwner) { listFavoritesColor ->
            favColorAdapter.submitList(listFavoritesColor)
            val position = binding.rcFavColor.adapter!!.itemCount-1
            binding.rcFavColor.smoothScrollToPosition(position)
        }
    }

    private fun initCurrentColorFieldObserver() = with(binding) {
        mNoteViewModel.editedColorsFields.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) return@observe
            if(list[colorType]==0){
                viewChangeBackgroundColor.setCardBackgroundColor(resources.getColor(R.color.transparent))
            }else{
                viewChangeBackgroundColor.setCardBackgroundColor(list[colorType])
            }
        }
    }

    private fun addFavorites() = with(binding) {
        val selectedColor = mNoteViewModel.editedColorsFields.value!![colorType]

        if (selectedColor == 0) {
            Toast.makeText(requireContext(), "You haven't selected a color", Toast.LENGTH_SHORT)
                .show()
            return
        }

        var found = false
        val favList = mNoteViewModel.favColor.value
        favList!!.forEach { favColor ->
            if (favColor.number == selectedColor) {
                found = true
            }
        }
        if (found) {
            Toast.makeText(requireContext(), "It's a favorite color already", Toast.LENGTH_SHORT)
                .show()
            return
        }

        mNoteViewModel.addFavoritesColors(listOf(FavoriteColor(0, selectedColor)))
        rcFavColor.visibility = View.VISIBLE
        rcFavColor.layoutManager?.scrollToPosition(favColorAdapter.list.size - 1)
        rcFavColor.adapter!!.notifyDataSetChanged()

    }

    private fun initColorPicker() = with(binding) {

        hueSeekBar.mode = HSLColorPickerSeekBar.Mode.MODE_HUE
        hueSeekBarSaturation.mode = HSLColorPickerSeekBar.Mode.MODE_SATURATION
        hueSeekBarLightness.mode = HSLColorPickerSeekBar.Mode.MODE_LIGHTNESS
        hueSeekBar.coloringMode = HSLColorPickerSeekBar.ColoringMode.PURE_COLOR

        val group = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(hueSeekBar, hueSeekBarSaturation, hueSeekBarLightness)
        }

        group.addListener(object :
            ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>, color: IntegerHSLColor, value: Int
            ) {
                val h = color.floatH
                val s = color.floatS
                val l = color.floatL

                val selectedColor = Color.HSVToColor(floatArrayOf(h, s, l))
                mNoteViewModel.changeColorField(colorType, selectedColor)
            }

            override fun onColorPicked(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {
                return
            }

            override fun onColorPicking(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {
                return
            }
        })
    }

}