package com.redefine.welike.business.search.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpFragmentPage;
import com.redefine.welike.business.search.ui.contract.ISearchPostContract;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;

/**
 * Created by liwenbo on 2018/2/11.
 */
@PageName("SearchPostFragmentPage")
public class SearchPostFragmentPage extends MvpFragmentPage<ISearchPostContract.ISearchPostPresenter> implements SearchFragmentPageSwitcher.ISearchRefreshDelegate {

    public SearchPostFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected ISearchPostContract.ISearchPostPresenter createPresenter() {
        return ISearchPostContract.SearchPostFactory.createPresenter(mPageStackManager, mPageConfig.pageBundle);
    }

    @Override
    public void onRefresh(String searchText) {
        mPresenter.onRefresh(searchText);
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        mPresenter.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mPresenter.onActivityPause();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        mPresenter.onPageStateChanged(oldPageState, pageState);
    }
}
