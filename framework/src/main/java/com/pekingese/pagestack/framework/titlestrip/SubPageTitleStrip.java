/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/8/2
 * <p>
 * Description : page标题条
 * <p>
 * Creation    : 2017/8/2
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/8/2, bobo, Create the file
 * ****************************************************************************
 */
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

import com.pekingese.pagestack.framework.drawable.ArrayDrawable;
import com.pekingese.pagestack.framework.titlebar.ITitleBar;
import com.pekingese.pagestack.framework.titlebar.ITitleBarFactory;
import com.pekingese.pagestack.framework.titlebar.PageTitleActionPack;
import com.pekingese.pagestack.framework.titlebar.SingleTitleActionSubject;
import com.pekingese.pagestack.framework.titlebar.TitleActionObserverDelegate;
import com.pekingese.pagestack.framework.titlebar.TitleBarFactory;
import com.pekingese.pagestack.framework.view.DecorView;

@DecorView
public class SubPageTitleStrip extends FrameLayout implements ISubPageTitleManager {
    private static final String TAG = "PagerTitleStrip";

    private ITitleBar mCurrentTitleBar;
    private ITitleBar mPreTitleBar;
    private ITitleBarFactory mTitleBarFactory;
    private SingleTitleActionSubject mSubject;
    private Drawable mDefaultPageTitleStripDrawable;

    public SubPageTitleStrip(Context context) {
        super(context);
        initTitleBars();
    }

    public SubPageTitleStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTitleBars();
    }

    public SubPageTitleStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTitleBars();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SubPageTitleStrip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTitleBars();
    }

    private void initTitleBars() {
        mDefaultPageTitleStripDrawable = new ColorDrawable(Color.BLACK).mutate();
        mTitleBarFactory = createTitleBarFactory();
        mSubject = new SingleTitleActionSubject();
        mCurrentTitleBar = mTitleBarFactory.createTitleBar(getContext(), mSubject);
        mPreTitleBar = mTitleBarFactory.createTitleBar(getContext(), mSubject);
        addView(mPreTitleBar.getView(), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
    public void applyPageTitleActions(PageTitleActionPack preTitleAction, PageTitleActionPack currentTitleAction) {
        mCurrentTitleBar.applyTitleAction(currentTitleAction);
        mPreTitleBar.getView().setVisibility(INVISIBLE);
        mPreTitleBar.applyTitleAction(preTitleAction);
        setBackgroundDrawable(getDrawableFromTitleAction(preTitleAction, currentTitleAction));
    }

    @Override
    public void setTitleActionObserver(TitleActionObserverDelegate observer) {
        mSubject.setObserver(observer);
    }

    private Drawable getDrawableFromTitleAction(PageTitleActionPack preTitleAction, PageTitleActionPack currentTitleAction) {
        Drawable preRes = null;
        if (preTitleAction != null) {
            preRes = preTitleAction.getBackgroundDrawable();
        }
        Drawable currentRes = null;
        if (currentTitleAction != null) {
            currentRes = currentTitleAction.getBackgroundDrawable();
        }

        if (preRes == null) {
            preRes = mDefaultPageTitleStripDrawable;
        }

        if (currentRes == null) {
            currentRes = mDefaultPageTitleStripDrawable;
        }

        if (preRes instanceof ColorDrawable && currentRes instanceof ColorDrawable) {
            if (((ColorDrawable) preRes).getColor() == ((ColorDrawable) currentRes).getColor()) {
                return preRes;
            }
        }
        Drawable[] arrayDrawable = new Drawable[2];

        arrayDrawable[0] = preRes;
        arrayDrawable[1] = currentRes;
        return new ArrayDrawable(arrayDrawable);
    }

    @Override
    public void applyPageScrollOffset(float positionOffset) {
        if (mPreTitleBar.getView().getVisibility() == INVISIBLE) {
            mPreTitleBar.getView().setVisibility(VISIBLE);
        }
        mPreTitleBar.setChildrenAlpha(1f - positionOffset);
        mCurrentTitleBar.setChildrenAlpha(positionOffset);
        mCurrentTitleBar.offsetTitle(positionOffset);
        applyBackgroundScrollOffset(positionOffset);
    }

    private void applyBackgroundScrollOffset(float positionOffset) {
        try {
            Drawable drawable = getBackground();
            if (drawable instanceof ArrayDrawable) {
                ArrayDrawable arrayDrawable = (ArrayDrawable) drawable;
                arrayDrawable.getDrawable(0).setAlpha((int) (255 - positionOffset * 255));
                arrayDrawable.getDrawable(1).setAlpha((int) (positionOffset * 255));
            }
        } catch (Exception e) {
            // do nothing
        }

    }

    @Override
    public void applyPageScrollEnd(PageTitleActionPack titleAction) {
        mPreTitleBar.getView().setVisibility(INVISIBLE);
        mPreTitleBar.setChildrenAlpha(1);
        mCurrentTitleBar.setChildrenAlpha(1);
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
