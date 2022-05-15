package com.buller.mysqlite.utils

import android.os.Bundle
import android.os.Parcelable


object CollectionsToBundle {
    fun toBundle(input: Map<String?, Parcelable?>): Bundle {
        val output = Bundle()
        for (key in input.keys) {
            output.putParcelable(key, input[key])
        }
        return output
    }

    fun <T : Parcelable?> fromBundle(input: Bundle, c: Class<T>): Map<String, T?> {
        val output: MutableMap<String, T?> = HashMap()
        for (key in input.keySet()) {
            output[key] = c.cast(input.getParcelable(key))
        }
        return output
    }
}