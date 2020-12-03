package com.primapp.extensions

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.primapp.R


fun ImageView.loadCircularImage(context: Context, url: String?) {
    Glide.with(context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.mipmap.ic_launcher_round)
        .into(this)
}

fun ImageView.loadCircularImageWithoutCache(url: String?) {
    Glide.with(this.context)
        .asBitmap()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.mipmap.ic_launcher_round)
        .into(this)
}