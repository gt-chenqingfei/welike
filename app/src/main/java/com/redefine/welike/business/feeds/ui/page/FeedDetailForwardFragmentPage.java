package com.redefine.welike.business.feeds.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpFragmentPage;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailForwardContract;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;

/**
 * Created by liwenbo on 2018/2/2.
 */
@PageName("FeedDetailForwardFragmentPage")
public class FeedDetailForwardFragmentPage extends MvpFragmentPage<IFeedDetailForwardContract.IFeedDetailForwardPresenter> implements IRefreshDelegate {
    private final IRefreshDelegate mRefreshDelegate;

    public FeedDetailForwardFragmentPage(IRefreshDelegate refreshDelegate, IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        mRefreshDelegate = refreshDelegate;
    }

    @Override
    protected IFeedDetailForwardContract.IFeedDetailForwardPresenter createPresenter() {
        return IFeedDetailForwardContract.IFeedDetailForwardFeedFactory.createPresenter(mPageStackManager,this, mPageConfig.pageBundle);
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

    public void onNewForward(PostBase postBase) {
        mPresenter.onNewForward(postBase);
    }

    public void refreshPostBase(PostBase postBase) {
        mPresenter.refreshPostBase(postBase);
    }
}
