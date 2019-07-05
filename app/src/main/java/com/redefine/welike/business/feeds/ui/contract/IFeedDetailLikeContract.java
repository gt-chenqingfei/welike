package com.redefine.welike.business.feeds.ui.contract;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.feeds.ui.presenter.FeedDetailLikePresenter;
import com.redefine.welike.business.feeds.ui.view.FeedDetailLikeView;
import com.redefine.welike.business.user.management.bean.User;

/**
 * Created by MR on 2018/1/19.
 */

public interface IFeedDetailLikeContract {

    interface IFeedDetailLikePresenter extends IBaseFragmentPagePresenter, ILoadMoreDelegate {
        void onRefresh();

        void onNewLike(User user);

        void refreshPostBase(PostBase postBase);
    }

    interface IFeedDetailLikeView extends IBaseFragmentPageView {
        void setAdapter(LoadMoreFooterRecyclerAdapter adapter);

        void setPresenter(IFeedDetailLikePresenter likePresenter);

        void showContent();

        void showLoading();

        void showEmptyView();

        void showErrorView();
    }

    class FeedDetailLikeFactory {
        public static IFeedDetailLikePresenter createPresenter(IPageStackManager pageStackManager, IRefreshDelegate refreshDelegate, Bundle pageBundle) {
            return new FeedDetailLikePresenter(pageStackManager, refreshDelegate, pageBundle);
        }

        public static IFeedDetailLikeView createView() {
            return new FeedDetailLikeView();
        }
    }
}
