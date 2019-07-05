package com.redefine.welike.business.message.ui.contract;

import com.redefine.welike.business.message.ui.adapter.MessageBoxAdapter;

/**
 * Created by gongguan on 2018/2/3.
 */

public interface IMessageBoxContract {

    interface IMessageBoxPresenter {
        boolean canLoadMore();

        void onLoadMore();

        void onRefresh();

        void init();

        void destroy();
    }

    interface IMessageBoxView {
        void setPresenter(IMessageBoxPresenter messageBoxPresenter);

        void setAdapter(MessageBoxAdapter mAdapter);

        void setRefreshEnable(boolean b);

        void showEmptyView();

        void showContent();

        void showErrorView();

        void finishRefresh();

        void autoRefresh();

        void showLoading();

        int getMagicLength();


    }

}
