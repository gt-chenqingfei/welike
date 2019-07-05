package com.redefine.richtext.drawee;

import android.text.SpanWatcher;
import android.text.Spannable;

import java.lang.ref.WeakReference;

/**
 * Name: DraweeSpanWatcher
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 14:49
 */
public class DraweeSpanChangeWatcher implements SpanWatcher {


    private final WeakReference<IRichDraweeSpanView> mSpanView;

    public DraweeSpanChangeWatcher(IRichDraweeSpanView richDraweeSpanTextView) {
        mSpanView = new WeakReference<>(richDraweeSpanTextView);
    }

    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {
        IRichDraweeSpanView draweeSpanView = mSpanView.get();
        if (draweeSpanView != null) {
            draweeSpanView.onSpanAdded(text, what, start, end);
        }
    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        IRichDraweeSpanView draweeSpanView = mSpanView.get();
        if (draweeSpanView != null) {
            draweeSpanView.onSpanRemoved(text, what, start, end);
        }
    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        IRichDraweeSpanView draweeSpanView = mSpanView.get();
        if (draweeSpanView != null) {
            draweeSpanView.onSpanChanged(text, what, ostart, oend, nstart, nend);
        }
    }
}