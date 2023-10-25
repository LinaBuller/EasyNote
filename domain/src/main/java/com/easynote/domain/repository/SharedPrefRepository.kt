package com.easynote.domain.repository

interface SharedPrefRepository {
    fun getFirstUsages(): Boolean
    fun setIsFirstUsages(isFirst: Boolean)
    fun getPreferredTheme(): Boolean
    fun setPreferredTheme(preferredTheme: Boolean)
    fun getTypeList(): Boolean
    fun setTypeList(typeList: Boolean)
}