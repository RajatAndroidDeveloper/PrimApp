package com.primapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    const val DOB_FORMAT = "MM/dd/yyyy"

    fun getDateFromPicker(calendar: Calendar, format: String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
    }
}