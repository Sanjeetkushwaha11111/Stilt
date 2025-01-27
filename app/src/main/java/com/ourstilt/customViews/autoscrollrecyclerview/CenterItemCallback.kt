package com.ourstilt.customViews.autoscrollrecyclerview

interface CenterItemCallback {
    fun onScrollFinished(visibleItemPosition: Int)
    fun onScrolled(dx: Int)
}