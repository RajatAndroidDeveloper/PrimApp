package com.primapp.binding

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.primapp.R
import com.primapp.constants.CommunityFilterTypes
import com.primapp.extensions.loadCircularImage
import com.primapp.extensions.removeLinksUnderline
import com.primapp.model.auth.UserData
import com.primapp.model.category.CommunityData
import com.primapp.utils.DateTimeUtils
import com.primapp.utils.getPrettyNumber
import java.util.*


@BindingAdapter("isRequired")
fun markRequiredInRed(textInput: TextInputLayout, isRequired: Boolean? = false) {
    if (isRequired == true) {
        textInput.hint = "${textInput.hint} *"
    }
}

@BindingAdapter("errorText")
fun textInputErrorFieldBinding(textInput: TextInputLayout, errorMessage: String?) {
    textInput.error = errorMessage
    errorMessage?.let {
        textInput.requestFocus()
    }
}

@BindingAdapter("errorText")
fun checkBoxErrorFieldBinding(checkBox: MaterialCheckBox, errorMessage: String?) {
    checkBox.error = errorMessage
    errorMessage?.let {
        checkBox.requestFocus()
    }
}

@BindingAdapter("spannableText")
fun makeSpannableText(textView: TextView, text: String?) {

    text?.apply {
        val span1 = SpannableString(this)

        span1.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(textView.context, R.color.colorAccent)),
            0,
            this.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )

        span1.setSpan(StyleSpan(Typeface.BOLD), 0, this.length, SPAN_INCLUSIVE_INCLUSIVE)

        textView.text = TextUtils.concat(textView.text, " ", span1)
    }

}

@BindingAdapter("htmlText")
fun makeTermsPolicyText(textView: MaterialCheckBox, text: String?) {
    val htmlText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(text)
    }
    textView.text = htmlText
    textView.removeLinksUnderline()
    textView.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("htmlText")
fun makeTermsPolicyText(textView: TextView, text: String?) {
    val htmlText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    } else {
        Html.fromHtml(text)
    }
    textView.text = htmlText
    textView.removeLinksUnderline()
    textView.movementMethod = LinkMovementMethod.getInstance()
}


@BindingAdapter("loadCircularImage")
fun loadCircularImageFromUrl(imgView: ImageView, url: String?) {
    imgView.loadCircularImage(imgView.context, url)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("genderAndDobFormat")
fun genderAndDobFormatText(textView: TextView, user: UserData) {
    val dob = DateTimeUtils.getDateFromMillis(user.dateOfBirth)
    var text = ""

    if (user.genderValue.isNullOrEmpty() && dob.isEmpty()) {
        textView.visibility = View.GONE
    }

    if (!user.genderValue.isNullOrEmpty()) {
        text = user.genderValue
    }

    if (text.isNotEmpty())
        text = "$text | "

    if (dob.isNotEmpty()) {
        text = "$text$dob"
    }

    textView.text = text
}

@BindingAdapter("membersAndCreatedDate")
fun membersAndCreatedDate(textView: TextView, data: CommunityData?) {
    data?.let {
        textView.text =
            "${getPrettyNumber(it.totalActiveMember)} members | ${DateTimeUtils.convertServerTimeStamp(data.cdate)}"
    }
}

@BindingAdapter("joinStyle", "type")
fun joinButtonStyle(button: Button, isJoined: Boolean, type: String?) {
    if (type == CommunityFilterTypes.CREATED_COMMUNITY) {
        //Make the button as Edit
        button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_blue_filled)
        button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
        button.text = button.context.getString(R.string.edit)
    } else {
        if (isJoined) {
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_primary_grey_filled)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.black))
            button.typeface = ResourcesCompat.getFont(button.context, R.font.poppins_regular)
        } else {
            button.background = ContextCompat.getDrawable(button.context, R.drawable.button_light_accent_blue_outlined)
            button.setTextColor(ContextCompat.getColor(button.context, R.color.colorAccent))
        }
    }
}