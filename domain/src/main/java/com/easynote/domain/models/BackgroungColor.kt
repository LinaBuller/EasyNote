package com.easynote.domain.models

import android.os.Parcelable
import codes.side.andcolorpicker.model.IntegerHSLColor
import kotlinx.parcelize.Parcelize

@Parcelize
class BackgroungColor(var position:Int, var colorWithHSL: ColorWithHSL):  Parcelable