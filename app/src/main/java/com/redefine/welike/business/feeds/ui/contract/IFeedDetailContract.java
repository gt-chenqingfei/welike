package com.redefine.welike.business.feeds.ui.contract;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.presenter.FeedDetailPresenter;
import com.redefine.welike.business.feeds.ui.view.FeedDetailView;
import com.redefine.welike.commonui.widget.UserFollowBtn;

/**
 * Created by liwb on 2018/1/11.
 */

public interface IFeedDetailContract {

    interface IFeedDetailPresenter extends IBasePagePresenter {

        void onCommentFeed(Context context, PostBase mFeed);

        void onCommentFeed(Context context, PostBase mFeed, boolean showEmoji);

        void onForwardFeed(Context context, PostBase mFeed);

        void onReport(Context context, String pid, String uid, String reason);

        Bundle onSaveInstanceState();

        void onNewMessage(Message message);

        void onClickErrorView();

        void onBrowseFeedDetailClick(int tye, boolean isShow, int showType);

        void onActivityResume();

        void onActivityPause();

        void onActivityDestroy();

        void onFollowUser(PostBase postBase, final Context context);

        void initFeedFollowBtn(PostBase postBase, UserFollowBtn userFollowBtn);

        void checkShowFollowBtn(Context context, PostBase postBase);
    }

    interface IFeedDetailView extends IBasePageView {

        void showErrorView();

        void setPresenter(IFeedDetailPresenter presenter);

        void onFeedFollowChange();

        void onNewComment(Comment comment);

        void onNewForward(PostBase postBase);

        void initViewDetail(PostBase mPostBase, int mIndex);

        void showLoadingView();

        boolean isShowContent();

        void onPostDelete();

        void updatePostToView(PostBase mPostBase);

        void onActivityResume();

        void onActivityPause();

        void onActivityDestroy();

        void showLoginSnackBar(int showType);

        void refreshFollow(boolean follow);
    }

    class FeedDetailFactory {

        public static IFeedDetailPresenter createPresenter(IPageStackManager pageStackManager, Bundle pageBundle) {
            return new FeedDetailPresenter(pageStackManager, pageBundle);
        }

        public static IFeedDetailView createView(IPageStackManager pageStackManager) {
            return new FeedDetailView(pageStackManager);
        }

        public static IFeedDetailView createView(IPageStackManager pageStackManager, Bundle pageBundle) {
            return new FeedDetailView(pageStackManager, pageBundle);
        }
    }

}
