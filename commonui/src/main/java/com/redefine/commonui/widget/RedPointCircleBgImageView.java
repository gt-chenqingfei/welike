package com.redefine.commonui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.redefine.commonui.R;
import com.redefine.foundation.utils.ScreenUtils;

/**
 * Created by liwenbo on 2018/2/8.
 */

public class RedPointCircleBgImageView extends CircleBgImageView {
    private Paint mRedPointPaint;
    private Paint mStokePaint;
    private Paint mNumberPoint;
    private int mRedPointNumber;
    private String mRedPointNumberStr;
    private int mReadPointR;
    private boolean mIsOnlyDrawRedPoint = false;
    private int mOffsetX;
    private int mOffsetY;

    public RedPointCircleBgImageView(Context context) {
        super(context);
        initRedPoint(context, null);
    }

    public RedPointCircleBgImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRedPoint(context, attrs);
    }

    public RedPointCircleBgImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRedPoint(context, attrs);
    }


    private void initRedPoint(Context context, AttributeSet attrs) {
        int redColor = getResources().getColor(R.color.red_point_bg_color);
        int stokeColor = getResources().getColor(R.color.app_color);
        mReadPointR = ScreenUtils.dip2Px(context, 9f);
        mIsOnlyDrawRedPoint = false;
        mOffsetX = 0;
        mOffsetY = 0;
        float stokeSize = 1;
        int numberColor = Color.WHITE;
        int numberSize = ScreenUtils.dip2Px(context, 11);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RedPointCircleBgImageView);
            redColor = ta.getColor(R.styleable.RedPointCircleBgImageView_red_point_color, redColor);
            stokeColor = ta.getColor(R.styleable.RedPointCircleBgImageView_red_point_stoke_color, stokeColor);
            stokeSize = ta.getDimension(R.styleable.RedPointCircleBgImageView_red_point_number_stoke_size, stokeSize);
            mIsOnlyDrawRedPoint = ta.getBoolean(R.styleable.RedPointCircleBgImageView_red_point_only_draw_red_point, false);
            mOffsetX = ta.getDimensionPixelOffset(R.styleable.RedPointCircleBgImageView_red_point_center_offset_x, 0);
            mOffsetY = ta.getDimensionPixelOffset(R.styleable.RedPointCircleBgImageView_red_point_center_offset_y, 0);
            mReadPointR = ta.getDimensionPixelOffset(R.styleable.RedPointCircleBgImageView_red_point_r, mReadPointR);
            numberColor = ta.getColor(R.styleable.RedPointCircleBgImageView_red_point_number_text_color, numberColor);
            numberSize = ta.getDimensionPixelSize(R.styleable.RedPointCircleBgImageView_red_point_number_text_size, numberSize);
            ta.recycle();
        }

        mRedPointPaint = new Paint();
        mRedPointPaint.setAntiAlias(true);
        mRedPointPaint.setColor(redColor);
        mStokePaint = new Paint();
        mStokePaint.setColor(stokeColor);
        mStokePaint.setAntiAlias(true);
        mStokePaint.setStrokeWidth(stokeSize);
        mStokePaint.setStyle(Paint.Style.STROKE);
        mNumberPoint = new Paint();
        mNumberPoint.setAntiAlias(true);
        mNumberPoint.setColor(numberColor);
        mNumberPoint.setTextAlign(Paint.Align.CENTER);
        mNumberPoint.setTextSize(numberSize);
    }

    public void setRedPointNumber(int redPointNumber) {
        mRedPointNumber = redPointNumber;
        mRedPointNumberStr = String.valueOf(redPointNumber);
        invalidate();
    }

    public void setOnlyShowRedPoint(boolean isShowRedPoint) {
        mIsOnlyDrawRedPoint = isShowRedPoint;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRedPointNumber > 0) {
            int r = mReadPointR;
            int centerX = getWidth();
            canvas.drawCircle(centerX, r, r, mRedPointPaint);
            canvas.drawCircle(centerX, r, r + mStokePaint.getStrokeWidth(), mStokePaint);
            Paint.FontMetrics fontMetrics = mNumberPoint.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;
            int baseLineY = (int) (r - top / 2 - bottom / 2);
            canvas.drawText(mRedPointNumberStr, centerX, baseLineY, mNumberPoint);
        } else if (mIsOnlyDrawRedPoint) {
            int r = mReadPointR;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            canvas.drawCircle(centerX + mOffsetX, centerY + mOffsetY, r, mRedPointPaint);
            canvas.drawCircle(centerX + mOffsetX, centerY + mOffsetY, r + mStokePaint.getStrokeWidth(), mStokePaint);
        }
    }
}
