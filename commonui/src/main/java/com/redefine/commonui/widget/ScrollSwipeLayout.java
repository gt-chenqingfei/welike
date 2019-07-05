package com.redefine.commonui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.daimajia.swipe.SwipeLayout;

/**
 * Created by liwenbo on 2018/4/19.
 */

public class ScrollSwipeLayout extends SwipeLayout {
    public ScrollSwipeLayout(Context context) {
        super(context);
    }

    public ScrollSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollSwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return (getDragEdge() == DragEdge.Right && getOpenStatus() == Status.Open) || super.canScrollHorizontally(direction);
    }
}
