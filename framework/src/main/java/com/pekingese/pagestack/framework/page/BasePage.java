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
package com.pekingese.pagestack.framework.page;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.config.PageSaveState;

public abstract class BasePage<T extends IPageStackManager> {
    public static final String PAGE_SAVE_STATE = "page_save_state";
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    public static final int PAGE_STATE_DETACH = 0;
    public static final int PAGE_STATE_HIDE = 1;
    public static final int PAGE_STATE_SHOW = 2;
    public static final int PAGE_STATE_DESTROY = 3;
    protected final PageConfig mPageConfig;
    protected final PageCache mPageCaChe;
    protected final T mPageStackManager;
    private final int mStatusBarHeight;
    private View mRootView;
    private int mPageState = PAGE_STATE_DETACH;

    public BasePage(T stackManager, PageConfig config, PageCache cache) {
        mPageStackManager = stackManager;
        mPageConfig = config;
        if (cache == null) {
            cache = new PageCache(new PageSaveState());
        }
        mPageCaChe = cache;
        mStatusBarHeight = getInternalDimensionSize(stackManager.getContext().getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public View getView() {
        return mRootView;
    }

    public PageConfig getPageConfig() {
        return mPageConfig;
    }

    @CallSuper
    public void createAndAttach(ViewGroup container) {
        create(container);
        attach(container);
    }

    @CallSuper
    public void create(ViewGroup container) {
        Bundle saveState = null;
        if (mPageCaChe.savedInstanceState != null) {
            saveState = mPageCaChe.savedInstanceState.mSaveState;
        }
        mRootView = createView(container, saveState);
        mRootView.setClickable(true);
        if (mPageConfig.isFitSystemWindow) {
//            setPageFitSystemWindow();
        }
        mPageCaChe.savedInstanceState = null;
        initView(container, saveState);
    }

    private void setPageFitSystemWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int paddingLeft = mRootView.getPaddingLeft();
            int paddingTop = mRootView.getPaddingTop();
            int paddingRight = mRootView.getPaddingRight();
            int paddingBottom = mRootView.getPaddingBottom();
            mRootView.setPadding(paddingLeft, paddingTop + mStatusBarHeight, paddingRight, paddingBottom);
            if (mRootView instanceof ViewGroup) {
                ((ViewGroup) mRootView).setClipToPadding(false);
            }
        }
    }

    protected abstract void initView(ViewGroup container, Bundle saveState);

    @NonNull
    protected abstract View createView(ViewGroup container, Bundle saveState);

    @CallSuper
    public void attach(ViewGroup container) {
        container.addView(mRootView, getLayoutParams());
    }

    @CallSuper
    public void detach(ViewGroup container) {
        container.removeView(mRootView);
    }

    @CallSuper
    public void destroy(ViewGroup container) {
        detach(container);
        mRootView = null;
    }

    public boolean isAttached() {
        return mRootView != null && mRootView.getParent() != null;
    }

    protected ViewGroup.LayoutParams getLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void onActivityDestroy() {
    }

    public void onActivityStart() {
    }

    public void onActivityResume() {
    }

    public void onActivityStop() {
    }

    public void onActivityPause() {
    }

    public Bundle onSaveInstanceState() {
        return new Bundle();
    }

    public int getPageState() {
        return mPageState;
    }

    public final void pageStateChanged(int oldPageState, int pageState) {
        mPageState = pageState;
        onPageStateChanged(oldPageState, pageState);
    }

    public void onPageStateChanged(int oldPageState, int pageState) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onPageScrollStart() {

    }

    public void onPageScrolled(float offset, int offsetPixels) {

    }

    /**
     * 在宿主activity被杀掉，此方法不会被执行
     * 建议不再重载此方法，建议采用延迟来处理页面加载滑动卡顿的问题
     */
    @Deprecated
    public void onPageScrollEnd() {

    }

    public boolean dispatchActivityKeyEvent(KeyEvent event) {
        return false;
    }

    public void onNewMessage(Message message) {

    }

    public boolean interceptGoBackPressed() {
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public int generateCommonRequestCode(int requestCode) {
        return (mPageConfig.position << 16) + (requestCode & 0xffff);
    }

}
