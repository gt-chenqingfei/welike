package com.redefine.commonui.widget.shadowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.redefine.commonui.R;
import com.redefine.foundation.utils.ScreenUtils;

/**
 * Created by liwenbo on 2018/2/22.
 */

public class ShadowDrawDelegate {

    private Drawable mRightDrawable;
    private Drawable mLeftDrawable;
    private Drawable mBottomDrawable;
    private Drawable mTopDrawable;
    private boolean isDrawTop;
    private boolean isDrawBottom;
    private boolean isDrawLeft;
    private boolean isDrawRight;
    private int offset;
    private final Rect tempRect = new Rect();
    private static int startColor = 0x0A000000;

    public ShadowDrawDelegate(Context context, AttributeSet attrs) {
        boolean isDrawTop = false;
        boolean isDrawBottom = false;
        boolean isDrawLeft = false;
        boolean isDrawRight = false;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShadowView);
            isDrawTop = ta.getBoolean(R.styleable.ShadowView_is_draw_top, false);
            isDrawBottom = ta.getBoolean(R.styleable.ShadowView_is_draw_bottom, false);
            isDrawLeft = ta.getBoolean(R.styleable.ShadowView_is_draw_left, false);
            isDrawRight = ta.getBoolean(R.styleable.ShadowView_is_draw_right, false);
            ta.recycle();
        }
        init(context, isDrawTop, isDrawBottom, isDrawLeft, isDrawRight);
    }

    public ShadowDrawDelegate(Context context, boolean isDrawTop, boolean isDrawBottom) {
        this(context, isDrawTop, isDrawBottom, false, false);
    }

    public ShadowDrawDelegate(Context context, boolean isDrawTop, boolean isDrawBottom, boolean isDrawLeft, boolean isDrawRight) {
        init(context, isDrawTop, isDrawBottom, isDrawLeft, isDrawRight);
    }

    private void init(Context context, boolean isDrawTop, boolean isDrawBottom, boolean isDrawLeft, boolean isDrawRight) {
        this.isDrawTop = isDrawTop;
        this.isDrawBottom = isDrawBottom;
        this.isDrawLeft = isDrawLeft;
        this.isDrawRight = isDrawRight;
        offset = ScreenUtils.dip2Px(context, 3);
        if (isDrawTop) {
            mTopDrawable = ScrimUtil.makeCubicGradientScrimDrawable(
                    startColor, //颜色
                    3, //渐变层数
                    Gravity.TOP); //起始方向
        }

        if (isDrawBottom) {
            mBottomDrawable = ScrimUtil.makeCubicGradientScrimDrawable(
                    startColor, //颜色
                    3, //渐变层数
                    Gravity.BOTTOM); //起始方向
        }

        if (isDrawLeft) {
            mLeftDrawable = ScrimUtil.makeCubicGradientScrimDrawable(
                    startColor, //颜色
                    3, //渐变层数
                    Gravity.LEFT); //起始方向
        }

        if (isDrawRight) {
            mRightDrawable = ScrimUtil.makeCubicGradientScrimDrawable(
                    startColor, //颜色
                    3, //渐变层数
                    Gravity.RIGHT); //起始方向
        }
    }

    public void draw(View view, Canvas canvas) {
        if (isDrawTop) {
            tempRect.set(0, 0, view.getWidth(), offset);
            mTopDrawable.setBounds(tempRect);
            mTopDrawable.draw(canvas);
        }
        if (isDrawLeft) {
            tempRect.set(0, 0, offset, view.getHeight());
            mLeftDrawable.setBounds(tempRect);
            mLeftDrawable.draw(canvas);
        }
        if (isDrawRight) {
            tempRect.set(view.getWidth() - offset, 0, view.getWidth(), view.getHeight());
            mRightDrawable.setBounds(tempRect);
            mRightDrawable.draw(canvas);
        }
        if (isDrawBottom) {
            tempRect.set(0, view.getHeight() - offset, view.getWidth(), view.getHeight());
            mBottomDrawable.setBounds(tempRect);
            mBottomDrawable.draw(canvas);
        }
    }
}
