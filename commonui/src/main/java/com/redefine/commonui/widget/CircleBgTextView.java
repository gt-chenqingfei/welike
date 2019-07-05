package com.redefine.commonui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.redefine.commonui.R;

/**
 * Created by liwenbo on 2018/2/8.
 */

@SuppressLint("AppCompatCustomView")
public class CircleBgTextView extends TextView {
    private Paint mPaint;

    public CircleBgTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleBgTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleBgTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleBgTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        int circleColor = getResources().getColor(R.color.red_point_bg_color);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleBgTextView);
            circleColor = ta.getColor(R.styleable.CircleBgTextView_circle_color, circleColor);
            ta.recycle();
        }
        mPaint = new Paint();
        mPaint.setColor(circleColor);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!TextUtils.equals("0", getText())) {
            int r = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r, mPaint);
        }
        super.onDraw(canvas);
    }
}
