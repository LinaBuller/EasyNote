package com.easynote.domain.models

import android.os.Parcelable
import codes.side.andcolorpicker.model.IntegerHSLColor
import kotlinx.parcelize.Parcelize

@Parcelize
class ColorWithHSL(var color:Int, var h:Float,var s:Float,var l:Float): Parcelable {
}