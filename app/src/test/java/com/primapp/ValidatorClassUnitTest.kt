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
    fun emailValidator_InvalidEmailSpace_ReturnsFalse() {
        assertFalse(Validator.isEmailValid("abc @email.com"))
    }

    @Test
    fun emailValidator_NullEmail_ReturnsFalse() {
        assertFalse(Validator.isEmailValid(null))
    }

    @Test
    fun passwordValidator_NullPassword_ReturnsFalse(){
        assertFalse(Validator.isPasswordValid(null))
    }

    @Test
    fun passwordValidator_EmptyPassword_ReturnsFalse(){
        assertFalse(Validator.isPasswordValid(""))
    }

    @Test
    fun passwordValidator_ShortLengthPassword_ReturnsFalse(){
        assertFalse(Validator.isPasswordValid("aas12"))
    }

    @Test
    fun passwordValidator_ValidPassword_ReturnsTrue(){
        assertTrue(Validator.isPasswordValid("abcdesdf"))
    }

    @Test
    fun passwordValidator_ValidPasswordWithSpecialCharacter_ReturnsTrue(){
        assertTrue(Validator.isPasswordValid("abc12@#**"))
    }

    @Test
    fun usernameValidator_NullUsername_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid(null))
    }


    @Test
    fun usernameValidator_EmptyUsername_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid(""))
    }

    @Test
    fun usernameValidator_InvalidUsername_ShortLength_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid("abc"))
    }

    @Test
    fun usernameValidator_InvalidUsername_StartNumber_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid("988as"))
    }

    @Test
    fun usernameValidator_InvalidUsername_StartSpecialChar_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid("_asn88"))
    }

    @Test
    fun usernameValidator_InvalidUsername_StartPeriodChar_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid(".asnss"))
    }

    @Test
    fun usernameValidator_InvalidUsername_StartChar_ReturnsFalse(){
        assertFalse(Validator.isUsernameValid("abc@jjj"))
    }

    @Test
    fun usernameValidator_ValidUsername_StartChar_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("abcdef"))
    }

    @Test
    fun usernameValidator_ValidUsername_StartCapitalChar_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("AbcDef"))
    }

    @Test
    fun usernameValidator_ValidUsername_ContainsUnderscore_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("Abc_Def"))
    }

    @Test
    fun usernameValidator_ValidUsername_ContainsPeriods_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("Abc.def"))
    }

    @Test
    fun usernameValidator_ValidUsername_ContainsNumber_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("Abc99def"))
    }

    @Test
    fun usernameValidator_ValidUsername_ContainsAll1_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("Abc_de.f_88"))
    }

    @Test
    fun usernameValidator_ValidUsername_ContainsAll2_ReturnsTrue(){
        assertTrue(Validator.isUsernameValid("Abc.de_f99"))
    }



}