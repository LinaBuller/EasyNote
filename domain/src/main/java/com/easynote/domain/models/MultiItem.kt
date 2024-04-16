package com.easynote.domain.models

open class MultiItem(override var position: Int = 0, open var isDeleted: Boolean = false) :
    ReorderableEntity {
}