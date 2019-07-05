package com.redefine.welike.business.feeds.ui.listener

import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean

open interface IFeedDetailCommentOpListener {
     fun onSwitchCommentOrder(sortType: FeedDetailCommentHeadBean.CommentSortType)
}