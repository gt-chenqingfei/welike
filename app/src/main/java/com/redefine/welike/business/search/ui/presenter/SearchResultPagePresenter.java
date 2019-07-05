package com.redefine.welike.business.search.ui.presenter;

import android.os.Bundle;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.foundation.framework.Event;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.feeds.management.SearchMovieManager;
import com.redefine.welike.business.search.ui.bean.SearchMovieBean;
import com.redefine.welike.business.search.ui.constant.SearchConstant;
import com.redefine.welike.business.search.ui.contract.ISearchPageContract;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchResultPagePresenter extends MvpTitlePagePresenter<ISearchPageContract.ISearchResultPageView> implements ISearchPageContract.ISearchResultPagePresenter,SearchMovieManager.SearchMovieManagerListener {

    private  SearchMovieManager mModel;

    public SearchResultPagePresenter(IPageStackManager pageStackManager, Bundle pageBundle) {
        super(pageStackManager, pageBundle);
        mModel=new SearchMovieManager();
        mModel.register(this);
    }

    @Override
    protected ISearchPageContract.ISearchResultPageView createPageView() {
        return ISearchPageContract.SearchResultPageFactory.createView(mPageStackManager, this, mPageBundle);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    public void onPageScrollStart() {
        mView.hideInputMethod();
    }

    @Override
    public void switchToPeoplePage() {
        mView.switchToPeoplePage();
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        if (pageState == BasePage.PAGE_STATE_HIDE) {
            mView.hideInputMethod();
        }
    }

    @Override
    public void goSugPage(String mSearchQuery) {
        onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putString(SearchConstant.SEARCH_QUERY, mSearchQuery);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SEARCH_SUG_EVENT, bundle));
    }

    @Override
    public void getMovie(String mSearchQuery) {
        mModel.search(mSearchQuery);

    }

    @Override
    public void onActivityResume() {
        mView.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();
    }

    @Override
    public void destroy() {
        super.destroy();
        mModel.unregister(this);
    }

    @Override
    public void onBackPressed() {
        mPageStackManager.getActivity().finish();
    }

    @Override
    public void onNewSearchResult(List<SearchMovieBean> contents, int errCode) {
        if(null!=contents&&contents.size()>0){
            mView.initMovieCard(contents.get(0));
        }

    }
}
