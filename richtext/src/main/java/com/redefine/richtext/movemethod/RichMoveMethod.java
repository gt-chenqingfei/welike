package com.redefine.richtext.movemethod;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.redefine.richtext.span.IClickableSpan;
import com.redefine.richtext.span.RichClickableSpan;

/**
 * Created by MR on 2018/1/26.
 */

public class RichMoveMethod extends LinkMovementMethod implements View.OnTouchListener {

    private IClickableSpan mSpan;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        try {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);
                float lineMax = layout.getLineMax(line);
                if (x > lineMax) {
                    return false;
                }
                IClickableSpan[] link = buffer.getSpans(off, off, IClickableSpan.class);
                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        if (mSpan == link[0]) {
                            mSpan.onClick(widget);
                        }
                        if (mSpan != null) {
                            mSpan.setPressed(false);
                            widget.invalidate();
                        }
                    } else {
                        mSpan = link[0];
                        mSpan.setPressed(true);
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                        widget.invalidate();
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (mSpan != null) {
                    mSpan.setPressed(false);
                    widget.invalidate();
                }
                mSpan = null;
            }
            return false;
        } catch (Exception e) {
            // do nothing
            return false;
        }
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            TextView widget = (TextView) v;
            Object text = widget.getText();
            if (text instanceof Spanned) {
                Spanned buffer = (Spanned) text;

                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);
                    float lineMax = layout.getLineMax(line);
                    if (x > lineMax) {
                        return false;
                    }
                    IClickableSpan[] link = buffer.getSpans(off, off, IClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            if (mSpan == link[0]) {
                                link[0].onClick(widget);
                            }
                            if (mSpan != null) {
                                mSpan.setPressed(false);
                                widget.invalidate();
                            }
                        } else {

                            // Selection only works on Spannable text. In our case setSelection doesn't work on spanned text
//                        Selection.setSelection((Spannable) buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                            mSpan = link[0];
                            mSpan.setPressed(true);
                            widget.invalidate();
                        }
                        return true;
                    }
                }

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (mSpan != null) {
                        mSpan.setPressed(false);
                        widget.invalidate();
                    }
                    mSpan = null;
                }
            }
            return false;
        } catch (Exception e) {
            // do nothing
            return false;
        }

    }
}
