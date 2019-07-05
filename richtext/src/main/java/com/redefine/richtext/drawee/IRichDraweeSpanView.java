package com.redefine.richtext.drawee;

import android.text.Spannable; /**
 * Name: IRichDraweeSpanView
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 12:10
 */
public interface IRichDraweeSpanView {
    void onSpanAdded(Spannable text, Object what, int start, int end);

    void onSpanRemoved(Spannable text, Object what, int start, int end);

    void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend);

    Spannable getSpannable();

    void invalidate();

    boolean postDelayed(Runnable what, long delay);

    boolean removeCallbacks(Runnable what);

    int getMeasuredWidth();
}
