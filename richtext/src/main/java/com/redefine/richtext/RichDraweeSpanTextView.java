package com.redefine.richtext;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;

import com.redefine.richtext.drawee.DraweeSpanChangeWatcher;
import com.redefine.richtext.drawee.IDraweeDelegateView;
import com.redefine.richtext.drawee.IRichDraweeSpanView;
import com.redefine.richtext.drawee.RichDraweeViewFactory;

/**
 * Name: RichDraweeSpanTextView
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 11:57
 */
public class RichDraweeSpanTextView extends RichTextView implements IRichDraweeSpanView {

    IDraweeDelegateView mDraweeDelegate;
    private DraweeSpanChangeWatcher mSpanWatcher;
    private static final int CHANGE_WATCHER_PRIORITY = 100;

    public RichDraweeSpanTextView(Context context) {
        super(context);
    }

    public RichDraweeSpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichDraweeSpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initRichTextProcessor() {
        super.initRichTextProcessor();
        getRichProcessor().setRoundRectLink(true);

    }

    private IDraweeDelegateView getDelegateView() {
        if (mDraweeDelegate == null) {
            mDraweeDelegate = RichDraweeViewFactory.createDraweeView(this);
        }
        return mDraweeDelegate;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getDelegateView().onAttachToView();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        getDelegateView().onAttachToView();
    }

    @Override
    protected void onDetachedFromWindow() {
        getDelegateView().onDetachFromView();
        super.onDetachedFromWindow();
    }

    @Override
    public void onStartTemporaryDetach() {
        getDelegateView().onDetachFromView();
        super.onStartTemporaryDetach();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        // Remove any ChangeWatchers that might have come from other TextViews.
        CharSequence s = getText();

        if (s instanceof Spannable) {
            Spannable sp = (Spannable) s;
            final DraweeSpanChangeWatcher[] watchers = sp.getSpans(0, sp.length(), DraweeSpanChangeWatcher.class);
            final int count = watchers.length;
            for (int i = 0; i < count; i++) {
                sp.removeSpan(watchers[i]);
            }

            if (mSpanWatcher == null) {
                mSpanWatcher = new DraweeSpanChangeWatcher(this);
            }
            sp.setSpan(mSpanWatcher, 0, sp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    | (CHANGE_WATCHER_PRIORITY << Spanned.SPAN_PRIORITY_SHIFT));
        }
    }

    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {
        getDelegateView().onSpanAdded(text, what, start, end);
    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        getDelegateView().onSpanRemoved(text, what, start, end);
    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        getDelegateView().onSpanChanged(text, what, ostart, oend, nstart, nend);
    }

    @Override
    public Spannable getSpannable() {
        if (getText() instanceof Spannable) {
            return (Spannable) getText();
        }
        return null;
    }
}
