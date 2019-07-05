package com.redefine.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.redefine.commonui.R;

/**
 * Created by liwenbo on 2018/2/8.
 */

public class CircleBgImageView extends AppCompatImageView {
    private Paint mPaint;

    public CircleBgImageView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleBgImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleBgImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        int circleColor = getResources().getColor(R.color.app_color);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleBgImageView);
            circleColor = ta.getColor(R.styleable.CircleBgImageView_circle_color, circleColor);
            ta.recycle();
        }
        mPaint = new Paint();
        mPaint.setColor(circleColor);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int r = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r, mPaint);
        super.onDraw(canvas);
    }
}
