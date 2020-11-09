package com.primapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    const val SEND_DOB_FORMAT = "yyyy/MM/dd"
    const val DOB_FORMAT = "MMM dd, yyyy"

    fun getDateFromPicker(calendar: Calendar, format: String? = SEND_DOB_FORMAT): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
    }

    fun getDateFromMillis(millis: Long?, format: String? = DOB_FORMAT): String {
        return if (millis == null || millis == 0L) "" else SimpleDateFormat(
            format,
            Locale.getDefault()
        ).format(Date(millis))
    }
}