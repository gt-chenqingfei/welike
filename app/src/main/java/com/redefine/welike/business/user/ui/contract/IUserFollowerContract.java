package com.redefine.welike.business.user.ui.contract;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.user.ui.adapter.UserFollowRecyclerAdapter;
import com.redefine.welike.business.user.ui.presenter.UserFollowerPresenter;
import com.redefine.welike.business.user.ui.view.UserFollowerView;

/**
 * Created by gongguan on 2018/1/22.
 */

public interface IUserFollowerContract {
    interface IUserFollowerPresenter extends IBaseFragmentPagePresenter {

        void onRefresh();

        boolean canLoadMore();

        void onLoadMore();

        void onActivityResume();

        void onActivityPause();

        void onFragmentShow();

        void onFragmentHide();

    }

    interface IUserFollowerView extends IBaseFragmentPageView {

        void setPresenter(IUserFollowerPresenter mPresenter);

        void setAdapter(UserFollowRecyclerAdapter mAdapter, ItemExposeManager callback);

        void autoRefresh();

        void setRefreshEnable(boolean b);

        void finishRefresh();

        void showLoading();

        void showContent();

        void showEmptyView();

        void showErrorView();

        void setRefreshCount(int size);

    }

    class IUserFollowerFactory {

        public static IUserFollowerView createView() {
            return new UserFollowerView();
        }

        public static IUserFollowerPresenter createPresenter(IPageStackManager mPageStackManager, Bundle pageBundle) {
            return new UserFollowerPresenter(mPageStackManager, pageBundle);
        }
    }
}
