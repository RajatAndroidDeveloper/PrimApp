package com.primapp.extensions

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.primapp.R

fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(object : URLSpan(u.url) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
    }
    text = spannable
}

fun NestedScrollView.smoothScrollTo(view: View) {
    var distance = view.top
    var viewParent = view.parent
    //traverses 10 times
    for (i in 0..9) {
        if ((viewParent as View) === this) break
        distance += (viewParent as View).top
        viewParent = viewParent.getParent()
    }
    smoothScrollTo(0, distance)
}


fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}


//Spannable String extension
fun getHighlightedText(@ColorInt color: Int, text: String): SpannableString {
    val moreText = SpannableString(text)
    moreText.setSpan(
        ForegroundColorSpan(color),
        0,
        moreText.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return moreText
}

fun SpannableStringBuilder.normal(text: String) {
    val startLength = this.length
    this.append(text)
    this.setSpan(StyleSpan(Typeface.NORMAL), startLength, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}