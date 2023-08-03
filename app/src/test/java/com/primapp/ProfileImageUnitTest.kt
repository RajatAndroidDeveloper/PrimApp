package com.primapp

import com.primapp.cache.UserCache
import org.junit.Test

class ProfileImageUnitTest {
    @Test
    fun profileInAppropriateValidator_CorrectImageSample_ReturnsTrue() {
        assert(UserCache.getUser(PrimApp().applicationContext)?.isInappropriate ?: false)
    }
}