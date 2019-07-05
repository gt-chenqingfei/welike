package com.redefine.welike.business.feeds.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.business.feeds.ui.contract.IMainMineContract;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */
@PageName("MineFragmentPage")
public class MineFragmentPage extends BaseFragmentPage {
    public static final String TAG = "mine_page";
    private boolean isFistShow = true;

    private IMainMineContract.IMainMinePresenter mPresenter;

    public MineFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
//        config.isFitSystemWindow = true;
        mPresenter = IMainMineContract.IMainMineFactory.createPresenter();
        isFistShow = true;
    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_MAIN_ME);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        EventLog.MainMe.report13();
        EventLog1.MainMe.report13();
        mPresenter.attach(isFistShow);
        isFistShow = false;
    }

    @Override
    public void destroy(ViewGroup container) {
        super.destroy(container);
        mPresenter.destroy();
    }

    @Override
    public void onBasePageStateChanged(int oldPageState, int pageState) {
        super.onBasePageStateChanged(oldPageState, pageState);
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            mPresenter.onPageShown();
        }
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            mPresenter.onPageShown();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mPresenter.onActivityResult(requestCode, resultCode, data);



    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        mPresenter.onActivityResume();
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        mPresenter.onCreate(mPageConfig.pageBundle, saveState);
        return mPresenter.createView(mPageStackManager.getLayoutInflater(), container, saveState);
    }

    @Override
    public void onNewMessage(Message message) {
        super.onNewMessage(message);
        if (message.what == MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT) {
//            mPresenter.refreshUserTask();
        }
    }
}
