package com.primapp.constants

import com.google.android.exoplayer2.upstream.cache.SimpleCache

interface Constants {
    companion object {
        const val VIDEO_LIST: String = "VIDEO_LIST"
        var simpleCache: SimpleCache? = null
    }
}