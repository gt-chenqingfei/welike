package com.redefine.welike.business.feeds.management;

import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;

/**
 * Created by zhl on 2018/4/18.
 */

public class CommentDelegateManager {

    private FeedDetailCommentHeadBean.CommentSortType mSortType;
    private final CommentDetailManager mCreatedCommentModel;
    private final CommentDetailManager mHotCommentModel;

    public CommentDelegateManager(FeedDetailCommentHeadBean.CommentSortType sortType) {
        mSortType = sortType;

        mCreatedCommentModel = new CommentDetailManager(FeedDetailCommentHeadBean.CommentSortType.CREATED);
        mHotCommentModel = new CommentDetailManager(FeedDetailCommentHeadBean.CommentSortType.HOT);

//        mCreatedCommentModel.setListener(commentsProviderCallback);
//        mHotCommentModel.setListener(commentsProviderCallback);
    }

    public CommentDetailManager getModel() {
        if (mSortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            return mHotCommentModel;
        }
        return mCreatedCommentModel;
    }

    public CommentDetailManager getModel(FeedDetailCommentHeadBean.CommentSortType sortType) {
        if (sortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            return mHotCommentModel;
        }
        return mCreatedCommentModel;
    }

    public void tryRefresh(String pid) {
        getModel().tryRefresh(pid);
    }

    public void tryHis(String pid) {
        getModel().tryHis(pid);
    }

    public void setSortType(FeedDetailCommentHeadBean.CommentSortType sortType) {
        mSortType = sortType;
    }
}
