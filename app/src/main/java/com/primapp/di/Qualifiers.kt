package com.primapp.di

import javax.inject.Qualifier

/*
    It's is used to generate annotation that could help differentiate the same type of class object
    with different argument sent to it's constructor
*/

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CoroutineScopeIO

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class PrimAPI