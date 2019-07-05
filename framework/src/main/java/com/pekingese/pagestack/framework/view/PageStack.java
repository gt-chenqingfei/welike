/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.transformer.TransitionEffectFactory;
import com.pekingese.pagestack.framework.util.CollectionUtil;
import com.pekingese.pagestack.framework.util.ScrimUtil;

import java.util.List;

public class PageStack extends BasePageStack implements OnPageChangeListener {

    private static final int MIN_SHADOW_ALPHA = 30;
    protected final IPageStackManager mPageStackManager;
    protected final IPageFactory mPageFactory;
    private Rect mHintRect = new Rect();
    private Rect mShadowRect = new Rect();
    private int mShadowAlpha = 255;
    private Drawable mShadowDrawable;
    private int mShadowSize;
    private static final int BEGIN_COLOR = 0x66000000;
    private static final int SHADOW_SIZE = 15;

    public PageStack(Context context, IPageStackManager manager, IPageFactory pageFactory) {
        super(context);
        this.mPageStackManager = manager;
        this.mPageFactory = pageFactory;
        initPageStack();
    }

    private void initPageStack() {
        mShadowDrawable = ScrimUtil.makeCubicGradientScrimDrawable(BEGIN_COLOR, 8, Gravity.RIGHT);
        mShadowSize = (int) (SHADOW_SIZE * getContext().getResources().getDisplayMetrics().density);
        addOnPageChangeListener(this);
        setAdapter(initPageAdapter());
    }

    protected PageStackAdapter initPageAdapter() {
        return new PageStackAdapter(mPageFactory);
    }

    @Override
    public void pushPage(PageConfig pageConfig) {
        pushPage(pageConfig, null);
    }

    @Override
    public void pushPage(PageConfig pageConfig, PageCache pageCache) {
        check();
        ((PageStackAdapter) getAdapter()).addPageConfig(pageConfig, pageCache);
        getAdapter().notifyDataSetChanged();
        if (!pageConfig.isPushWithAnimation) {
            setScrollState(SCROLL_STATE_SETTLING);
        }
        scrollToLastPage(pageConfig);
    }

    private void check() {
        if (!isScrollEnd()) {
            completeScroll(false);
        }
    }

    @Override
    public boolean popPage() {
        check();
        return scrollToPrePage();
    }

    @Override
    public boolean popToRootPage() {
        check();
        return scrollToRootPage();
    }

    @Override
    public List<PageConfig> findPageConfigByType(Class<? extends BasePage> pageClass) {
        return ((PageStackAdapter) getAdapter()).findPageConfigByType(pageClass);
    }

    @Override
    public PageConfig findPageConfigByIndex(int index) {
        return ((PageStackAdapter) getAdapter()).findPageConfigByIndex(index);
    }

