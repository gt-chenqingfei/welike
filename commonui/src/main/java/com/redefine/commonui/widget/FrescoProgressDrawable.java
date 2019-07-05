package com.redefine.commonui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.drawee.drawable.DrawableUtils;
import com.redefine.commonui.R;
import com.redefine.foundation.utils.ScreenUtils;

/**
 * Created by nianguowang on 2018/7/3
 */
public class FrescoProgressDrawable extends Drawable {

    private Paint mPaint;
    private int mRadius;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private int mMax = 10000;
    private int mLevel = 1;
    private RectF mShowRect;

    public FrescoProgressDrawable(Context context) {
        mRadius = ScreenUtils.dip2Px(12);
        mDrawableWidth = ScreenUtils.dip2Px(200);
        mDrawableHeight = ScreenUtils.dip2Px(200);
        int circleColor = context.getResources().getColor(R.color.main_orange_dark);
        mPaint = new Paint();
        mPaint.setColor(circleColor);
        mPaint.setStrokeWidth(ScreenUtils.dip2Px(3));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mShowRect == null) {
            mShowRect = new RectF(mDrawableWidth/2 - mRadius, mDrawableHeight/2 - mRadius, mDrawableWidth/2 + mRadius, mDrawableHeight/2 + mRadius);
        }
        canvas.drawArc(mShowRect, 0, 0, false, mPaint);
        canvas.drawArc(mShowRect, -90, ((float) mLevel / mMax) * 360, false, mPaint);
    }

    public void setSize(int width, int height) {
        if(width < mRadius || height < mRadius) {
            throw new RuntimeException("width and height should bigger than progressbar radius, width=" + width + ",height=" + height + ",progressbar radius=" + mRadius);
        }
        mDrawableWidth = width;
        mDrawableHeight = height;
    }

    public void setProgressBarSize(int radius) {
        if(mRadius > mDrawableHeight || mRadius > mDrawableWidth) {
            throw new RuntimeException("Drawable width and Drawable height should bigger than progressbar radius, Drawable width=" + mDrawableWidth + ",Drawable height=" + mDrawableHeight + ",progressbar radius=" + mRadius);
        }
        mRadius = radius;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(mPaint.getColor());
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        if(level > 0 && level < 10000) {
            invalidateSelf();
            return true;
        }else {
            return false;
        }
    }
}
