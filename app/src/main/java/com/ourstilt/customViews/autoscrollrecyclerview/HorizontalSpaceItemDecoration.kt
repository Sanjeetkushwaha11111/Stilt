package com.ourstilt.customViews.autoscrollrecyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val horizontalSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Set half of the horizontal space to the left and right of each item
        val halfSpace = horizontalSpace / 2

        // Set left margin for all items except the first one
        if (itemPosition != 0) {
            outRect.left = halfSpace
        }

        // Set right margin for all items except the last one
        if (itemPosition != itemCount - 1) {
            outRect.right = halfSpace
        }
    }
}