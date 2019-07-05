package com.redefine.welike.business.search.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpBaseTitlePage;
import com.redefine.welike.business.search.ui.contract.ISearchSugContract;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by liwenbo on 2018/3/13.
 */
@PageName("SearchSugPage")
public class SearchSugPage extends MvpBaseTitlePage<ISearchSugContract.ISearchSugPresenter> {

    public SearchSugPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected ISearchSugContract.ISearchSugPresenter createPresenter() {
        return ISearchSugContract.SearchSugFactory.createPresenter(mPageStackManager, mPageConfig.pageBundle);
    }

    @Override
    public void onPageScrollStart() {
        mPresenter.hideInputMethod();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        if (pageState == BasePage.PAGE_STATE_HIDE) {
            mPresenter.hideInputMethod();
        } else if (pageState == BasePage.PAGE_STATE_SHOW){
            EventLog1.Search.report1(EventLog1.Search.FromPage.DISCOVER);
        }
    }
}