    private boolean scrollToRootPage() {
        if (getLastPosition() == 0) {
            return false;
        }
        setScrollState(SCROLL_STATE_SETTLING);
        setCurrentItem(0, false);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void scrollToLastPage(PageConfig pageConfig) {
        boolean withAnimation = pageConfig.isPushWithAnimation && isScrollEnd();
        if (!withAnimation) {
            setScrollState(SCROLL_STATE_SETTLING);
        }
        setCurrentItem(getLastPosition(), withAnimation);
    }

    private boolean scrollToPrePage() {
        if (getLastPosition() <= 0) {
            return false;
        }

        PageConfig pageConfig = ((PageStackAdapter) getAdapter()).getPageConfig(getLastPosition());
        boolean withAnimation = pageConfig.isPopWithAnimation && isScrollEnd();
        if (!withAnimation) {
            setScrollState(SCROLL_STATE_SETTLING);
        }
        setCurrentItem(getSecondToLastPosition(), withAnimation);
        return true;
    }

    @CallSuper
    @Override
    public void onPageScrolled(float positionOffset, int offsetPixels) {

    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        child.getHitRect(mHintRect);

        if (mHintRect.left - getScrollX() > 0) {
            // 说明此View是顶层View，并且以及产生了滑动，渲染shadow
            mShadowAlpha = (int) ((child.getWidth() - mHintRect.left + getScrollX()) * 1.0f / child.getWidth() * 255);
            mShadowDrawable.setAlpha(Math.max(mShadowAlpha, MIN_SHADOW_ALPHA));
            mShadowRect.set(mHintRect.left - mShadowSize, mHintRect.top,
                    mHintRect.left, mHintRect.bottom);
            mShadowDrawable.setBounds(mShadowRect);
            mShadowDrawable.draw(canvas);
        }
        return result;
    }

    @CallSuper
    @Override
    public void onPageScrollStart() {
        PageConfig config = ((PageStackAdapter) getAdapter()).getPageConfig(getLastPosition());
        onApplyPageConfig(config);
    }

    @Override
    protected void setPrimaryItem(int mCurItem, BasePage basePage) {
        super.setPrimaryItem(mCurItem, basePage);
        PageConfig config = null;
        if (basePage == null) {
            config = ((PageStackAdapter) getAdapter()).getPageConfig(mCurItem);
        } else {
            config = basePage.getPageConfig();
        }
        if (config == null) {
            return ;
        }
        onApplyPageConfigOnShown(config);
    }

    @CallSuper
    @Override
    public void onPageScrollEnd(int currentItem) {
        if (currentItem == getLastPosition()) {
            return;
        }
        if (getAdapter().getCount() == 0) {
            return;
        }
        List<PageConfig> configs = ((PageStackAdapter) getAdapter()).removePositionRight(currentItem);
        if (configs != null) {
            ((PageStackAdapter) getAdapter()).removePageCache(configs);
            getAdapter().notifyDataSetChanged();
        }
    }

    protected void onApplyPageConfig(PageConfig config) {
        setPageTransformer(false, TransitionEffectFactory.getPageTransformer(config.effect));
    }

    protected void onApplyPageConfigOnShown(PageConfig config) {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).setRequestedOrientation(config.orientation);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected int getLastPosition() {
        return getAdapter().getCount() - 1;
    }

    protected int getSecondToLastPosition() {
        return getLastPosition() - 1;
    }

    public void dispatchMessageToPageByConfig(PageConfig config, Message message) {
        if (config == null) {
            return ;
        }
        ItemInfo info = infoForPosition(config.position);
        if (info != null && info.object != null) {
            info.object.onNewMessage(message);
        }
    }

    public void dispatchMessageToPagesByConfig(List<PageConfig> configs, Message message) {
        if (CollectionUtil.isArrayEmpty(configs)) {
            return ;
        }
        int lastIndex = configs.size() - 1;
        for (int i = lastIndex; i >= 0; i--) {
            dispatchMessageToPageByConfig(configs.get(i), message);
        }
    }

    public void dispatchMessageToAll(Message message) {
        for (ItemInfo info : mItems) {
            if (info != null && info.object != null) {
                info.object.onNewMessage(message);
            }
        }
    }

    public void clearPageStack() {
        setAdapter(initPageAdapter());
    }

    public BasePage getCurrentPage() {
        ItemInfo itemInfo = infoForPosition(mCurItem);
        if (itemInfo != null) {
            return itemInfo.object;
        }
        return null;
    }

    public int parseRequestCodeToPosition(int requestCode) {
        return (requestCode >> 16) & 0xffff;
    }

    public int parseRequestCode(int requestCode) {
        return requestCode & 0xffff;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int position = parseRequestCodeToPosition(requestCode);
        int realRequestCode = parseRequestCode(requestCode);
        ItemInfo itemInfo = infoForPosition(position);
        if (itemInfo != null && itemInfo.object != null) {
            itemInfo.object.onRequestPermissionsResult(realRequestCode, permissions, grantResults);
        }
    }

    public BasePage getPage(int position) {
        ItemInfo itemInfo = infoForPosition(position);
        if (itemInfo != null && itemInfo.object != null) {
            return itemInfo.object;
        }
        return null;
    }
}
