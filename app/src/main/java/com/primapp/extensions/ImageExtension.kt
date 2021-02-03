package com.primapp.extensions

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.primapp.R


fun ImageView.loadImageWithProgress(context: Context, url: String?) {
//    val circularProgressDrawable = CircularProgressDrawable(context)
//    circularProgressDrawable.strokeWidth = 5f
//    circularProgressDrawable.centerRadius = 30f
//    circularProgressDrawable.start()
//
//    val requestOptions = RequestOptions().apply {
//        placeholder(circularProgressDrawable)
//    }

    Glide.with(context)
        .load(url)
        // .apply(requestOptions)
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .into(this)
}

fun ImageView.loadImageWithFitCenter(context: Context, url: String?) {

    Glide.with(context)
        .load(url)
        .fitCenter()
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .into(this)
}

fun ImageView.loadImageWithRoundedCorners(context: Context, url: String?) {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    val requestOptions = RequestOptions().apply {
        transforms(CenterCrop(), RoundedCorners(10))
        placeholder(circularProgressDrawable)
    }

    Glide.with(context)
        .load(url)
        .apply(requestOptions)
        .into(this)
}

fun ImageView.loadCircularImage(context: Context, url: String?) {
    Glide.with(context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.drawable.placeholder_circle)
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


fun ImageView.loadCircularImageWithBorder(url: String?) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .placeholder(R.drawable.placeholder_circle)
        .error(R.drawable.placeholder_circle)
        .into(this)
}