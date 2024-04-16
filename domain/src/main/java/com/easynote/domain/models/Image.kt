package com.easynote.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Image(

    var id: String = UUID.randomUUID().toString(),

    var foreignId: Long = 0L,

    var uri: String = "",

    var isNew: Boolean = true,

    override var position: Int = 0

) : Parcelable,ReorderableEntity {
}