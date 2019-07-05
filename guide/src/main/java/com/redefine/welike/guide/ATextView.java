package com.redefine.welike.guide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class ATextView extends android.support.v7.widget.AppCompatTextView {
    public ATextView(Context context) {
        super(context);
        init();
    }

    public ATextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ATextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private Path arrow;
    private Paint bgPaint;
    private RectF bg;

    private void init() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);   //设置画笔抗锯齿
        bgPaint.setStrokeWidth(2);    //设置线宽
        bgPaint.setColor(getResources().getColor(R.color.guide_bg));  //设置线的颜色
    }

    public void setArrow(final int padding, final boolean top) {
        post(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bg == null) {
            int height = getHeight();   //获取View的高度
            int width = getWidth();     //获取View的宽度
            int px = dip2Px(6);
            bg = new RectF(getPaddingLeft() - px * 2, getPaddingTop() - px, width - getPaddingRight() + px * 2, height - getPaddingBottom() + px);
        }
        //框定文本显示的区域
        canvas.drawRoundRect(bg, 30, 30, bgPaint);
        if (arrow != null) {
            canvas.drawPath(arrow, bgPaint);
        }
        super.onDraw(canvas);
    }

    private static int dip2Px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
