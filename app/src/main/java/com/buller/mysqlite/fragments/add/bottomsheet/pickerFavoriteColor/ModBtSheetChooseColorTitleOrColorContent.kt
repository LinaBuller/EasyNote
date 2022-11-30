package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.DialogModalBottomSheetLobsterpickerBinding
import com.buller.mysqlite.model.FavoriteColor
import com.buller.mysqlite.viewmodel.NotesViewModel

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModBtSheetChooseColorTitleOrColorContent(
    private val listCurrentColorTitleFromAddList: List<Int>
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: DialogModalBottomSheetLobsterpickerBinding
    private var onColorSelectedListener: OnColorSelectedListener? = null
    private lateinit var mNoteViewModel: NotesViewModel
    private lateinit var favColorAdapter: FavoriteColorAdapter
    private var colorTitle = 0
    private var colorContent = 0
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNoteViewModel = ViewModelProvider(requireActivity())[NotesViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnColorSelectedListener) {
            onColorSelectedListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        initFavColorAdapter()
        initFavoriteColorsObserver()
        visibleSupportButton()
        initColorPicker()

        ibDeleteColorTitle.setOnClickListener {
            colorTitle = clearBackground(rb1)

        }
        ibDeleteColorContent.setOnClickListener {
            colorContent = clearBackground(rb2)
        }

        initCurrentColor()

        ibCleanAll.setOnClickListener {
            colorTitle = clearBackground(rb1)
            colorContent = clearBackground(rb2)
        }

        ibAddInFavoritesTitle.setOnClickListener {
            addFavorites(rb1)
            rcFavColor.layoutManager?.scrollToPosition(favColorAdapter.list.size-1);
        }

        ibAddInFavoritesContent.setOnClickListener {
            addFavorites(rb2)
            rcFavColor.layoutManager?.scrollToPosition(favColorAdapter.list.size-1);
        }

        saveColor.setOnClickListener {
            onColorSelectedListener?.onColorSelected(colorTitle, colorContent)
            dismiss()
        }
    }

    private fun addFavorites(radioButton: RadioButton) = with(binding) {
        var color = 0
        if (radioButton.id == rb1.id) {
            color = if (colorTitle == 0) {
                listCurrentColorTitleFromAddList[0]
            } else {
                colorTitle
            }
        } else if (radioButton.id == rb2.id) {
            color = if (colorContent == 0) {
                listCurrentColorTitleFromAddList[1]
            } else {
                colorContent
            }
        }
        if (color != 0) {
            mNoteViewModel.addFavoritesColors(listOf(FavoriteColor(0, color)))
        } else {
            Toast.makeText(requireContext(), "You don't entered color", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initFavColorAdapter() = with(binding) {
        favColorAdapter = FavoriteColorAdapter(requireContext())
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        layoutManager.stackFromEnd = true
        rcFavColor.layoutManager = layoutManager
        rcFavColor.adapter = favColorAdapter
    }

    private fun initFavoriteColorsObserver() {
        mNoteViewModel.favColor.observe(viewLifecycleOwner) { listFavoritesColor ->
            favColorAdapter.submitList(listFavoritesColor)
        }
    }

    private fun clearBackground(radioButton: RadioButton): Int {
        radioButton.background.mutate()
        radioButton.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.custom_button_background)
        return 0
    }

    private fun initCurrentColor() = with(binding) {
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
    }

    private fun visibleSupportButton() = with(binding) {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (rb1.id == checkedId) {
                ibDeleteColorContent.visibility = View.GONE
                ibAddInFavoritesContent.visibility = View.GONE
                ibDeleteColorTitle.visibility = View.VISIBLE
                ibAddInFavoritesTitle.visibility = View.VISIBLE
            } else if (rb2.id == checkedId) {
                ibDeleteColorTitle.visibility = View.GONE
                ibAddInFavoritesTitle.visibility = View.GONE
                ibDeleteColorContent.visibility = View.VISIBLE
                ibAddInFavoritesContent.visibility = View.VISIBLE
            }
        }
    }

    private fun initColorPicker() = with(binding) {

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
                picker: ColorSeekBar<IntegerHSLColor>, color: IntegerHSLColor, value: Int
            ) {
                val h = color.floatH
                val s = color.floatS
                val l = color.floatL
                val a = color.intA

                if (rb1.isChecked) {
                    colorTitle = Color.HSVToColor(a, floatArrayOf(h, s, l))
                    rb1.background.mutate()
                    rb1.background.setTint(colorTitle)

                } else if (rb2.isChecked) {
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
    }

    companion object {
        const val TAG = "ModalBottomSheetLobsterpicker"
    }

    interface OnColorSelectedListener {
        fun onColorSelected(colorTitle: Int, colorContent: Int)
    }
}