package com.easynote.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class BackgroundColor(var position:Int, var colorWithHSL: ColorWithHSL): Parcelable