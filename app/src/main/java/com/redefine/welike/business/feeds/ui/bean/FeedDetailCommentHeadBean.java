package com.redefine.welike.business.feeds.ui.bean;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class FeedDetailCommentHeadBean extends BaseHeaderBean {

    private CommentSortType mSortType = CommentSortType.CREATED;

    public FeedDetailCommentHeadBean(CommentSortType sortType) {
        mSortType = sortType;
    }

    public CommentSortType getSortType() {
        return mSortType;
    }

    public void setmSortType(CommentSortType mSortType) {
        this.mSortType = mSortType;
    }

    public static enum CommentSortType {
        CREATED, HOT
    }
}
