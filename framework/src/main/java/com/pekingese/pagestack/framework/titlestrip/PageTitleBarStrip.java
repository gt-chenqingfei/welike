package com.pekingese.pagestack.framework.titlestrip;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pekingese.pagestack.framework.titlebar.ITitleActionObserver;
import com.pekingese.pagestack.framework.titlebar.ITitleBar;
import com.pekingese.pagestack.framework.titlebar.ITitleBarFactory;
import com.pekingese.pagestack.framework.titlebar.PageTitleActionPack;
import com.pekingese.pagestack.framework.titlebar.SingleTitleActionSubject;
import com.pekingese.pagestack.framework.titlebar.TitleBarFactory;

/**
 * Created by liwenbo on 2018/2/1.
 */

public class PageTitleBarStrip extends FrameLayout implements IPageTitleManager {
    private static final String TAG = "PagerTitleStrip";

    private ITitleBar mCurrentTitleBar;
    private ITitleBarFactory mTitleBarFactory;
    private SingleTitleActionSubject mSubject;
    private Drawable mDefaultPageTitleStripDrawable;

    public PageTitleBarStrip(Context context) {
        super(context);
        initTitleBars();
    }

    public PageTitleBarStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTitleBars();
    }

    public PageTitleBarStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTitleBars();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PageTitleBarStrip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTitleBars();
    }

    private void initTitleBars() {
        mDefaultPageTitleStripDrawable = new ColorDrawable(Color.BLACK).mutate();
        mTitleBarFactory = createTitleBarFactory();
        mSubject = new SingleTitleActionSubject();
        mCurrentTitleBar = mTitleBarFactory.createTitleBar(getContext(), mSubject);
        addView(mCurrentTitleBar.getView(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    protected ITitleBarFactory createTitleBarFactory() {
        return new TitleBarFactory();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void applyPageTitleActions(PageTitleActionPack currentTitleAction) {
        mCurrentTitleBar.applyTitleAction(currentTitleAction);
        setBackgroundDrawable(getDrawableFromTitleAction(currentTitleAction));
    }

    @Override
    public void setTitleActionObserver(ITitleActionObserver observer) {
        mSubject.setObserver(observer);
    }

    private Drawable getDrawableFromTitleAction(PageTitleActionPack currentTitleAction) {

        Drawable currentRes = null;
        if (currentTitleAction != null) {
            currentRes = currentTitleAction.getBackgroundDrawable();
        }

        if (currentRes == null) {
            currentRes = mDefaultPageTitleStripDrawable;
        }

        return currentRes;
    }

    @Override
    public void applyPageScrollOffset(float positionOffset) {
    }

    @Override
    public void applyPageScrollEnd(PageTitleActionPack titleAction) {
        mCurrentTitleBar.applyTitleAction(titleAction);
        if (titleAction != null) {
            titleAction.getBackgroundDrawable().setAlpha(255);
            setBackgroundDrawable(titleAction.getBackgroundDrawable());
        } else {
            mDefaultPageTitleStripDrawable.setAlpha(255);
            setBackgroundDrawable(mDefaultPageTitleStripDrawable);
        }
    }
}
