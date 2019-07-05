package com.redefine.welike.business.feeds.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpFragmentPage;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailLikeContract;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.user.management.bean.User;

/**
 * Created by liwenbo on 2018/2/2.
 */
@PageName("FeedDetailLikeFragmentPage")
public class FeedDetailLikeFragmentPage extends MvpFragmentPage<IFeedDetailLikeContract.IFeedDetailLikePresenter> implements IRefreshDelegate {
    private final IRefreshDelegate mRefreshDelegate;

    public FeedDetailLikeFragmentPage(IRefreshDelegate refreshDelegate, IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mRefreshDelegate = refreshDelegate;
    }

    @Override
    protected IFeedDetailLikeContract.IFeedDetailLikePresenter createPresenter() {
        return IFeedDetailLikeContract.FeedDetailLikeFactory.createPresenter(mPageStackManager, this, mPageConfig.pageBundle);
    }

    @Override
    public void startRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    public void stopRefresh() {
        mRefreshDelegate.stopRefresh();
    }

    @Override
    public void setRefreshEnable(boolean isEnable) {
        mRefreshDelegate.setRefreshEnable(isEnable);
    }

    public void onNewLike(User user) {
        mPresenter.onNewLike(user);
    }

    public void refreshPostBase(PostBase postBase) {
        mPresenter.refreshPostBase(postBase);
    }
}
