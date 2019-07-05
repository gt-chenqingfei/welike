package com.redefine.welike.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.R;

/**
 * Created by MR on 2018/1/19.
 */

public class NoScrollViewPager extends ViewPager {
    private boolean mEnableScroll;

    public NoScrollViewPager(Context context) {
        super(context);
        setEnableScroll(false);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NoScrollViewPager);
        mEnableScroll = ta.getBoolean(R.styleable.NoScrollViewPager_scrollable, false);
        ta.recycle();
    }

    public void setEnableScroll(boolean isEnable) {
        mEnableScroll = isEnable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (mEnableScroll) {
                return super.onInterceptTouchEvent(ev);
            }
            return false;
        } catch (Throwable e) {
            LogUtil.e(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (mEnableScroll) {
                return super.onTouchEvent(ev);
            }
            return false;
        } catch (Throwable e) {
            LogUtil.e(e.getMessage());
        }
        return false;
    }
}
