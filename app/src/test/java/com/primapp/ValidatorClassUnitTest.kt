package com.primapp

import com.primapp.utils.Validator
import junit.framework.Assert.*
import org.junit.Test

class ValidatorClassUnitTest {
    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(Validator.isEmailValid("name@email.com"))
    }

    @Test
    fun emailValidatorWithNumbers_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(Validator.isEmailValid("name1233@email.com"))
    }

    @Test
    fun emailValidatorWithNumbersPeriods_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(Validator.isEmailValid("name.aa1233@email.com"))
    }

    @Test
    fun emailValidator_CorrectEmailSubDomain_ReturnsTrue() {
        assertTrue(Validator.isEmailValid("name@email.co.uk"))
    }

    @Test
    fun emailValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(Validator.isEmailValid("name@email"))
    }

    @Test
    fun emailValidator_InvalidEmailDoubleDot_ReturnsFalse() {
        assertFalse(Validator.isEmailValid("name@email..com"))
    }

    @Test
    fun emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(Validator.isEmailValid("@email.com"))
    }

    @Test
    fun emailValidator_EmptyString_ReturnsFalse() {
        assertFalse(Validator.isEmailValid(""))
    }

    @Test
    fun emailValidator_NullEmail_ReturnsFalse() {
        assertFalse(Validator.isEmailValid(null))
    }
}