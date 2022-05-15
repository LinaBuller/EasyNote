package com.buller.mysqlite.constans

object FirebaseAuthConstans {
    //"The email address is already in use by another account."
    const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"

    //"The email address is badly formatted."
    const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"

    //"The custom token format is incorrect. Please check the documentation."
    const val ERROR_INVALID_CUSTOM_TOKEN = "ERROR_INVALID_CUSTOM_TOKEN"

    //"The custom token corresponds to a different audience."
    const val ERROR_CUSTOM_TOKEN_MISMATCH = "ERROR_CUSTOM_TOKEN_MISMATCH"

    //"The supplied auth credential is malformed or has expired."
    const val ERROR_INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"

    //"The password is invalid or the user does not have a password."
    const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"

    //"The supplied credentials do not correspond to the previously signed in user."
    const val ERROR_USER_MISMATCH = "ERROR_USER_MISMATCH"

    //"This operation is sensitive and requires recent authentication. Log in again before retrying this request."
    const val ERROR_REQUIRES_RECENT_LOGIN = "ERROR_REQUIRES_RECENT_LOGIN"

    //"An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."
    const val ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL =
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"

    //"This credential is already associated with a different user account."
    const val ERROR_CREDENTIAL_ALREADY_IN_USE = "ERROR_CREDENTIAL_ALREADY_IN_USE"

    //"The user account has been disabled by an administrator."
    const val ERROR_USER_DISABLED = "ERROR_USER_DISABLED"

    //"The user\'s credential is no longer valid. The user must sign in again."
    const val ERROR_USER_TOKEN_EXPIRED = "ERROR_USER_TOKEN_EXPIRED"

    //"There is no user record corresponding to this identifier. The user may have been deleted."
    const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"

    //"The user\'s credential is no longer valid. The user must sign in again."
    const val ERROR_INVALID_USER_TOKEN = "ERROR_INVALID_USER_TOKEN"

    //"This operation is not allowed. You must enable this service in the console."
    const val ERROR_OPERATION_NOT_ALLOWED = "ERROR_OPERATION_NOT_ALLOWED"

    //"The given password is invalid."
    const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"

    //"An email address must be provided."
    const val ERROR_MISSING_EMAIL = "ERROR_MISSING_EMAIL"
}