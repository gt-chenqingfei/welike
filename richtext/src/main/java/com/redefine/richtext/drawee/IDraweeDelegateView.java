package com.redefine.richtext.drawee;

import android.graphics.drawable.Drawable;
import android.text.Spannable; /**
 * Name: IDraweeDelegateView
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 12:01
 */
public interface IDraweeDelegateView {

    void onAttachToView();

    void onDetachFromView();

    void onSpanAdded(Spannable text, Object what, int start, int end);

    void onSpanRemoved(Spannable text, Object what, int start, int end);

    void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend);

    void invalidate(Drawable who);

    void postDelayed(Runnable what, long delay);

    void removeCallbacks(Runnable what);

    int getWidgetWidth();
}
