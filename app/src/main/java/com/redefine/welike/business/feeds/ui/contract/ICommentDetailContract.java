package com.redefine.welike.business.feeds.ui.contract;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.presenter.CommentDetailPresenter;
import com.redefine.welike.business.feeds.ui.view.CommentDetailView;

/**
 * Created by MR on 2018/1/15.
 */

public interface ICommentDetailContract {

    interface ICommentDetailPresenter extends IBasePagePresenter, ILoadMoreDelegate {

        void doForwardComment(Context context, Comment comment);

        void doCommentReply(Context context, Comment mComment);

        void onRefresh();

        void onNewMessage(Message message);

        void deleteMainComment();

    }

    interface ICommentDetailView extends IBasePageView {

        void setPresenter(ICommentDetailPresenter presenter);

        void showErrorView();

        void setAdapter(LoadMoreFooterRecyclerAdapter mAdapter);

        void setMainComment(Comment mComment, PostBase postBase);

        void onClickLikeBtn();

        void showLoading();

        void showContent();

        void showEmptyView();

        void scrollToTop();
    }

    class CommentDetailFactory {
        public static ICommentDetailPresenter createPresenter(Activity activity, Bundle pageBundle) {
            return new CommentDetailPresenter(activity, pageBundle);
        }

        public static ICommentDetailView createView() {
            return new CommentDetailView();
        }
    }
}
