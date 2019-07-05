package com.redefine.richtext.drawee;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;

import com.redefine.richtext.span.DraweeSpan;

import java.util.HashSet;
import java.util.Set;

/**
 * Name: DraweeDelegateTextView
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 12:08
 */
public class DraweeDelegateEditView implements IDraweeDelegateView {
    private final IRichDraweeSpanView mView;
    private Set<DraweeSpan> mDraweeSpan = new HashSet<DraweeSpan>();


    public DraweeDelegateEditView(IRichDraweeSpanView spanView) {
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
        mDraweeSpan.add((DraweeSpan) what);
        if (!((DraweeSpan) what).getDraweeHolder().isAttached()) {
            ((DraweeSpan) what).onAttach(this);
        }
    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        if (!(what instanceof DraweeSpan)) {
            return ;
        }
        mDraweeSpan.remove(what);
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
        DraweeSpan imageSpan = getImageSpan(who);
        if (imageSpan != null) {
            CharSequence text = mView.getSpannable();
            if (!TextUtils.isEmpty(text)) {
                Spannable editable = (Spannable)text;
                int start = editable.getSpanStart(imageSpan);
                int end = editable.getSpanEnd(imageSpan);
                int flags = editable.getSpanFlags(imageSpan);
                editable.setSpan(imageSpan, start, end, flags);
            }

        }
    }

    private DraweeSpan getImageSpan(Drawable drawable) {
        DraweeSpan imageSpan = null;
        CharSequence text = mView.getSpannable();
        if (!TextUtils.isEmpty(text)) {
            Spanned spanned = (Spanned) text;
            DraweeSpan[] spans = spanned.getSpans(0, text.length(), DraweeSpan.class);
            if (spans != null && spans.length > 0) {
                for (DraweeSpan span : spans) {
                    if (drawable == span.getDrawable()) {
                        imageSpan = span;
                    }
                }
            }
        }

        return imageSpan;
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
