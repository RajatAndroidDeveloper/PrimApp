package com.primapp.utils

import javax.inject.Inject

class ErrorFields @Inject constructor() {

    var errorEmail: String? = null

    var errorPassword: String? = null

    var errorOldPassword: String? = null

    var errorConfirmPassword: String? = null

    var errorName: String? = null

    var errorOTP: String? = null

    var errorAmount: String? = null
}