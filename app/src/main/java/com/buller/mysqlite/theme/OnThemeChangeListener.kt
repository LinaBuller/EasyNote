package com.buller.mysqlite.theme

import com.easynote.domain.models.CurrentTheme

interface OnThemeChangeListener {
    fun onThemeChanged(currentTheme: CurrentTheme)
}