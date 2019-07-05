/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/8/1
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/8/1
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/8/1, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.layer.BasePageEnvironment;
import com.pekingese.pagestack.framework.layer.BasePageStackLayer;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.util.CollectionUtil;
import com.pekingese.pagestack.framework.view.PageStack;

import java.util.List;

public abstract class BasePageStackManager implements IPageStackManager {

    private final PageStack mPageStack;
    private final BasePageStackLayer mLayer;
    private final LayoutInflater mLayoutInflater;
    private final BasePageEnvironment mPageEnvironment;
    private final Activity mActivity;

    public BasePageStackManager(Activity context) {
        mActivity = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPageEnvironment = createPageEnvironment(context);
        mPageStack = createPageStack(context);
        mLayer = createPageStackLayer(context);
        mPageEnvironment.addView(mPageStack, new PageStack.LayoutParams());
        mPageEnvironment.addView(mLayer, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    private BasePageEnvironment createPageEnvironment(Context context) {
        return new BasePageEnvironment(context);
    }

    protected BasePageStackLayer createPageStackLayer(Context context) {
        return new BasePageStackLayer(context);
    }

    protected abstract PageStack createPageStack(Context context);

    public void bindPageStack(ViewGroup parent) {
        parent.addView(mPageEnvironment, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public void pushPage(PageConfig config) {
        pushPage(config, null);
    }

    @Override
    public void pushPage(PageConfig pageConfig, PageCache pageCache) {
        mPageStack.pushPage(pageConfig, pageCache);
    }

    @Override
    public void clearPageStack() {
        mPageStack.clearPageStack();
    }

    @Override
    public boolean popPage() {
        return mPageStack.popPage();
    }

    @Override
    public boolean popToRootPage() {
        return mPageStack.popToRootPage();
    }

    @Override
    public boolean onBackPressed() {
        BasePage basePage = mPageStack.getCurrentPage();
        if (basePage != null) {
            if (basePage.interceptGoBackPressed()) {
                return true;
            }
        }
        return popPage();
    }

    @Override
    public void dispatchMessageToPage(Class<? extends BasePage> pageClass, Message message, boolean isToAll) {
        List<PageConfig> pageConfigs = findPageConfigByType(pageClass);
        if (CollectionUtil.isArrayEmpty(pageConfigs)) {
            return ;
        }
        if (isToAll) {
            dispatchMessageToPagesByConfig(pageConfigs, message);
        } else {
            dispatchMessageToPageByConfig(pageConfigs.get(pageConfigs.size() - 1), message);
        }

    }

    @Override
    public void dispatchMessageToPageByIndex(int index, Message message) {
        PageConfig config = findPageConfigByIndex(index);
        dispatchMessageToPageByConfig(config, message);
    }

    @Override
    public void dispatchMessageToPageByConfig(PageConfig config, Message message) {
        mPageStack.dispatchMessageToPageByConfig(config, message);
    }

    @Override
    public void dispatchMessageToPagesByConfig(List<PageConfig> configs, Message message) {
        mPageStack.dispatchMessageToPagesByConfig(configs, message);
    }

    @Override
    public void dispatchMessageToAll(Message message) {
        mPageStack.dispatchMessageToAll(message);
    }

    @Override
    public List<PageConfig> findPageConfigByType(Class<? extends BasePage> pageClass) {
        return mPageStack.findPageConfigByType(pageClass);
    }

    @Override
    public PageConfig findPageConfigByIndex(int index) {
        return mPageStack.findPageConfigByIndex(index);
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {
        mPageStack.onActivitySaveInstanceState(outState);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {
        mPageStack.onActivityRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPageStack.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void saveStartForResultPage() {
        mPageStack.saveStartForResultPage();
    }

    @Override
    public void onActivityDestroy() {
        mPageStack.onActivityDestroy();
        mLayer.onActivityDestroy();
    }

    @Override
    public void onActivityPause() {
        mPageStack.onActivityPause();
        mLayer.onActivityPause();
    }

    @Override
    public void onActivityStop() {
        mPageStack.onActivityStop();
        mLayer.onActivityStop();
    }

    @Override
    public void onActivityStart() {
        mPageStack.onActivityStart();
        mLayer.onActivityStart();
    }

    @Override
    public void onActivityResume() {
        mPageStack.onActivityResume();
        mLayer.onActivityResume();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mPageStack.dispatchActivityKeyEvent(event);
    }

    @Override
    public BasePageStackLayer getPageStackLayer() {
        return mLayer;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPageStack.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public PageStack getPageStack() {
        return mPageStack;
    }

    public Activity getActivity() {
        return mActivity;
    }
}
