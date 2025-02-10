package com.mystilt.customViews.autoscrollrecyclerview

interface CenterItemCallback {
    fun onScrollFinished(visibleItemPosition: Int)
    fun onScrolled(dx: Int)
}