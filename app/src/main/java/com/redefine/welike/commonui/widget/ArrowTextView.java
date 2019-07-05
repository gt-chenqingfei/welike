package com.redefine.welike.commonui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;

public class ArrowTextView extends android.support.v7.widget.AppCompatTextView {
    static final int ARROW_UP = 1;
    static final int ARROW_DOWN = 2;
    static final int ARROW_NONE = 0;

    public ArrowTextView(Context context) {
        super(context);
        init();
    }

    public ArrowTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArrowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private Path arrow;
    private Paint bgPaint;
    private RectF bg;

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setElevation(5f);
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    int height = view.getHeight();   //获取View的高度
                    int width = view.getWidth();     //获取View的宽度
                    int px = ScreenUtils.dip2Px(6);
                    outline.setRoundRect(getPaddingLeft() - px * 2, getPaddingTop() - px, width - getPaddingRight() + px * 2, height - getPaddingBottom() + px, height / 2);
                }
            });
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setForeground(getResources().getDrawable(outValue.resourceId, getContext().getTheme()));
            }
        }
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);   //设置画笔抗锯齿
        bgPaint.setStrokeWidth(2);    //设置线宽
        bgPaint.setColor(getResources().getColor(R.color.guide_bg));  //设置线的颜色
    }

    public void play(final View target, final int arrowTop) {
        play(target, arrowTop,true);
    }
    private void play(final View target, final int arrowTop, final boolean needViewTreeObserver) {
        final ArrowTextView mine = this;
//        target.post(new Runnable() {
//            @Override
//            public void run() {
        int parentWidth = ((ViewGroup) getParent()).getWidth();
        int myWidth = getWidth();
        myWidth = (myWidth > parentWidth ? parentWidth : myWidth);
        int[] pos = new int[2];
        target.getLocationOnScreen(pos);
        int targetViewX = pos[0];
        int padding = targetViewX + target.getWidth() / 2 - myWidth / 2;
        if (padding < 0) {
            padding = 0;
        }
        if (padding + getWidth() > parentWidth) {
            padding = parentWidth - getWidth();
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mine.getLayoutParams();
        params.setMargins(padding, params.topMargin, params.rightMargin, params.bottomMargin);
        requestLayout();
        int x = targetViewX - padding + (target.getWidth()) / 2;
        if (arrowTop > ARROW_NONE) {
            setArrow(x, arrowTop == ARROW_DOWN);
        }
        if (needViewTreeObserver){
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    play(target, arrowTop,false);
                }
            });
        }
    }

    public void playAbove(final View target) {
        play(target, ARROW_UP);
    }

    public void playUnder(final View target) {
        play(target, ARROW_DOWN);
    }

    public void setArrow(final int padding, final boolean top) {
//        post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        int cx = padding;
        int height = getHeight();
        Path path = new Path();
        if (top) {
            int heightPadding = getPaddingTop();
            path.moveTo(cx, 0);// 三角形顶点
            path.lineTo(cx - heightPadding, heightPadding);   //三角形左边的点
            path.lineTo(cx + heightPadding, heightPadding);   //三角形右边的点
        } else {
            int heightPadding = getPaddingBottom();
            path.moveTo(cx, height);// 三角形顶点
            path.lineTo(cx - heightPadding, height - heightPadding);   //三角形左边的点
            path.lineTo(cx + heightPadding, height - heightPadding);   //三角形右边的点
        }
        path.close();
        arrow = path;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (bg == null) {
            int height = getHeight();   //获取View的高度
            int width = getWidth();     //获取View的宽度
            int px = ScreenUtils.dip2Px(6);
            bg = new RectF(getPaddingLeft() - px * 2, getPaddingTop() - px, width - getPaddingRight() + px * 2, height - getPaddingBottom() + px);
        }
        //框定文本显示的区域
        canvas.drawRoundRect(bg, getHeight() / 2, getHeight() / 2, bgPaint);
        if (arrow != null) {
            canvas.drawPath(arrow, bgPaint);
        }
        super.onDraw(canvas);
    }
}
