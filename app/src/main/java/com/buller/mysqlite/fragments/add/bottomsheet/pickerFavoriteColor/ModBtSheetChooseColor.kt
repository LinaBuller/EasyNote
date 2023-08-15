package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentColorPikerBackgroundBinding
import com.buller.mysqlite.theme.BaseTheme
import com.buller.mysqlite.theme.ThemeBottomSheetFragment
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.models.BackgroungColor
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.viewmodels.ColorPikerViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModBtSheetChooseColor(private val listColorGradient: ArrayList<BackgroungColor>) :
    ThemeBottomSheetFragment(), OnChangeColorsFields{
    private lateinit var binding: FragmentColorPikerBackgroundBinding
    private val mNoteViewModel: NotesViewModel by activityViewModel()
    private val mColorPikerViewModel: ColorPikerViewModel by viewModel()
    var onSaveColorsFromCurrentNote: ((List<BackgroungColor>) -> Unit)? = null
    lateinit var favColorAdapter: FavoriteColorAdapter

    //true - first gradient color, false - second gradient color
    var isFirstOrSecondGradient: Boolean? = null

    private var group: PickerGroup<IntegerHSLColor>? = null


    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme

        binding.apply {
            root.background.setTintList(ColorStateList.valueOf(theme.backgroundColor(requireContext())))
            textChangeColor.setTextColor(theme.textColor(requireContext()))
            clearBackgroundField.background.setTintList(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
            clearBackgroundField.setColorFilter(theme.akcColor(requireContext()))


            saveSelectedColors.setColorFilter(theme.akcColor(requireContext()))
            saveSelectedColors.background.setTintList(
                ColorStateList.valueOf(
                    theme.backgroundDrawer(
                        requireContext()
                    )
                )
            )
        }
        if (dialog != null) {
            dialog!!.window!!.navigationBarColor = theme.setStatusBarColor(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPikerBackgroundBinding.inflate(inflater, container, false)
        initThemeObserver()
        initFavoriteColorAdapter()
        initColorPicker()
        isFirstOrSecondGradient = true
        mColorPikerViewModel.setCheckedColor(isFirstOrSecondGradient!!)
        mColorPikerViewModel.setCurrentColors(listColorGradient)
        initCurrentColorObserver()
        initSelectorColorObserver()
        initFavoriteColorObserver()
        binding.apply {

            firstCheckbox.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(true)
            }
            cardViewFirsfGradient.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(true)
            }
            secondCheckbox.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(false)
            }
            cardViewSecondGradient.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(false)
            }
            saveSelectedColors.setOnClickListener {
                if (mColorPikerViewModel.currentColorsList.value != null) {
                    onSaveColorsFromCurrentNote?.invoke(mColorPikerViewModel.currentColorsList.value!!)
                }
                dismiss()
            }

            clearBackgroundField.setOnClickListener {
                if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == true) {
                    mColorPikerViewModel.cleanSelectedColors(FIRST_COLOR)
                } else if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == false) {
                    mColorPikerViewModel.cleanSelectedColors(SECOND_COLOR)
                }
                group?.setColor(createIntegerHSLColor(0F, 0F, 256F))
            }
        }
        return binding.root
    }

    private fun initCurrentColorObserver() = with(binding) {
        mColorPikerViewModel.currentColorsList.observe(viewLifecycleOwner) { listEditedColor ->
            if (listEditedColor.isNullOrEmpty()) return@observe

            if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == true) {
                val firstColor = listEditedColor[FIRST_COLOR]
                cardViewFirsfGradient.setCardBackgroundColor(firstColor.colorWithHSL.color)


            } else if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == false) {
                val secondColor = listEditedColor[SECOND_COLOR]
                cardViewSecondGradient.setCardBackgroundColor(secondColor.colorWithHSL.color)
            }
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    listEditedColor[FIRST_COLOR].colorWithHSL.color,
                    listEditedColor[SECOND_COLOR].colorWithHSL.color
                )
            )

            gradientDrawable.cornerRadius = 50f;
            viewChangeBackgroundColor.background = gradientDrawable
        }
    }

    private fun initSelectorColorObserver() = with(binding) {
        mColorPikerViewModel.selectorColor.observe(viewLifecycleOwner) { isCheck ->

            val firstColor = mColorPikerViewModel.currentColorsList.value?.get(FIRST_COLOR)
            val secondColor = mColorPikerViewModel.currentColorsList.value?.get(SECOND_COLOR)

            if (firstColor != null && secondColor != null) {
                cardViewFirsfGradient.setCardBackgroundColor(firstColor.colorWithHSL.color)
                cardViewSecondGradient.setCardBackgroundColor(secondColor.colorWithHSL.color)
            }

            if (isCheck) {
                firstCheckbox.isChecked = true
                secondCheckbox.isChecked = false
                isFirstOrSecondGradient = true
                if (firstColor != null) {
                    group?.setColor(
                        createIntegerHSLColor(
                            firstColor.colorWithHSL.h,
                            firstColor.colorWithHSL.s,
                            firstColor.colorWithHSL.l
                        )
                    )
                }
            } else {
                secondCheckbox.isChecked = true
                firstCheckbox.isChecked = false
                isFirstOrSecondGradient = false
                if (secondColor != null) {
                    group?.setColor(
                        createIntegerHSLColor(
                            secondColor.colorWithHSL.h,
                            secondColor.colorWithHSL.s,
                            secondColor.colorWithHSL.l
                        )
                    )
                }
            }
        }
    }

    private fun initFavoriteColorAdapter() = with(binding){
        favColorAdapter = FavoriteColorAdapter(this@ModBtSheetChooseColor)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, true)
        layoutManager.stackFromEnd = true
        binding.rcFavColor.layoutManager = layoutManager
        binding.rcFavColor.adapter = favColorAdapter
    }

    private fun initFavoriteColorObserver(){
        mColorPikerViewModel.favoriteColors.observe(viewLifecycleOwner) { listFavColor ->
            favColorAdapter.submitList(listFavColor)
        }
    }

    override fun onChangeEditedColorFromCheckbox(
        colorToField: FavoriteColor,
        holder: FavoriteColorAdapter.FavoriteColorHolder
    ) {
        if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == true) {
            val savedFavColor = BackgroungColor(
                FIRST_COLOR,
                ColorWithHSL(
                    colorToField.number,
                    colorToField.h,
                    colorToField.s,
                    colorToField.l
                )
            )
            mColorPikerViewModel.setColorFromCurrentColorsList(savedFavColor)
        } else if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == false) {
            val savedFavColor = BackgroungColor(
                SECOND_COLOR,
                ColorWithHSL(
                    colorToField.number,
                    colorToField.h,
                    colorToField.s,
                    colorToField.l
                )
            )
            mColorPikerViewModel.setColorFromCurrentColorsList(savedFavColor)
        }
        group?.setColor(
            createIntegerHSLColor(
                colorToField.h,
                colorToField.s,
                colorToField.l
            )
        )

    }

    override fun onDeleteFavColor(favoriteColor: FavoriteColor) {

        AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure to delete?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                mColorPikerViewModel.deleteFavoriteColor(favoriteColor)
                dialog.dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onAddNewFavColor() {
        if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == true) {
            mColorPikerViewModel.setFavoriteColor(FIRST_COLOR)
        } else if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == false) {
            mColorPikerViewModel.setFavoriteColor(SECOND_COLOR)
        }
        favColorAdapter.notifyDataSetChanged()
        Toast.makeText(
            requireContext(),
            "You add favorite color!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initColorPicker() = with(binding) {

        hueSeekBar.mode = HSLColorPickerSeekBar.Mode.MODE_HUE
        hueSeekBarSaturation.mode = HSLColorPickerSeekBar.Mode.MODE_SATURATION
        hueSeekBarLightness.mode = HSLColorPickerSeekBar.Mode.MODE_LIGHTNESS
        hueSeekBar.coloringMode = HSLColorPickerSeekBar.ColoringMode.PURE_COLOR
        group = PickerGroup<IntegerHSLColor>().also {
            it.registerPickers(hueSeekBar, hueSeekBarSaturation, hueSeekBarLightness)
        }
        group!!.addListener(object :
            ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>, color: IntegerHSLColor, value: Int
            ) {
                val h = color.floatH
                val s = color.floatS
                val l = color.floatL

                val selectedColor = Color.HSVToColor(floatArrayOf(h, s, l))
                if (isFirstOrSecondGradient != null) {
                    if (isFirstOrSecondGradient == true) {
                        mColorPikerViewModel.setColorFromCurrentColorsList(
                            BackgroungColor(
                                FIRST_COLOR,
                                ColorWithHSL(selectedColor, h, s, l)
                            )
                        )
                    } else if (isFirstOrSecondGradient == false) {
                        mColorPikerViewModel.setColorFromCurrentColorsList(
                            BackgroungColor(
                                SECOND_COLOR,
                                ColorWithHSL(selectedColor, h, s, l)
                            )
                        )
                    }
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

    private fun createIntegerHSLColor(h: Float, s: Float, l: Float): IntegerHSLColor {
        val integerHSLColor = IntegerHSLColor()
        integerHSLColor.floatL = l
        integerHSLColor.floatS = s
        integerHSLColor.floatH = h
        return integerHSLColor
    }

    private fun initThemeObserver() {
        mNoteViewModel.currentTheme.observe(viewLifecycleOwner) { currentTheme ->
            favColorAdapter.themeChanged(currentTheme)
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetLobster-picker"
        const val FIRST_COLOR = 0
        const val SECOND_COLOR = 1
    }

}