package com.player.musicoo.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {
    private val defaultSpacing: Int
    private val lastItemSpacing: Int

    init {
        val density = context.resources.displayMetrics.density
        defaultSpacing = (8 * density).toInt()
        lastItemSpacing = (16 * density).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        // 设置间隔，除了最后一个item外，其余item的右边都有间隔
        if (position < itemCount - 1) {
            outRect.right = defaultSpacing
        } else {
            outRect.right = lastItemSpacing
        }
    }
}
