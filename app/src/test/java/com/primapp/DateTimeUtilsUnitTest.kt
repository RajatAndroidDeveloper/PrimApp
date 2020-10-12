package com.primapp

import com.primapp.utils.DateTimeUtils
import junit.framework.Assert.*
import org.junit.Test
import java.util.*

class DateTimeUtilsUnitTest {

    @Test
    fun check_getDateFromMillis_ForNullValue_ReturnEmptyString() {
        assertEquals("", DateTimeUtils.getDateFromMillis(null))
    }

    @Test
    fun check_getDateFromMillis_ForZeroValue_ReturnEmptyString() {
        assertEquals("", DateTimeUtils.getDateFromMillis(0L))
    }

    @Test
    fun check_getDateFromMillis_ForDefaultFormat_ReturnFormattedString() {
        assertEquals("10/12/2020", DateTimeUtils.getDateFromMillis(1602509409622))
    }
}