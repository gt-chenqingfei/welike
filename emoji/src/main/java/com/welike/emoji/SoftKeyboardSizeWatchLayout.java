package com.welike.emoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class SoftKeyboardSizeWatchLayout extends RelativeLayout {

    private Context mContext;
    private int mOldh = -1;
    private int mNowh = -1;
    protected int mScreenHeight = 0;
    protected boolean mIsSoftKeyboardPop = false;

    public SoftKeyboardSizeWatchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                if (mScreenHeight == 0) {
                    mScreenHeight = r.bottom;
                }
                mNowh = mScreenHeight - r.bottom;
                if (mOldh != -1 && mNowh != mOldh) {
                    if (mNowh > 0) {
                        mIsSoftKeyboardPop = true;
                        if (mListenerList != null) {
                            for (OnSoftChangeListener l : mListenerList) {
                                l.onSoftKeyboardPop(mNowh);
                            }
                        }
                    } else {
                        mIsSoftKeyboardPop = false;
                        if (mListenerList != null) {
                            for (OnSoftChangeListener l : mListenerList) {
                                l.onSoftKeyboardClose();
                            }
                        }
                    }
                }
                mOldh = mNowh;
                if (viewSizeChangeListener != null) {
                    viewSizeChangeListener.onViewSizeChanged(mIsSoftKeyboardPop);
                }
            }
        });
    }

    public boolean isSoftKeyboardPop() {
        return mIsSoftKeyboardPop;
    }

    private List<OnSoftChangeListener> mListenerList;
    private OnViewSizeChangeListener viewSizeChangeListener;

    public void addOnSoftChangedListener(OnSoftChangeListener l) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(l);
    }

    public void setOnViewSizeChangeListener(OnViewSizeChangeListener l) {
        this.viewSizeChangeListener = l;
    }

    public interface OnSoftChangeListener {
        void onSoftKeyboardPop(int height);

        void onSoftKeyboardClose();
    }

    public interface OnViewSizeChangeListener {
        void onViewSizeChanged(boolean isSoftKeyboardPop);
    }
}
