package com.redefine.welike.business.feeds.ui.contract;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.welike.business.common.GuideManager;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.business.feeds.ui.presenter.MainHomePresenter;
import com.redefine.welike.business.feeds.ui.view.MainHomeView;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;

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

public interface IMainHomeContract {

    interface IMainHomePresenter extends IBasePresenter, ILoadMoreDelegate {
        void onCreate(Bundle arguments, Bundle savedInstanceState);

        View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

        void onRefresh();

        void attach();

        void detach();

        void destroy();

        void toDiscover();

        void autoRefresh();

        void onActivityResume();

        void onActivityPause();

        void onFragmentShow();

        void onFragmentHide();
//        void refreshUserTask();

        void hide();
        void show();
        void hideRefresh();
        void showRefresh();

        void setRequestPermissionCallback(OnRequestPermissionCallback callback);
    }

    interface IMainHomeView extends IBaseView {
        View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

        void setPresenter(IMainHomePresenter presenter);

        void setAdapter(RecyclerView.Adapter mAdapter);

        void finishRefresh(boolean isSuccess);

        void autoRefresh();

        void setRefreshEnable(boolean b);

        void setRefreshCount(int size);

        void autoPlayVideo();

        void onActivityResume();

        void onActivityPause();

        void onFragmentShow();

        void onFragmentHide();

        void destroy();

        void showLoading();

        void showErrorView();

        void showEmptyView();

        void showContentView();

        void scrollToTop();

        boolean canScroll();

        void showGuide(@GuideManager.GuideType String guide, View view);

        void hideGuide(boolean click);

        void setRecyclerViewManager(ItemExposeManager manager);

        Context getContext();

        void onHeaderChange();

//        void checkFixInfo();
//
//        void hideFixInfo();
    }

    class IMainHomeFactory {
        public static IMainHomePresenter createPresenter(IPageStackManager pageStackManager) {
            return new MainHomePresenter(pageStackManager);

        }

        public static IMainHomeView createView() {
            return new MainHomeView();
        }
    }

}
