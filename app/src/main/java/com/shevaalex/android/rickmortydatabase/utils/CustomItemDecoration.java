package com.shevaalex.android.rickmortydatabase.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;

public class CustomItemDecoration extends RecyclerView.ItemDecoration {
    private final Activity a;
    private boolean isOffsetLayout;

    public CustomItemDecoration (Activity a, boolean isOffsetLayout) {
        this.a = a;
        this.isOffsetLayout = isOffsetLayout;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int position = parent.getChildLayoutPosition(view);
        int spanCount;
        float density = DisplayMetricsUtils.getScreenDensity(a);
        final int topSpacing = Math.round((a.getResources().getDimensionPixelSize(R.dimen.item_episode_layout_spacing)*density) / 2);
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            spanCount = gridLayoutManager.getSpanCount();
            if (gridLayoutManager.getOrientation() == RecyclerView.VERTICAL) {
                float widthPixels = DisplayMetricsUtils.getScreenWidthPx(a);
                float rvItemWidth = a.getResources().getDimensionPixelSize(R.dimen.item_episode_image_width)
                        + a.getResources().getDimensionPixelSize(R.dimen.item_episode_text_width);
                float spaceToSpread = widthPixels - (rvItemWidth * spanCount);
                int spacing = Math.round(spaceToSpread / (spanCount+1));
                outRect.top = topSpacing;
                outRect.bottom = topSpacing;
                if (isOffsetLayout) {
                    if (position == 0) {
                        outRect.left = topSpacing;
                        outRect.right = topSpacing;
                    } else {
                        if (spanCount <= 2) {
                            if (position % spanCount == 0) {
                                outRect.left = spacing / 2;
                                outRect.right = spacing;
                            } else {
                                outRect.left = spacing;
                                outRect.right = spacing / 2;
                            }
                        } else {
                            if (position % spanCount == 0) {
                                outRect.left = spacing / 2;
                                outRect.right = spacing;
                            } else if (position % spanCount == 1) {
                                outRect.left = spacing;
                                outRect.right = spacing / 2;
                            } else {
                                outRect.left = spacing / 2;
                                outRect.right = spacing / 2;
                            }
                        }
                    }
                    return;
                }
                if (spanCount <= 2) {
                    if (position % spanCount != 0) {
                        outRect.left = spacing / 2;
                        outRect.right = spacing;
                    } else {
                        outRect.left = spacing;
                        outRect.right = spacing / 2;
                    }
                } else {
                    if (position % spanCount == 0) {
                        outRect.left = spacing;
                        outRect.right = spacing / 2;
                    } else if (position % spanCount == spanCount - 1) {
                        outRect.left = spacing / 2;
                        outRect.right = spacing;
                    } else {
                        outRect.left = spacing / 2;
                        outRect.right = spacing / 2;
                    }
                }
            } else {
                outRect.set(topSpacing, topSpacing, topSpacing, topSpacing);
            }
        }
    }
}
