package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.afollestad.materialdialogs.MaterialDialog
import com.buller.mysqlite.BaseBottomSheetFragment
import com.buller.mysqlite.DecoratorView
import com.buller.mysqlite.R
import com.buller.mysqlite.databinding.FragmentColorPikerBackgroundBinding
import com.buller.mysqlite.theme.BaseTheme
import com.dolatkia.animatedThemeManager.AppTheme
import com.easynote.domain.models.BackgroundColor
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.viewmodels.BaseViewModel
import com.easynote.domain.viewmodels.ColorPikerViewModel
import com.easynote.domain.viewmodels.NotesViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModBtSheetChooseColor(private val listColorGradient: List<BackgroundColor>) :
    BaseBottomSheetFragment(), OnChangeColorsFields {
    private lateinit var binding: FragmentColorPikerBackgroundBinding
    private val mNoteViewModel: NotesViewModel by activityViewModel()
    private val mColorPikerViewModel: ColorPikerViewModel by viewModel()

    var onSaveColorsFromCurrentNote: ((List<BackgroundColor>) -> Unit)? = null
    private lateinit var favColorAdapter: FavoriteColorAdapter
    private var wrapperDialog: Context? = null
    override val mBaseViewModel: BaseViewModel get() = mColorPikerViewModel

    //true - first gradient color, false - second gradient color
    var isFirstOrSecondGradient: Boolean? = null

    private var group: PickerGroup<IntegerHSLColor>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPikerBackgroundBinding.inflate(inflater, container, false)
        initThemeObserver()
        initCurrentColorObserver()
        initSelectorColorObserver()


        isFirstOrSecondGradient = true
        mColorPikerViewModel.setCheckedColor(isFirstOrSecondGradient!!)
        mColorPikerViewModel.setCurrentColors(listColorGradient)


        initFavoriteColorAdapter()
        initColorPicker()
        initFavoriteColorObserver()
        binding.apply {

            firstCheckbox.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(true)
            }
            cardViewFirstGradient.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(true)
            }

            secondCheckbox.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(false)
            }
            cardViewSecondGradient.setOnClickListener {
                mColorPikerViewModel.setCheckedColor(false)
            }

            saveSelectedColors.setOnClickListener {
                val currentColorList = mColorPikerViewModel.currentColorsList.value
                if (currentColorList != null) {

                    val first = currentColorList[0]
                    val second = currentColorList[1]

                    val adaptedList = currentColorList.toMutableList()

                    if (first.colorWithHSL.color == 267 || first.colorWithHSL.color == -1 || first.colorWithHSL.color == -16777216) {
                        adaptedList[0] = BackgroundColor(first.position, ColorWithHSL(0, 0F, 0F, 0F))
                    }

                    if (second.colorWithHSL.color == 267 || second.colorWithHSL.color == -1 || second.colorWithHSL.color == -16777216) {
                        adaptedList[1] = BackgroundColor(second.position, ColorWithHSL(0, 0F, 0F, 0F))
                    }
                    onSaveColorsFromCurrentNote?.invoke(adaptedList)
                }
                dismiss()
            }

            clearBackgroundField.setOnClickListener {
                val currentTheme = mNoteViewModel.currentTheme.value
                if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == true) {
                    mColorPikerViewModel.cleanSelectedColors(currentTheme!!, FIRST_COLOR)

                } else if (isFirstOrSecondGradient != null && isFirstOrSecondGradient == false) {
                    mColorPikerViewModel.cleanSelectedColors(currentTheme!!, SECOND_COLOR)
                }
                group?.setColor(createIntegerHSLColor(0F, 0F, 256F))
            }
        }
        initEventObservers()
        return binding.root
    }

    override fun syncTheme(appTheme: AppTheme) {
        val theme = appTheme as BaseTheme
        wrapperDialog = ContextThemeWrapper(requireContext(), theme.styleDialogTheme())
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

    private fun initCurrentColorObserver() = with(binding) {
        mColorPikerViewModel.currentColorsList.observe(viewLifecycleOwner) { listEditedColor ->
            if (listEditedColor.isNullOrEmpty()) return@observe

            val firstColor = listEditedColor[FIRST_COLOR].colorWithHSL.color
            val secondColor = listEditedColor[SECOND_COLOR].colorWithHSL.color

            val theme = mNoteViewModel.currentTheme.value
            //-16777216
            if (firstColor == 0 || firstColor == 267 || firstColor == -16777216) {
                DecoratorView.setBackgroundColorToCard(
                    theme,
                    cardViewFirstGradient,
                    requireContext()
                )
            } else {
                cardViewFirstGradient.setCardBackgroundColor(firstColor)
            }

            if (secondColor == 0 || secondColor == 267 || firstColor == -secondColor) {
                DecoratorView.setBackgroundColorToCard(
                    theme,
                    cardViewSecondGradient,
                    requireContext()
                )
            } else {
                cardViewSecondGradient.setCardBackgroundColor(secondColor)
            }

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(firstColor, secondColor)
            )

            gradientDrawable.cornerRadius = 50f;
            viewChangeBackgroundColor.background = gradientDrawable
        }
    }

    private fun initSelectorColorObserver() = with(binding) {
        mColorPikerViewModel.checkedColor.observe(viewLifecycleOwner) { isCheck ->

            val firstColor = mColorPikerViewModel.currentColorsList.value?.get(FIRST_COLOR)
            val secondColor = mColorPikerViewModel.currentColorsList.value?.get(SECOND_COLOR)

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
                if (firstColor != null) {
                    group?.setColor(
                        createIntegerHSLColor(
                            secondColor!!.colorWithHSL.h,
                            secondColor.colorWithHSL.s,
                            secondColor.colorWithHSL.l
                        )
                    )
                }
            }
        }
    }

    private fun initFavoriteColorAdapter() = with(binding) {
        favColorAdapter = FavoriteColorAdapter(this@ModBtSheetChooseColor)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, true)
        layoutManager.stackFromEnd = true
        binding.rcFavColor.layoutManager = layoutManager
        binding.rcFavColor.adapter = favColorAdapter
    }

    private fun initFavoriteColorObserver() {
        mColorPikerViewModel.favoriteColors.observe(viewLifecycleOwner) { listFavColor ->
            favColorAdapter.submitList(listFavColor)
        }
    }

    override fun onChangeEditedColorFromCheckbox(
        colorToField: FavoriteColor, holder: FavoriteColorAdapter.FavoriteColorHolder
    ) {
        if (isFirstOrSecondGradient == true) {
            val savedFavColor = BackgroundColor(
                FIRST_COLOR, ColorWithHSL(
                    colorToField.number, colorToField.h, colorToField.s, colorToField.l
                )
            )
            mColorPikerViewModel.setColorFromCurrentColorsList(savedFavColor)
        } else if (isFirstOrSecondGradient == false) {
            val savedFavColor = BackgroundColor(
                SECOND_COLOR, ColorWithHSL(
                    colorToField.number, colorToField.h, colorToField.s, colorToField.l
                )
            )
            mColorPikerViewModel.setColorFromCurrentColorsList(savedFavColor)
        }
        group?.setColor(
            createIntegerHSLColor(
                colorToField.h, colorToField.s, colorToField.l
            )
        )

    }

    override fun onDeleteFavColor(favoriteColor: FavoriteColor) {

        MaterialDialog(wrapperDialog!!).show {
            title(R.string.delete)
            message(R.string.delete_fav_color)
            icon(R.drawable.ic_delete)
            positiveButton(R.string.yes) { dialog ->
                mColorPikerViewModel.deleteFavoriteColor(favoriteColor)
                dialog.dismiss()
            }
            negativeButton(R.string.no) { dialog ->
                dialog.dismiss()
            }
        }
    }

    override fun onAddNewFavColor() {
        if (isFirstOrSecondGradient == true) {
            mColorPikerViewModel.setFavoriteColor(FIRST_COLOR)
        } else if (isFirstOrSecondGradient == false) {
            mColorPikerViewModel.setFavoriteColor(SECOND_COLOR)
        }

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


                if (isFirstOrSecondGradient == true) {
                    mColorPikerViewModel.setColorFromCurrentColorsList(
                        BackgroundColor(
                            FIRST_COLOR, ColorWithHSL(selectedColor, h, s, l)
                        )
                    )
                } else if (isFirstOrSecondGradient == false) {
                    mColorPikerViewModel.setColorFromCurrentColorsList(
                        BackgroundColor(
                            SECOND_COLOR, ColorWithHSL(selectedColor, h, s, l)
                        )
                    )
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