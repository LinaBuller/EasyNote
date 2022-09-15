package com.buller.mysqlite.fragments.add.bottomsheet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.buller.mysqlite.databinding.DialogModalBottomSheetLobsterpickerBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModBtSheetChooseColorTitleOrColorContent(private val listCurrentColorTitleFromAddList: List<Int>) :
    BottomSheetDialogFragment() {
    private lateinit var binding: DialogModalBottomSheetLobsterpickerBinding
    private var onColorSelectedListener: OnColorSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnColorSelectedListener) {
            onColorSelectedListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DialogModalBottomSheetLobsterpickerBinding.inflate(inflater, container, false).also {
                (parentFragment as? OnColorSelectedListener)?.let {
                    onColorSelectedListener = it
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        var colorTitle = 0
        var colorContent = 0

        val currentColorTitleFromAddList = listCurrentColorTitleFromAddList[0]
        val currentColorContentFromAddList = listCurrentColorTitleFromAddList[1]

        if (currentColorTitleFromAddList != 0) {
            rb1.background.mutate()
            rb1.background.setTint(currentColorTitleFromAddList)
        }

        if (currentColorContentFromAddList != 0) {
            rb2.background.mutate()
            rb2.background.setTint(currentColorContentFromAddList)
        }

        hueSeekBar.mode = HSLColorPickerSeekBar.Mode.MODE_HUE
        hueSeekBarSaturation.mode = HSLColorPickerSeekBar.Mode.MODE_SATURATION
        hueSeekBarLightness.mode = HSLColorPickerSeekBar.Mode.MODE_LIGHTNESS
        hueSeekBar.coloringMode = HSLColorPickerSeekBar.ColoringMode.PURE_COLOR

        val group = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(hueSeekBar, hueSeekBarSaturation, hueSeekBarLightness, alphaSeekBar)
        }

        group.addListener(object :
            ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                val h = color.floatH
                val s = color.floatS
                val l = color.floatL
                val a = color.intA

                if (binding.rb1.isChecked) {
                    colorTitle = Color.HSVToColor(a, floatArrayOf(h, s, l))
                    rb1.background.mutate()
                    rb1.background.setTint(colorTitle)
                } else if (binding.rb2.isChecked) {
                    colorContent = Color.HSVToColor(a, floatArrayOf(h, s, l))
                    rb2.background.mutate()
                    rb2.background.setTint(colorContent)
                }
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

        saveColor.setOnClickListener {
            onColorSelectedListener?.onColorSelected(colorTitle, colorContent)
            dismiss()
        }
    }

    interface OnColorSelectedListener {
        fun onColorSelected(colorTitle: Int, colorContent: Int)
    }

    companion object {
        const val TAG = "ModalBottomSheetLobsterpicker"
    }
}