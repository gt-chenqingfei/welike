package com.redefine.multimedia.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.R;

public class CircleDownloadProgressBar extends View {
    private Paint mPaint;
    private int mMax = 100;
    private int mProgress = 0;
    private RectF mShowRect;

    public CircleDownloadProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public CircleDownloadProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleDownloadProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleDownloadProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        int circleColor = getResources().getColor(R.color.main_orange_dark);
        int circleStroke = 3;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleDownloadProgressBar);
            circleColor = ta.getColor(R.styleable.CircleDownloadProgressBar_circle_color, circleColor);
            circleStroke = ta.getInteger(R.styleable.CircleDownloadProgressBar_circle_stroke, circleStroke);
            ta.recycle();
        }
        mPaint = new Paint();
        mPaint.setColor(circleColor);
        mPaint.setStrokeWidth(ScreenUtils.dip2Px(circleStroke));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    public void setProgress(int progress) {
        mProgress = Math.max(0, progress);
        invalidate();
    }

    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    public void setStrokeWidth(int dpValue) {
        if (mPaint != null) {
            mPaint.setStrokeWidth(ScreenUtils.dip2Px(dpValue));
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0 || mMax == 0) {
            return;
        }
        if (mShowRect == null) {
            mShowRect = new RectF(mPaint.getStrokeWidth() / 2, mPaint.getStrokeWidth() / 2, getMeasuredWidth() - mPaint.getStrokeWidth() / 2, getMeasuredHeight() - mPaint.getStrokeWidth() / 2);
        }
        canvas.drawArc(mShowRect, 0, 0, false, mPaint);
        canvas.drawArc(mShowRect, -90, ((float) mProgress / mMax) * 360, false, mPaint);
        super.onDraw(canvas);
    }
}
