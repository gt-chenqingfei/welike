package com.redefine.welike.business.feeds.ui.contract;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.feeds.ui.presenter.FeedDetailForwardPresenter;
import com.redefine.welike.business.feeds.ui.view.FeedDetailForwardView;

/**
 * Created by liwb on 2018/1/12.
 */

public interface IFeedDetailForwardContract {

    interface IFeedDetailForwardPresenter extends IBaseFragmentPagePresenter, ILoadMoreDelegate {
        void onRefresh();

        void onNewForward(PostBase postBase);

        void refreshPostBase(PostBase postBase);
    }

    interface IFeedDetailForwardView extends IBaseFragmentPageView, ILoadMoreDelegate {

        void setAdapter(RecyclerView.Adapter mAdapter);

        void setPresenter(IFeedDetailForwardPresenter forwardPresenter);

        void showContent();

        void showLoading();

        void showErrorView();

        void showEmptyView();

        void showLoginSnackBar(int type);
    }

    class IFeedDetailForwardFeedFactory {
        public static IFeedDetailForwardPresenter createPresenter(IPageStackManager pageStackManager, IRefreshDelegate onRefreshDelegate, Bundle pageBundle) {
            return new FeedDetailForwardPresenter(pageStackManager, onRefreshDelegate, pageBundle);
        }

        public static IFeedDetailForwardView createView() {
            return new FeedDetailForwardView();
        }
    }
}
