package com.primapp.extensions

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun showSuccess(context: Context, msg: String) {
    Toasty.success(context, msg).show()
}

fun showError(context: Context, msg: String) {
    Toasty.error(context, msg).show()
}

fun showInfo(context: Context, msg: String) {
    Toasty.info(context, msg).show()
}

fun showWarning(context: Context, msg: String) {
    Toasty.warning(context, msg).show()
}