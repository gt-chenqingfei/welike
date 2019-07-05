package com.redefine.welike.business.im.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by nianguowang on 2018/10/13
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mItemSpace;
    private int mSpanCount;

    public GridSpaceItemDecoration(int itemSpace, int spanCount) {
        this.mItemSpace = itemSpace;
        this.mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % mSpanCount; // item column

        outRect.left = column * mItemSpace / mSpanCount;
        outRect.right = mItemSpace - (column + 1) * mItemSpace / mSpanCount;
        if (position >= mSpanCount) {
            outRect.top = mItemSpace;
        }

    }
}
