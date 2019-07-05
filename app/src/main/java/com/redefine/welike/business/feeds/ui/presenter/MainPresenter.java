package com.redefine.welike.business.feeds.ui.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IMianContract;
import com.redefine.welike.business.feeds.ui.fragment.MainFragmentPageSwitcher;
import com.redefine.welike.business.im.AllCountManager;
import com.redefine.welike.business.im.MessageCountListener;
import com.redefine.welike.business.user.management.FollowerManager;

import io.reactivex.android.schedulers.AndroidSchedulers;

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

public class MainPresenter implements IMianContract.IMainPresenter
        , MessageCountListener, FollowerManager.GetFollowersCallback {
    private final IMianContract.IMainView mView;


    public MainPresenter(IMianContract.IMainView view) {
        mView = view;
        mView.setPresenter(this);
        AllCountManager.INSTANCE.register(this);
        FollowerManager.INSTANCE.regCallback(this);
        FollowerManager.INSTANCE.requestNews();
    }


    @Override
    public void onCreate(Bundle arguments, Bundle savedInstanceState) {

    }

    @Override
    public void destroy() {
        FollowerManager.INSTANCE.unregCallback(this);
        AllCountManager.INSTANCE.unregister(this);
        mView.destroy();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        mView.onPageStateChanged(oldPageState, pageState);
    }

    @Override
    public void onNewMessage(Message message) {
        if (message.what == MessageIdConstant.MESSAGE_LAUNCH_DISCOVER_PAGE) {
            mView.switchToPageIndex(MainFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION);
        } else if (message.what == MessageIdConstant.MESSAGE_LAUNCH_FORYOU_SUB) {
            Bundle data = message.getData();
            boolean reRefresh = false;
            if (data != null) {
                reRefresh = data.getBoolean(FeedConstant.BUNDLE_KEY_REFRESH_AGAIN);
            }
            mView.switchToHomePageIndex(reRefresh);
        } else if (message.what == MessageIdConstant.MESSAGE_LAUNCH_HOME_PAGE) {
            mView.switchToPageIndex(MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION);
        } else if (message.what == MessageIdConstant.MESSAGE_LAUNCH_MESSAGE_PAGE) {
            mView.switchToPageIndex(MainFragmentPageSwitcher.FRAGMENT_MESSAGE_POSITION);
        } else if (message.what == MessageIdConstant.MESSAGE_LAUNCH_MINE_PAGE) {
            mView.switchToPageIndex(MainFragmentPageSwitcher.FRAGMENT_MINE_POSITION);
        } else if (message.what == MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT) {
            mView.refreshUserTask(message);
        } else if (message.what == MessageIdConstant.MESSAGE_SYNC_NEW_LEAST_FEED) {
            mView.updateMyDistoryPoint(false);
        } else if (message.what == MessageIdConstant.MESSAGE_SHOW_HOME_REFRESH) {
            mView.updateMyHomeTab(true);
        } else if (message.what == MessageIdConstant.MESSAGE_HIDE_HOME_REFRESH) {
            mView.updateMyHomeTab(false);
        }
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();
    }

    @Override
    public void onActivityResume() {
        mView.onActivityResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showPage() {
    }

    @Override
    public void onTabChange(boolean isDiscoveryTab) {
    }

    @Override
    public void onNewFollowers(int count) {
        mView.updateMyRedPoint(count > 0);
    }

    @Override
    public void onChanged(final int count) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mView.updateRedPoint(count > 0);
            }
        });
    }
}
