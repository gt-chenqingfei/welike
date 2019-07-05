package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwitchViewPager extends ViewPager {
    public boolean scroll = true;

    public SwitchViewPager(@NonNull Context context) {
        super(context);
    }

    public SwitchViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scroll && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scroll && super.onInterceptTouchEvent(ev);
    }
}
