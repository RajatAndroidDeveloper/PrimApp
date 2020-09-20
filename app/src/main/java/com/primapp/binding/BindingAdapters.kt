package com.primapp.binding

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.primapp.R


@BindingAdapter("isRequired")
fun markRequiredInRed(textInput: TextInputLayout, isRequired: Boolean? = false) {
    if (isRequired == true) {
        textInput.hint = "${textInput.hint} *"
    }
}

@BindingAdapter("errorText")
fun textInputErrorFieldBinding(textInput: TextInputLayout, errorMessage: String?) {
    textInput.error = errorMessage
}

@BindingAdapter("spannableText")
fun makeSpannableText(textView:TextView, text:String?){

    text?.apply{
        val span1 = SpannableString(this)

        span1.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(textView.context,R.color.colorAccent)),
            0,
            this.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )

        span1.setSpan(StyleSpan(Typeface.BOLD),0,this.length, SPAN_INCLUSIVE_INCLUSIVE)

        textView.text = TextUtils.concat(textView.text, " ", span1)
    }

}