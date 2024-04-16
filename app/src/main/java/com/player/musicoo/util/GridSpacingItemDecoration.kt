package com.player.musicoo.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val context: Context,
    private val spacing: Int,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (spanCount > 1) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }
    }
}

