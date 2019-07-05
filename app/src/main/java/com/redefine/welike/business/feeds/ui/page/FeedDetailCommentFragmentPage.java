package com.redefine.welike.business.feeds.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpFragmentPage;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailCommentContract;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;

/**
 * Created by liwenbo on 2018/2/2.
 */
@PageName("FeedDetailCommentFragmentPage")
public class FeedDetailCommentFragmentPage extends MvpFragmentPage<IFeedDetailCommentContract.IFeedDetailCommentPresenter> implements IRefreshDelegate {
    private final IRefreshDelegate mRefreshDelegate;

    public FeedDetailCommentFragmentPage(IRefreshDelegate refreshDelegate, IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mRefreshDelegate = refreshDelegate;
    }

    @Override
    protected IFeedDetailCommentContract.IFeedDetailCommentPresenter createPresenter() {
        return IFeedDetailCommentContract.FeedDetailCommentFactory.createPresenter(mPageStackManager, this, mPageConfig.pageBundle);
    }

    @Override
    public void startRefresh() {
        if (mPresenter != null) {
            mPresenter.onRefresh();
        }
    }


    public void setSwitchCommentOrder(FeedDetailCommentHeadBean.CommentSortType sortType) {
        if (mPresenter != null) {
            mPresenter.onSwitchCommentOrder(sortType);
        }
    }

    @Override
    public void stopRefresh() {
        mRefreshDelegate.stopRefresh();
    }

    @Override
    public void setRefreshEnable(boolean isEnable) {
        mRefreshDelegate.setRefreshEnable(isEnable);
    }

    public void onNewComment(Comment comment) {
        if (mPresenter != null) {
            mPresenter.onNewComment(comment);
        }
    }

    public void refreshPostBase(PostBase postBase) {
        mPresenter.refreshPostBase(postBase);
    }
}
