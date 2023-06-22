package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.buller.mysqlite.utils.theme.BaseTheme
import com.buller.mysqlite.viewmodel.NotesViewModel
import com.dolatkia.animatedThemeManager.AppTheme
import com.dolatkia.animatedThemeManager.ThemeFragment

class ColorPikerBackgroundFragment(private val colorType: Int) : ThemeFragment() {
    private lateinit var binding: FragmentColorPikerBackgroundBinding
    val mNoteViewModel: NotesViewModel by activityViewModels()
    private lateinit var favColorAdapter: FavoriteColorAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPikerBackgroundBinding.inflate(inflater, container, false)
        initCurrentColorFieldObserver()
        initFavColorAdapter()
        initFavoriteColorsObserver()
        initColorPicker()
        initThemeObserver()
        binding.apply {

            clearBackgroundField.setOnClickListener {
                mNoteViewModel.changeColorField(colorType, 0)
            }
        }
        return binding.root
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        binding.apply {
            root.background.setTintList(ColorStateList.valueOf(theme.backgroundColor(requireContext())))
            textChangeColor.setTextColor(theme.textColor(requireContext()))
            clearBackgroundField.background.setTintList(ColorStateList.valueOf(theme.backgroundDrawer(requireContext())))
            clearBackgroundField.setColorFilter(theme.akcColor(requireContext()))
            val currentColorList = mNoteViewModel.editedColorsFields.value
            if (currentColorList != null) {
                if (currentColorList[colorType] == 0) {
                    viewChangeBackgroundColor.setCardBackgroundColor(
                        theme.backgroundColor(
                            requireContext()
                        )
                    )
                }
            }
        }
    }

    private fun initFavColorAdapter() = with(binding) {
        favColorAdapter = FavoriteColorAdapter(
            colorType,
            this@ColorPikerBackgroundFragment
        )
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, true)
        layoutManager.stackFromEnd = true
        rcFavColor.layoutManager = layoutManager
        rcFavColor.adapter = favColorAdapter
    }

    private fun initFavoriteColorsObserver() {
        mNoteViewModel.favColor.observe(viewLifecycleOwner) { listFavoritesColor ->
            favColorAdapter.submitList(listFavoritesColor)
            val position = binding.rcFavColor.adapter!!.itemCount - 1
            binding.rcFavColor.smoothScrollToPosition(position)
        }
    }

    private fun initCurrentColorFieldObserver() = with(binding) {
        mNoteViewModel.editedColorsFields.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) return@observe
            if (list[colorType] == 0) {
                viewChangeBackgroundColor.setCardBackgroundColor(resources.getColor(R.color.transparent,null))
            } else {
                viewChangeBackgroundColor.setCardBackgroundColor(list[colorType])
            }
        }
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

    private fun initThemeObserver(){
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner){currentTheme->
            favColorAdapter.themeChanged(currentTheme)
        }
    }
}