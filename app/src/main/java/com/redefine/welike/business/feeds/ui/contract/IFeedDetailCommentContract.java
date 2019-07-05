package com.redefine.welike.business.feeds.ui.contract;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.foundation.mvp.IBaseFragmentPagePresenter;
import com.redefine.foundation.mvp.IBaseFragmentPageView;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.feeds.ui.presenter.FeedDetailCommentPresenter;
import com.redefine.welike.business.feeds.ui.view.FeedDetailCommentView;

/**
 * Created by liwb on 2018/1/12.
 */

public interface IFeedDetailCommentContract {

    interface IFeedDetailCommentPresenter extends IBaseFragmentPagePresenter, ILoadMoreDelegate {

        void onRefresh();

        void onNewComment(Comment comment);

        void deleteComment(String value);

        void refreshPostBase(PostBase postBase);

        void onSwitchCommentOrder(FeedDetailCommentHeadBean.CommentSortType sortType);
    }

    interface IFeedDetailCommentView extends IBaseFragmentPageView, ILoadMoreDelegate {

        void setAdapter(RecyclerView.Adapter adapter);

        void setPresenter(IFeedDetailCommentPresenter commentPresenter);

        void showContent();

        void showLoading();

        void showEmptyView();

        void showErrorView();

        void scrollToTop();

        void showLoginSnackBar(int type);


    }

    class FeedDetailCommentFactory {
        public static IFeedDetailCommentPresenter createPresenter(IPageStackManager pageStackManager, IRefreshDelegate delegate, Bundle pageBundle) {
            return new FeedDetailCommentPresenter(pageStackManager, delegate, pageBundle);
        }

        public static IFeedDetailCommentView createView() {
            return new FeedDetailCommentView();
        }
    }
}
