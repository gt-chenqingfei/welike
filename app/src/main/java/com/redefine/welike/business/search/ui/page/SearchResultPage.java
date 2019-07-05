package com.redefine.welike.business.search.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpBaseTitlePage;
import com.redefine.welike.business.search.ui.contract.ISearchPageContract;

/**
 * Created by liwenbo on 2018/2/11.
 */
@PageName("SearchResultPage")
public class SearchResultPage extends MvpBaseTitlePage<ISearchPageContract.ISearchResultPagePresenter> {

    public SearchResultPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected ISearchPageContract.ISearchResultPagePresenter createPresenter() {
        return ISearchPageContract.SearchResultPageFactory.createPresenter(mPageStackManager, mPageConfig.pageBundle);
    }

    @Override
    public void onPageScrollStart() {
        super.onPageScrollStart();
        mPresenter.onPageScrollStart();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        mPresenter.onPageStateChanged(oldPageState, pageState);
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

}
