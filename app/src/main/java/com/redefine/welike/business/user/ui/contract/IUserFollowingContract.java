package com.redefine.welike.business.user.ui.contract;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.user.ui.adapter.UserFollowingRecyclerAdapter;
import com.redefine.welike.business.user.ui.presenter.UserFollowingPresenter;
import com.redefine.welike.business.user.ui.view.UserFollowingView;

/**
 * Created by gongguan on 2018/1/12.
 */

public interface IUserFollowingContract {
    interface IUserFollowingPresenter extends IBaseFragmentPagePresenter {

        void onRefresh();

        boolean canLoadMore();

        void onLoadMore();

        void onActivityResume();

        void onActivityPause();

        void onFragmentShow();

        void onFragmentHide();
    }

    interface IUserFollowingView extends IBaseFragmentPageView {
        void setPresenter(IUserFollowingPresenter iUserFollowingPresenter);

        void setAdapter(UserFollowingRecyclerAdapter mAdapter, ItemExposeManager callback);

        void setRefreshEnable(boolean b);

        void finishRefresh();

        boolean canLoadMore();

        void onLoadMore();

        void autoRefresh();

        void showLoading();

        void showContent();

        void showEmptyView();

        void showErrorView();

        void setRefreshCount(int size);

    }

    class IUserFollowingFactory {

        public static IUserFollowingView createView() {
            return new UserFollowingView();
        }

        public static IUserFollowingPresenter createPresenter(IPageStackManager mPageStackManager, Bundle pageBundle) {
            return new UserFollowingPresenter(mPageStackManager, pageBundle);
        }
    }

}
