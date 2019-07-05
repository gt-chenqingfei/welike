package com.redefine.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.redefine.commonui.R;

/**
 * Created by liwenbo on 2018/2/8.
 */

public class CircleBgView extends View {
    private Paint mPaint;

    public CircleBgView(Context context) {
        super(context);
        initBg(context, null);
    }

    public CircleBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBg(context, attrs);
    }

    public CircleBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBg(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initBg(context, attrs);
    }

    private void initBg(Context context, @Nullable AttributeSet attrs) {
        int circleColor = getResources().getColor(R.color.app_color);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleBgView);
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
