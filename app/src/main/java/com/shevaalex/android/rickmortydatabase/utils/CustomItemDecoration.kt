package com.shevaalex.android.rickmortydatabase.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.shevaalex.android.rickmortydatabase.R


class CustomItemDecoration(
        private val minimalSpacing: Int,
        private val spanCount: Int
) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager
        val position = parent.getChildLayoutPosition(view)
        if (layoutManager is GridLayoutManager) {
            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                val screenWidthPx = getScreenWidthPx(view.context)
                val rvItemWidthPx = getDimensPx(view.context, R.dimen.item_episode_location_width)
                val spaceToSpread = screenWidthPx - rvItemWidthPx * spanCount
                val spacing = spaceToSpread / (spanCount + 1)
                outRect.top = minimalSpacing
                outRect.bottom = minimalSpacing
                //double column layout spacing
                if (spanCount <= 2) {
                    //right column spacing
                    if (position % spanCount != 0) {
                        outRect.left = spacing / 2
                        outRect.right = spacing
                    }
                    //left column spacing
                    else {
                        outRect.left = spacing
                        outRect.right = spacing / 2
                    }
                }
                //multi column spacing
                else {
                    outRect.left = spacing / 2
                    outRect.right = spacing / 2
                }
            }
            if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
                val screenHeightPx = getScreenHeightPx(view.context)
                val bottomNavHeightPx = getActionBarHeightPx(view.context)
                val statusBarHeightPx = getDimensPx(view.context, R.dimen.status_bar_height_standard)
                val rvItemHeightPx = getDimensPx(view.context, R.dimen.item_episode_location_height)
                val spaceToSpread =
                        screenHeightPx - bottomNavHeightPx - statusBarHeightPx - (rvItemHeightPx * spanCount)
                val spacing = spaceToSpread / (spanCount + 1)
                outRect.left = minimalSpacing
                outRect.right = minimalSpacing
                //double row spacing
                if (spanCount <= 2) {
                    //bottom row spacing
                    if (position % spanCount != 0) {
                        outRect.top = spacing / 2
                        outRect.bottom = spacing
                    }
                    //top row spacing
                    else {
                        outRect.top = spacing
                        outRect.bottom = spacing / 2
                    }
                }
                //multi row spacing
                else {
                    outRect.top = spacing / 2
                    outRect.bottom = spacing / 2
                }
            }
        }
    }

}