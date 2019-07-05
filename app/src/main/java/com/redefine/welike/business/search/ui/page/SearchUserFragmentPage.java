package com.redefine.welike.business.search.ui.page;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.frameworkmvp.page.MvpFragmentPage;
import com.redefine.welike.business.search.ui.contract.ISearchUserContract;

/**
 * Created by liwenbo on 2018/2/11.
 */
@PageName("SearchUserFragmentPage")
public class SearchUserFragmentPage extends MvpFragmentPage<ISearchUserContract.ISearchUserPresenter> implements SearchFragmentPageSwitcher.ISearchRefreshDelegate {

    public SearchUserFragmentPage(IPageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
    }

    @Override
    protected ISearchUserContract.ISearchUserPresenter createPresenter() {
        return ISearchUserContract.SearchUserFactory.createPresenter(mPageStackManager, mPageConfig.pageBundle);
    }

    @Override
    public void onRefresh(String searchText) {
        mPresenter.onRefresh(searchText);
    }
}
