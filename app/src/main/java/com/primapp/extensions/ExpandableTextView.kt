package com.primapp.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.primapp.R

@SuppressLint("AppCompatCustomView")
class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextView(context, attrs) {
    var originalText: CharSequence? = null
        private set
    private var trimmedText: CharSequence? = null
    private var bufferType: BufferType? = null
    private var trim = true
    private var trimLength: Int
    private fun setText() {
        super.setText(displayableText, bufferType)
    }

    private val displayableText: CharSequence?
        private get() = if (trim) trimmedText else originalText

    override fun setText(text: CharSequence?, type: BufferType) {
        originalText = text
        trimmedText = getTrimmedText(text)
        bufferType = type
        setText()
    }

    private fun getTrimmedText(text: CharSequence?): CharSequence? {
        return if (originalText != null && originalText!!.length > trimLength) {
            val readmore = SpannableString(READ_MORE)
            readmore.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),
                0,
                readmore.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS).append(readmore)
        } else {
            originalText
        }
    }

    fun setTrimLength(trimLength: Int) {
        this.trimLength = trimLength
        trimmedText = getTrimmedText(originalText)
        setText()
    }

    fun getTrimLength(): Int {
        return trimLength
    }

    companion object {
        private const val DEFAULT_TRIM_LENGTH = 200
        private const val ELLIPSIS = "....."
        private const val READ_MORE = "read more"
        private const val READ_LESS = "Read Less"
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        trimLength = typedArray.getInt(
            R.styleable.ExpandableTextView_trimLength,
            DEFAULT_TRIM_LENGTH
        )
        typedArray.recycle()
        setOnClickListener {
            trim = !trim
            setText()
            //requestFocusFromTouch()
        }
    }
}