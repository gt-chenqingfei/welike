package com.redefine.commonui.h5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gongguan on 2018/1/14.
 */

public class ProgressView extends View {

    private int progress = 1;
    private final static int HEIGHT = 5;
    private Paint paint;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);// 填充方式为描边
        paint.setStrokeWidth(HEIGHT);
        paint.setAntiAlias(true);// 抗锯齿
        paint.setDither(true);// 使用抖动效果
        paint.setColor(Color.BLACK);
    }

    /**
     * 设置进度
     *
     * @param progress 进度值
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth() * progress / 100, HEIGHT, paint);//画矩形从（0.0）开始到（progress,height）的区域
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

}
