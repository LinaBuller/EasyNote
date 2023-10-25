package com.example.data.storage.sharedprefs

import android.content.Context
import android.content.SharedPreferences

private const val SHARED_PREFS_NAME = "shared_prefs_name"
private const val FIRST_USAGES = "first_usages"
private const val PREFERRED_THEME = "preferred_theme"
private const val KIND_OF_LIST = "kind_of_list"
private const val IS_BIO_AUTH = "is_bio_auth"

class SharedPrefNoteManager(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getFirstUsages(): Boolean {
        return sharedPreferences.getBoolean(FIRST_USAGES, true)
    }

    fun setIsFirstUsages(isFirst: Boolean) {
        editor.apply {
            putBoolean(FIRST_USAGES, isFirst)
            apply()
        }
    }

    fun getPreferredTheme(): Boolean {
        return sharedPreferences.getBoolean(PREFERRED_THEME, true)
    }

    fun setPreferredTheme(preferredTheme: Boolean) {
        editor.apply {
            putBoolean(PREFERRED_THEME, preferredTheme)
            apply()
        }
    }


    fun getTypeList(): Boolean {
        return sharedPreferences.getBoolean(KIND_OF_LIST, true)
    }

    fun setTypeList(typeList: Boolean) {
        editor.apply {
            putBoolean(KIND_OF_LIST, typeList)
            apply()
        }
    }

    fun getIsBioAuth(): Boolean {
        return sharedPreferences.getBoolean(IS_BIO_AUTH, false)
    }

    fun setIsBioAuth(isBioAuth: Boolean) {
        editor.apply {
            putBoolean(IS_BIO_AUTH, isBioAuth)
            apply()
        }
    }

    fun getPermissions() {

    }
}