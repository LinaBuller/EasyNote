package com.easynote.domain.models

data class UserCredential(var email:String, var password:String)
{
    fun isNotEmptyEmail():Boolean{
        return email.isNotBlank()
    }

    fun isNotEmptyPassword():Boolean{
        return password.isNotBlank()
    }
}