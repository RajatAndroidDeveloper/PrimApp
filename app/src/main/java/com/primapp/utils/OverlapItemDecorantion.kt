package com.primapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class OverlapItemDecorantion() : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, vertOverlap, 0)
    }


    companion object {
        private const val vertOverlap = -40
    }
}