package com.example.data.storage.repository

import com.easynote.domain.repository.SharedPrefRepository
import com.example.data.storage.sharedprefs.SharedPrefNoteManager

class SharedPrefRepositoryImpl(var sharedPrefNoteManager: SharedPrefNoteManager):SharedPrefRepository {
    override fun getFirstUsages(): Boolean {
       return sharedPrefNoteManager.getFirstUsages()
    }

    override fun setIsFirstUsages(isFirst: Boolean) {
      sharedPrefNoteManager.setIsFirstUsages(isFirst)
    }

    override fun getPreferredTheme(): Boolean {
       return sharedPrefNoteManager.getPreferredTheme()
    }

    override fun setPreferredTheme(preferredTheme: Boolean) {
      sharedPrefNoteManager.setPreferredTheme(preferredTheme)
    }

    override fun getTypeList(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setTypeList(typeList: Boolean) {
        TODO("Not yet implemented")
    }


}