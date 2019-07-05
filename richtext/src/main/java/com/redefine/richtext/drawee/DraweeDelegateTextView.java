package com.redefine.richtext.drawee;

import android.graphics.drawable.Drawable;
import android.text.Spannable;

import com.redefine.richtext.span.DraweeSpan;

/**
 * Name: DraweeDelegateTextView
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 12:08
 */
public class DraweeDelegateTextView implements IDraweeDelegateView {
    private final IRichDraweeSpanView mView;


    public DraweeDelegateTextView(IRichDraweeSpanView spanView) {
        mView = spanView;
    }

    @Override
    public void onAttachToView() {
        Spannable spannable = mView.getSpannable();
        if (spannable == null) {
            return ;
        }
        DraweeSpan[] draweeSpans = spannable.getSpans(0, spannable.length(), DraweeSpan.class);
        for (DraweeSpan span : draweeSpans) {
            span.onAttach(this);
        }
    }

    @Override
    public void onDetachFromView() {
        Spannable spannable = mView.getSpannable();
        if (spannable == null) {
            return ;
        }
        DraweeSpan[] draweeSpans = spannable.getSpans(0, spannable.length(), DraweeSpan.class);
        for (DraweeSpan span : draweeSpans) {
            span.onDetach(this);
        }
    }

    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {
        if (!(what instanceof DraweeSpan)) {
            return ;
        }

        if (!((DraweeSpan) what).getDraweeHolder().isAttached()) {
            ((DraweeSpan) what).onAttach(this);
        }
    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        if (!(what instanceof DraweeSpan)) {
            return ;
        }
        ((DraweeSpan) what).onDetach(this);
    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        if (!(what instanceof DraweeSpan)) {
            return ;
        }
    }

    @Override
    public void invalidate(Drawable who) {
        mView.invalidate();
    }

    @Override
    public void postDelayed(Runnable what, long delay) {
        mView.postDelayed(what, delay);
    }

    @Override
    public void removeCallbacks(Runnable what) {
        mView.removeCallbacks(what);
    }

    @Override
    public int getWidgetWidth() {
        return mView.getMeasuredWidth();
    }
}
