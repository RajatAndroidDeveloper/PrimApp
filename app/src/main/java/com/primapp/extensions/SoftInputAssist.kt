package com.primapp.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout

class SoftInputAssist(activity: Activity) {
    private var rootView: View?
    private var contentContainer: ViewGroup?
    private var viewTreeObserver: ViewTreeObserver? = null
    private val listener = OnGlobalLayoutListener { possiblyResizeChildOfContent() }
    private val contentAreaOfWindowBounds: Rect = Rect()
    private val rootViewLayout: FrameLayout.LayoutParams
    private var usableHeightPrevious = 0
    fun onPause() {
        if (viewTreeObserver!!.isAlive) {
            viewTreeObserver!!.removeOnGlobalLayoutListener(listener)
        }
    }

    fun onResume() {
        if (viewTreeObserver == null || !viewTreeObserver!!.isAlive) {
            viewTreeObserver = rootView!!.viewTreeObserver
        }
        viewTreeObserver!!.addOnGlobalLayoutListener(listener)
    }

    fun onDestroy() {
        rootView = null
        contentContainer = null
        viewTreeObserver = null
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer!!.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow: Int = contentAreaOfWindowBounds.bottom
        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout.height = usableHeightNow
            rootView!!.layout(
                contentAreaOfWindowBounds.left,
                contentAreaOfWindowBounds.top,
                contentAreaOfWindowBounds.right,
                contentAreaOfWindowBounds.bottom
            )
            rootView!!.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    init {
        contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
        rootView = contentContainer!!.getChildAt(0)
        rootViewLayout = rootView?.getLayoutParams() as FrameLayout.LayoutParams
    }
}