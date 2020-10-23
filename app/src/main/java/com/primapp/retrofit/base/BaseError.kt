package com.primapp.retrofit.base

class BaseError {
    var code = 0
    var error = ""


    constructor()
    constructor(code: Int, message: String) {
        this.code = code
        this.error = message
    }
}