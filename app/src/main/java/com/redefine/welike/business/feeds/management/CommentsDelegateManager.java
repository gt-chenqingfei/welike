package com.redefine.welike.business.feeds.management;

import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class CommentsDelegateManager {

    private FeedDetailCommentHeadBean.CommentSortType mSortType;
    private final CommentsManager mCreatedCommentModel;
    private final CommentsManager mHotCommentModel;
    private boolean isAuth;

    public CommentsDelegateManager(FeedDetailCommentHeadBean.CommentSortType sortType, CommentsManager.CommentsProviderCallback commentsProviderCallback) {
        mSortType = sortType;

        mCreatedCommentModel = new CommentsManager(FeedDetailCommentHeadBean.CommentSortType.CREATED);
        mHotCommentModel = new CommentsManager(FeedDetailCommentHeadBean.CommentSortType.HOT);

        mCreatedCommentModel.setListener(commentsProviderCallback);
        mHotCommentModel.setListener(commentsProviderCallback);

        isAuth = AccountManager.getInstance().isLogin();
    }

    public CommentsManager getModel() {
        if (mSortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            return mHotCommentModel;
        }
        return mCreatedCommentModel;
    }

    public void tryRefresh(String pid) {
        getModel().tryRefresh(pid, isAuth);
    }

    public void tryHis(String pid) {
        getModel().tryHis(pid, isAuth);
    }

    public void setSortType(FeedDetailCommentHeadBean.CommentSortType sortType) {
        mSortType = sortType;
    }
}
