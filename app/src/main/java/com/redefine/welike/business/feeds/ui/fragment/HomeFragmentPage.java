package com.redefine.welike.business.feeds.ui.fragment;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BaseFragmentPage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.business.feeds.ui.contract.IMainHomeContract;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

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
@PageName("HomeFragmentPage")
public class HomeFragmentPage extends BaseFragmentPage implements OnRequestPermissionCallback, EasyPermissions.PermissionCallbacks {
    public static final String TAG = "home_page";
    private IMainHomeContract.IMainHomePresenter mPresenter;

    public HomeFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
//        config.isFitSystemWindow = true;
        mPresenter = IMainHomeContract.IMainHomeFactory.createPresenter(stackManager);
        mPresenter.setRequestPermissionCallback(this);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        mPresenter.onCreate(mPageConfig.pageBundle, saveState);
        return mPresenter.createView(mPageStackManager.getLayoutInflater(), container, saveState);
    }

    @Override
    public void attach(ViewGroup container) {
        mPresenter.attach();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_MAIN_HOME);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        super.attach(container);
    }

    @Override
    public void detach(ViewGroup container) {
        mPresenter.detach();
        super.detach(container);
    }

    @Override
    public void destroy(ViewGroup container) {
        mPresenter.destroy();
        super.destroy(container);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        mPresenter.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mPresenter.onActivityPause();
    }

    public void refresh() {
        mPresenter.autoRefresh();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        if (oldPageState == PAGE_STATE_SHOW && pageState != PAGE_STATE_SHOW) {
            mPresenter.hide();
        }
        if (pageState == PAGE_STATE_SHOW){
            mPresenter.show();
        }

    }

    @Override
    public void onBasePageStateChanged(int oldPageState, int pageState) {
        super.onBasePageStateChanged(oldPageState, pageState);
        if (pageState == PAGE_STATE_SHOW){
            mPresenter.show();
        }

    }

    @Override
    public void onNewMessage(Message message) {
        super.onNewMessage(message);
        if (message.what == MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT) {
//            mPresenter.refreshUserTask();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onRequestPermission(@NotNull String message, int requestCode, @NotNull String permission) {
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.OTHER);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //do nothing
    }
}
