package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nianguowang on 2018/10/14
 */
public class NoScrollableRecyclerView extends RecyclerView {

    public NoScrollableRecyclerView(Context context) {
        super(context);
    }

    public NoScrollableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }

}
