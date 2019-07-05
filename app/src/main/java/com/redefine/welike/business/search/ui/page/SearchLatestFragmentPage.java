package com.redefine.welike.business.search.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpFragmentPage;
import com.redefine.welike.business.search.ui.contract.ISearchLatestContract;
import com.redefine.welike.business.search.ui.contract.ISearchPageContract;

/**
 * Created by liwenbo on 2018/3/5.
 */
@PageName("SearchLatestFragmentPage")
public class SearchLatestFragmentPage extends MvpFragmentPage<ISearchLatestContract.ISearchLatestPresenter> implements SearchFragmentPageSwitcher.ISearchRefreshDelegate {

    public SearchLatestFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected ISearchLatestContract.ISearchLatestPresenter createPresenter() {
        return ISearchLatestContract.SearchLatestFactory.createPresenter(mPageStackManager, mPageConfig.pageBundle);
    }

    @Override
    public void onRefresh(String searchText) {
        mPresenter.onRefresh(searchText);
    }

    public void setBasePresenter(ISearchPageContract.ISearchResultPagePresenter presenter) {
        mPresenter.setBasePresenter(presenter);
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