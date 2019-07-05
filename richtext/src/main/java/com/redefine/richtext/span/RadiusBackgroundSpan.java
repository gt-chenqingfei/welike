package com.redefine.richtext.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.view.View;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;

/**
 * Name: RoundRectSpan
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-27 20:24
 */
public class RadiusBackgroundSpan extends ReplacementSpan implements RichSpan {

    private final Block mBlock;
    private final OnRichItemClickListener mListener;
    private final Drawable mLeftDrawable;
    private int mSize;
    private int mRadius;
    private boolean isPressed;
    private final int lineMargin;

    public RadiusBackgroundSpan(Drawable drawable, int i, Block copy, OnRichItemClickListener l) {
        mLeftDrawable = drawable;
        mRadius = i;
        mBlock = copy;
        mListener = l;
        lineMargin = ScreenUtils.dip2Px(6);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        int leftWidth = mLeftDrawable != null ? mLeftDrawable.getIntrinsicWidth() : 0;
        mSize = (int) (paint.measureText(text, start, end) + 3 * mRadius) + leftWidth;
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int textHeight = fmPaint.bottom - fmPaint.top;
            fm.ascent = -textHeight - lineMargin;
            fm.top = -textHeight - lineMargin;
            fm.descent = 2 * lineMargin;
            fm.bottom = 2 * lineMargin;
        }
        return mSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int color = paint.getColor();//保存文字颜色
        paint.setColor(isPressed ? 0xFF77A2C4 : 0xFF95CBF6);//设置背景颜色
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        RectF oval = new RectF(x, y + paint.ascent() - lineMargin, x + mSize, y + paint.descent() + lineMargin);
        canvas.drawRoundRect(oval,  mRadius, mRadius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        paint.setColor(color);//恢复画笔的文字颜色
        color = paint.getColor();//保存文字颜色
        boolean isBoldText = paint.isFakeBoldText();
        paint.setColor(Color.WHITE);
        paint.setFakeBoldText(true);
        int leftWidth = mLeftDrawable != null ? mLeftDrawable.getIntrinsicWidth() : 0;
        canvas.drawText(text, start, end, x + mRadius + leftWidth, y, paint);//绘制文字
        if (mLeftDrawable != null) {
            canvas.save();
            float textHeight = paint.descent() - paint.ascent();
            float offset = (textHeight - mLeftDrawable.getBounds().height()) / 2;
            float transY = y + paint.ascent() + offset;
            canvas.translate(x + mRadius, transY);
            mLeftDrawable.draw(canvas);
            canvas.restore();
        }
        paint.setColor(color);
        paint.setFakeBoldText(isBoldText);
    }

    @Override
    public Block getBlock() {
        return mBlock;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.WHITE);
    }

    @Override
    public RichItem getRichItem() {
        return mBlock.richItem;
    }

    @Override
    public void onClick(View widget) {
        if (mBlock == null || mBlock.richItem == null) {
            return ;
        }
        if (mListener != null) {
            mListener.onRichItemClick(mBlock.richItem);
        }
    }

    @Override
    public void setPressed(boolean b) {
        isPressed = b;
    }
}