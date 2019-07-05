package com.redefine.richtext.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ReplacementSpan;

import com.redefine.foundation.utils.ScreenUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A better implementation of image spans that also supports centering images against the text.
 * <p>
 * In order to migrate from ImageSpan, replace {@code new ImageSpan(drawable, alignment)} with
 * {@code new BetterImageSpan(drawable, BetterImageSpan.normalizeAlignment(alignment))}.
 * <p>
 * There are 2 main differences between BetterImageSpan and ImageSpan:
 * 1. Pass in ALIGN_CENTER to center images against the text.
 * 2. ALIGN_BOTTOM no longer unnecessarily increases the size of the text:
 * DynamicDrawableSpan (ImageSpan's parent) adjusts sizes as if alignment was ALIGN_BASELINE
 * which can lead to unnecessary whitespace.
 */
public class RichImageSpan extends ReplacementSpan {

    @IntDef({ALIGN_BASELINE, ALIGN_BOTTOM, ALIGN_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BetterImageSpanAlignment {
    }

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;

    /**
     * A helper function to allow dropping in BetterImageSpan as a replacement to ImageSpan,
     * and allowing for center alignment if passed in.
     */
    public static final @BetterImageSpanAlignment
    int normalizeAlignment(int alignment) {
        switch (alignment) {
            case DynamicDrawableSpan.ALIGN_BOTTOM:
                return ALIGN_BOTTOM;
            case ALIGN_CENTER:
                return ALIGN_CENTER;
            case DynamicDrawableSpan.ALIGN_BASELINE:
            default:
                return ALIGN_BASELINE;
        }
    }

    private int mWidth;
    private int mHeight;
    private Rect mBounds;
    private final int mAlignment;
    private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
    private final Drawable mDrawable;
    private final int margin = ScreenUtils.dip2Px(8);

    public RichImageSpan(Drawable drawable) {
        this(drawable, ALIGN_BASELINE);
    }

    public RichImageSpan(Drawable drawable, @BetterImageSpanAlignment int verticalAlignment) {
        mDrawable = drawable;
        mAlignment = verticalAlignment;
        updateBounds();
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Returns the width of the image span and increases the height if font metrics are available.
     */
    @Override
    public int getSize(
            Paint paint,
            CharSequence text,
            int start,
            int end,
            Paint.FontMetricsInt fontMetrics) {
        updateBounds();
        if (fontMetrics == null) {
            return mWidth;
        }

        int offsetAbove = getOffsetAboveBaseline(fontMetrics);
        int offsetBelow = mHeight + offsetAbove;
        fontMetrics.ascent = offsetAbove - margin;
        fontMetrics.top = offsetAbove - margin;
        fontMetrics.descent = offsetBelow + margin;
        fontMetrics.bottom = offsetBelow + margin;

        return mWidth;
    }

    @Override
    public void draw(
            Canvas canvas,
            CharSequence text,
            int start,
            int end,
            float x,
            int top,
            int y,
            int bottom,
            Paint paint) {
        paint.getFontMetricsInt(mFontMetricsInt);
        int iconTop = y + getOffsetAboveBaseline(mFontMetricsInt);
        canvas.translate(x, iconTop);
        mDrawable.draw(canvas);
        canvas.translate(-x, -iconTop);
    }

    public void updateBounds() {
        mBounds = mDrawable.getBounds();

        mWidth = mBounds.width();
        mHeight = mBounds.height();
    }

    protected int getOffsetAboveBaseline(Paint.FontMetricsInt fm) {
        switch (mAlignment) {
            case ALIGN_BOTTOM:
                return fm.descent - mHeight;
            case ALIGN_CENTER:
                int textHeight = fm.descent - fm.ascent;
                int offset = (textHeight - mHeight) / 2;
                return fm.ascent + offset;
            case ALIGN_BASELINE:
            default:
                return -mHeight;
        }
    }
}
